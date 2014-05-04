package me.wener.cbhistory.core;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.gson.Gson;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.*;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.Comment;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.repositories.ArticleRepository;
import me.wener.cbhistory.repositories.CommentRepository;
import me.wener.cbhistory.repositories.RawDataRepository;
import me.wener.cbhistory.util.CodecUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class ProcessCenter
{
    /**
     * 文章过期的时间,单位为 天
     */
    public static final int ARTICLE_EXPIRED_DAYS = 7;// 看到有 6 天后还有评论的,所以暂且设置为7天
    /**
     * 评论更新的间隔,单位为 分钟
     */
    public static final int COMMENT_UPDATE_PERIOD_MIN = 60 * 5;// 5 小时

    private static ProcessCenter instance;

    @Inject
    private EventScheduler scheduler;

    /**
     * 用来持久化大量对象,自己控制事务,是非共享的 em
     */
    private EntityManager em;

    @Inject
    ArticleRepository articleRepo;
    @Inject
    RawDataRepository rawCommentRepo;
    @Inject
    CommentRepository commentRepo;

    // 匹配出Gv信息,放在 data 分组
    Pattern regGV = Pattern.compile("^GV\\.DETAIL[^\\{]+(?<data>\\{[^\\}]+})", Pattern.MULTILINE);

    private static final Pattern regMatchId = Pattern.compile("articles/(?<id>\\d+)");


    private static Gson gson = new Gson();

    public static ProcessCenter getInstance()
    {
        if (instance == null)
            instance = new ProcessCenter();
        return instance;
    }

    @Inject
    public void needNonSharedEm(EntityManagerFactory emf)
    {
        em = emf.createEntityManager();
    }

    // region 服务性的事件处理
    @Subscribe
    @AllowConcurrentEvents
    public void unwrapStartEvent(StartEvent e)
    {
        Event event = e.getEvent();
        checkNotNull(event);
        log.debug("开始事件 " + e);
        Events.post(event);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void logFinishedEvent(FinishEvent e)
    {
        Event event = e.getEvent();
        checkNotNull(event);
        log.debug("结束事件 " + e);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void catchDeadEvent(DeadEvent e)
    {
        final String format = "检测到未处理的事件 event:%s source:%s";
        log.warn(String.format(format, e.getEvent(), e.getSource()));
    }

    @Subscribe
    public void handleException(ExceptionEvent e)
    {
        if (log.isErrorEnabled())
        {
            final String format = "当前事件为: %s\n当前订阅者为: %s\n订阅方法为: %s\nEventBus为: %s\n";
            String msg = "\n";
            log.error("检测到异常 :", e.getException());

            msg += "===================================\n";
            msg += "异常详细信息:\n";

            SubscriberExceptionContext ctx = e.getContext();
            if (ctx != null)
            {
                msg += String.format(format, ctx.getEvent(), ctx.getSubscriber(), ctx.getSubscriberMethod(), ctx.getEventBus());
            } else
                msg += "无上下文信息.\n";

            if (e.getExtra() != null)
            {
                msg += String.format("附加信息为: %s \n", e.getExtra());
            } else
                msg += "无附加信息.\n";

            msg += "===================================";
            log.error(msg);
        }
    }
// endregion

    @Subscribe
    public void dispatchDiscoverEvent(TryDiscoverArticleEvent e)
    {
        // 不要再使用该方法,因为这个一次性请求这么多页面
        // 并发太高,可能会导致各种原因,将这些事件错开
        throw new RuntimeException("这是不明智的操作");
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

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void getCommentFromInternet(TryUpdateCommentEvent e)
    {
        Article article = e.getArticle();

        if (log.isDebugEnabled())
            log.debug("处理尝试更新评论事件, " + article);

        // 确保更新该评论达到更新间隔
        if (article.getLastUpdateDate() != null && minutesAgoFromNow(article.getLastUpdateDate()) < COMMENT_UPDATE_PERIOD_MIN)
        {
            if (log.isDebugEnabled())
                log.debug("文章评论未达到更新间隔. " + article);
            return;
        }

        String op = CBHistory.calcOp(article);
        String url = "http://www.cnbeta.com/cmt";
        HttpRequest request = HttpRequest.post(url);
        request
                .contentType("application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .form("op", op);

        HttpResponse response = insureResponse(request);
        if (response == null)
        {
            log.error("获取评论失败,无法获取相应,请求的url为: "+url+",参数op为:"+op);
            return;
        }

        RawData raw = article.getRawData();
        if (raw == null)
            raw = gson.fromJson(response.bodyText(), RawData.class);
        else
            CodecUtils.jsonMergeTo(response.bodyText(), raw);

        if (!raw.getStatus().equals("success"))
            throw new RuntimeException("获取到的评论内容状态异常 :" + raw + " 在文章:" + article);

//        raw.setSid(e.getArticle().getSid());
        article.setRawData(raw);
        raw.setArticle(article);
        raw.setSid(article.getSid());

//        persist(article);// 保存状态
        article = articleRepo.save(article);
//        persist(raw);// 将源数据保存到数据库中

        Events.post(new UpdateCommentEvent(article, raw));
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void parseComment(UpdateCommentEvent e)
    {
        Article article = e.getArticle();

        if (log.isDebugEnabled())
            log.debug("更新评论 " + e);

        String result = CodecUtils.decodeBase64(e.getRawContent().getResult());
        result = result.replaceFirst("^cnbeta", "");// 去除前缀
        RawComment rawComment = new RawComment();
        // 设置好已有的评论
        Set<Comment> comments = article.getComments();
        {
            if (comments == null)
            {
                comments = Sets.newHashSet();
                article.setComments(comments);
            }
            Map<String, Comment> cmtMap = Maps.newHashMap();

            for (Comment comment : comments)
                cmtMap.put(comment.getTid().toString(), comment);

            rawComment.setCommentList(cmtMap);
        }
        // 更新
        CodecUtils.jsonMergeTo(result, rawComment);
        CodecUtils.jsonMergeTo(result, article);

        comments.addAll(rawComment.getCommentList().values());

        for (Comment comment : comments)
        {
            // 将 pid 为 0 的值置为空, 因为 id 为 0 的评论是不存在的
            if (comment.getPid() != null && comment.getPid() == 0)
                comment.setPid(null);
            // 将匿名人士的名字设置为 null
            if (comment.getName() != null && comment.getName().equals("匿名人士"))
                comment.setName(null);
            // 不存储 userId = 0
            if (comment.getUserId() != null && comment.getUserId() == 0)
                comment.setUserId(null);
            // 有可能为空字符串
            if (comment.getIcon() != null && comment.getIcon().equals(""))
                comment.setIcon(null);
        }

        article.setLastUpdateDate(new Date());

        // 保存状态
        // 这里的更新稍微有点麻烦,但是为了确保正确性,也就这样了
        Iterable<Comment> saved = commentRepo.save(article.getComments());
        article.setComments(Sets.newHashSet(saved));
        article = articleRepo.save(article);

        if (log.isInfoEnabled())
            log.info("完成文章的更新. " + article);
        // 添加下次更新的事件调度
        if (! isArticleExpired(article))
        {
            // TODO 修改为快要过期的是时候再更新
            Date nextUpdate = DateTime.now().plusMinutes(COMMENT_UPDATE_PERIOD_MIN - 10).toDate();
            TryFoundArticleEvent event = new TryFoundArticleEvent(article.getSid());
            scheduler.schedule(event, nextUpdate);
        }

        Events.finish(e);
    }

    @Subscribe
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void handlePersist(PersistEntityEvent e)
    {
        if (log.isDebugEnabled())
        {
            final String format = "持久化 %s";
            log.debug(String.format(format, e.getEntities().getClass()));
        }

        em.getTransaction().begin();

        int i = 0;
        for (Persistable persistable : e.getEntities())
        {
            i++;
            if (em.contains(persistable))
            {
                em.persist(persistable);
            } else
            {
                em.merge(persistable);
            }
        }

        if (log.isDebugEnabled())
        {
            final String format = "完成持久化 %s, 共有 %s 条数据";
            log.debug(String.format(format, e.getEntities().getClass(), i));
        }

        try
        {
            em.getTransaction().commit();
        } catch (Exception ex)
        {
            log.error("事务提交异常 消息为:" + ex.getMessage());
            ExceptionEvent event = new ExceptionEvent(ex, "当前的持久化对象为: " + e.getEntities());
            Events.post(event);
        }
    }


    private <E extends Persistable> void persist(E entity)
    {
        Events.post(new PersistEntityEvent(entity));
    }

    private <E extends Persistable> void persist(Iterable<E> entity)
    {
        Events.post(new PersistEntityEvent(entity));
    }

    private boolean isArticleExpired(Long id)
    {
        Article article = articleRepo.findOne(id);

        return isArticleExpired(article);
    }

    private boolean isArticleExpired(Article article)
    {
        if (article == null)
            return false;

        return daysAgoFromNow(article.getDate()) >= ARTICLE_EXPIRED_DAYS;
    }

    private boolean isArticleCommentNeedUpdate(Article article)
    {
        return minutesAgoFromNow(article.getLastUpdateDate()) >= COMMENT_UPDATE_PERIOD_MIN;
    }

    private static int minutesAgoFromNow(Date date)
    {
        checkNotNull(date);
        Minutes minutes = Minutes.minutesBetween(new DateTime(date), DateTime.now());
        return minutes.getMinutes();
    }

    private static int daysAgoFromNow(Date date)
    {
        checkNotNull(date);
        Days i = Days.daysBetween(new DateTime(date), DateTime.now());
        return i.getDays();
    }

    /**
     * 确保能获取到响应,默认将会尝试三次
     */
    public HttpResponse insureResponse(HttpRequest request)
    {
        return insureResponse(request, 3);
    }
    public HttpResponse insureResponse(HttpRequest request, int retryTimes)
    {
        HttpResponse response = null;

        do
        {
            try
            {
                response = request.send();
            } catch (jodd.http.HttpException ex)
            {
                log.warn("获取响应失败: " + ex.getMessage());
            }
        } while (response == null && retryTimes-- > 0);

        return response;
    }

}
