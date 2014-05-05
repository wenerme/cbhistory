package me.wener.cbhistory.core.process;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.core.EventScheduler;
import me.wener.cbhistory.core.Events;
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
@Deprecated
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

    private static Gson gson = new Gson();

    public static ProcessCenter getInstance()
    {
        if (instance == null)
            instance = new ProcessCenter();
        return instance;
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
