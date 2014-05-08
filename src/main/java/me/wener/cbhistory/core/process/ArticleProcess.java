package me.wener.cbhistory.core.process;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Named;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.DiscoverArticleEvent;
import me.wener.cbhistory.core.event.FoundArticleEvent;
import me.wener.cbhistory.core.event.TryDiscoverArticleByUrlEvent;
import me.wener.cbhistory.core.event.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.TryUpdateCommentEvent;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.util.CodecUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对文章的相关处理
 */
@Named
@Slf4j
public class ArticleProcess extends CommonProcess
{
    // 匹配出Gv信息,放在 data 分组
    private static final Pattern regGV = Pattern.compile("^GV\\.DETAIL[^\\{]+(?<data>\\{[^\\}]+})", Pattern.MULTILINE);
    private static final Pattern regMatchId = Pattern.compile("articles/(?<id>\\d+)");

    @Subscribe
    @AllowConcurrentEvents
    public void getHtmlFromInternet(TryDiscoverArticleByUrlEvent e)
    {
        final String url = e.getUrl();
        HttpRequest request = HttpRequest.get(url);
        HttpResponse response = insureResponse(request);
        if (response == null)
        {
            log.error("获取响应失败,请求的url为: {}", e.getUrl());
            return;
        }
        DiscoverArticleEvent event = new DiscoverArticleEvent(response.bodyText());
        Events.post(event);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void discoverArticleIdInContent(DiscoverArticleEvent e)
    {
        if (log.isDebugEnabled())
            log.debug("解析出内容中的文章 ID {}", e);
        final String content = e.getContent();

        Set<String> ids = Sets.newHashSet();
        Matcher matcher = regMatchId.matcher(content);
        while (matcher.find())
        {
            String id = matcher.group("id");
            ids.add(id);
        }

        // 将发现的所有 ID 发布出去
        // 这里的并发太恐怖了,需要缩小范围
        Article article = null;
        for (String id : ids)
        {
            try
            {
                article = articleRepo.findOne(Long.parseLong(id));
            } catch (NumberFormatException ignored){continue;}

            if (article != null)
            {
                if (isArticleNeedUpdate(article))
                {
                    log.debug("发现的文章已经存在,需要更新. {}", article);
                    Events.post(new TryUpdateCommentEvent(article));
                }else
                    log.debug("发现的文章已经存在,尚且不需要更新. {}", article);
            } else
                Events.post(new TryFoundArticleEvent(id));
        }
    }

    /**
     * 下载文章基本信息,主要是处理发现的新文章
     */
    @Subscribe
    @AllowConcurrentEvents
    public void getArticleFromInternet(TryFoundArticleEvent e)
    {
        if (log.isDebugEnabled())
            log.debug("发现文章, 尝试获取 " + e);

        Long sid = null;
        // region 获取ID,并确保 ID 的正确性
        try
        {
            sid = Long.parseLong(e.getArticleId());
        } catch (NumberFormatException ex)
        {
            log.error("解析文章 ID 的时候出现异常,不再尝试获取该文章. sid: {}", e.getArticleId());
            return;
        }
        // endregion

        Article article = articleRepo.findOne(sid);

        // 如果文章已经存在,将不再尝试下载,而是直接尝试更新评论
        if (article != null && article.getSn() != null)
        {
            log.info("文章已经存在,不需要继续下载,将尝试更新评论. sid: {}", sid);
            Events.post(new TryUpdateCommentEvent(article));
            return;
        }

        // 下载文章页
        String url = "http://www.cnbeta.com/articles/%s.htm";
        url = String.format(url, e.getArticleId());

        HttpResponse response = insureResponse(HttpRequest.get(url), 3);
        if (response == null)
        {
            log.error("获取响应失败,请求的url为: {}", url);
            return;
        }

        // 如果返回状态不正确,则不再处理
        if (response.statusCode() != 200)
        {
            log.error("获取到的响应返回状态异常,将停止接下来的操作. 请求路径为: {} 响应对象: {}. "
                    , url, response);
            return;
        }

        // 这里必须转换下编码,目前使用的 UTF-8
        String text = new String(response.bodyBytes(), Charsets.UTF_8);

        Events.post(new FoundArticleEvent(sid, text));// 发布发现文章事件
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void parseArticle(FoundArticleEvent e) throws Exception
    {
        final String content = e.getContent();
        final Jerry doc = Jerry.jerry(content);

        Article article = articleRepo.findOne(e.getArticleId());

        // 解析出文章的详细信息
        Matcher matcher = regGV.matcher(content);
        if (matcher.find())
        {
            String data = matcher.group("data");
            // 判断是否为新的文章
            if (article == null)
            {
                article = gson.fromJson(data, Article.class);
                if (log.isInfoEnabled())
                    log.info("发现新文章: {}", article);
            } else
                CodecUtils.jsonMergeTo(data, article);
        } else
            throw new Exception("无法匹配出 GvDetail 的内容.");

        // 解析 HTML
        article.setTitle(doc.$("#news_title").text().trim());
        String intro = CharMatcher.anyOf("\r\n").removeFrom(doc.$(".introduction p").text());
        article.setIntroduction(intro);

        {
            Jerry bar = doc.$(".title_bar");
            String tmp = bar.$(".where").text().trim();
            tmp = tmp.substring(tmp.indexOf("：") + 1);// 替换前缀
            article.setSource(tmp);
            article.setDate(CodecUtils.jsonToDate(bar.$(".date").text()));
        }

        // 先将当前状态保存
        article = articleRepo.save(article);

        // 更新评论
        Events.post(new TryUpdateCommentEvent(article));
    }


    private List<Article> filterExpiredArticleById(Iterable<Long> articles)
    {
        Set<Article> set = Sets.newHashSet();
        for (Long id : articles)
        {
            Article article = articleRepo.findOne(id);
            if (article == null)
                continue;
            set.add(article);
        }
        return filterExpiredArticle(set);
    }

    private List<Article> filterExpiredArticle(Iterable<Article> articles)
    {
        List<Article> list = Lists.newArrayList();

        for (Article article : articles)
        {
            if (isArticleNeedUpdate(article))
            {
                list.add(article);
            }
        }

        return list;
    }

    /**
     * 并发控制,请求尝试处理该文章,如果能处理,则返回 true,如果不能处理,则代表有其他进程在处理
     */
    private boolean tryProcess(Long articleId)
    {
        return false;
    }

    /**
     * 完成处理文章
     */
    private boolean doneProcess(Long articleId)
    {
        return false;
    }
}
