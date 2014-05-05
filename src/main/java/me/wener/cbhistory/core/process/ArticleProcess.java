package me.wener.cbhistory.core.process;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.DiscoverArticleEvent;
import me.wener.cbhistory.core.event.ExceptionEvent;
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
    Pattern regGV = Pattern.compile("^GV\\.DETAIL[^\\{]+(?<data>\\{[^\\}]+})", Pattern.MULTILINE);

    private static final Pattern regMatchId = Pattern.compile("articles/(?<id>\\d+)");

    @PostConstruct
    public void init()
    {
        log.info("完成处理中心的初始化.");
    }

    @Subscribe
    @AllowConcurrentEvents
    public void getHtmlFromInternet(TryDiscoverArticleByUrlEvent e)
    {
        final String url = e.getUrl();
        HttpRequest request = HttpRequest.get(url);
        HttpResponse response = insureResponse(request);
        if (response == null)
        {
            log.error("获取响应失败,请求的url为: "+e.getUrl());
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
            log.debug("解析出内容中的文章 ID " + e);
        final String content = e.getContent();

        Set<String> ids = Sets.newHashSet();
        Matcher matcher = regMatchId.matcher(content);
        while (matcher.find())
        {
            String id = matcher.group("id");
            ids.add(id);
        }

        // 将发现的所有 ID 发布出去
        for (String id : ids)
            Events.post(new TryFoundArticleEvent(id));
    }

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
            log.error("解析文章 ID 的时候出现异常,不再尝试获取该文章. ID:" + e.getArticleId());
            return;
        }
        // endregion

        // 判断文章是否过期
        Article article = articleRepo.findOne(sid);
        if (article != null && article.getDate() != null && daysAgoFromNow(article.getDate()) > ARTICLE_EXPIRED_DAYS)
        {
            if (article.getLastUpdateDate() == null)
            {
                if (log.isInfoEnabled())
                    log.info("虽然文章已过期,但是由于尚未获取过一次,将尝试获取文章. " + article);
            } else
            {
                if (log.isDebugEnabled())
                    log.debug("发现的文章已经过期. " + article);
                return;
            }
        }

        String url = "http://www.cnbeta.com/articles/%s.htm";
        url = String.format(url, e.getArticleId());

        HttpResponse response = insureResponse(HttpRequest.get(url), 3);
        if (response == null)
        {
            log.error("获取响应失败,请求的url为: "+url);
            return;
        }

        // 确保返回的状态正确
        if (response.statusCode() != 200)
        {
            final String format = "获取到的响应返回状态异常: %s. 请求路径为: %s 将停止接下来的操作.";
            String msg = String.format(format, response.statusCode(), url);
            Events.post(new ExceptionEvent(new Exception(msg)));
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
                    log.info("发现新文章: "+ article);
            }else
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
            // 目前的时间格式 2014-04-30 11:57:01
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            article.setDate(CodecUtils.jsonToDate(bar.$(".date").text()));

            // 这两个值是动态生成的,要在获取到评论后才能赋值
//            article.setReadCount(Integer.parseInt(bar.$("#view_num").text()));
//            article.setDiscussCount(Integer.parseInt(bar.$(".comment_num").text()));
        }

        // 先将当前状态保存
        article = articleRepo.save(article);

        // 更新评论
        Events.post(new TryUpdateCommentEvent(article));
    }

}
