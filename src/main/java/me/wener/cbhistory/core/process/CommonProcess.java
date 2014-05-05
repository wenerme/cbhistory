package me.wener.cbhistory.core.process;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.EventScheduler;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.repositories.ArticleRepository;
import me.wener.cbhistory.repositories.CommentRepository;
import me.wener.cbhistory.repositories.RawDataRepository;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;

/**
 * 抽象一些常用的东西
 */
@Slf4j
public abstract class CommonProcess
{
    /**
     * 文章过期的时间,单位为 天
     */
    public static final int ARTICLE_EXPIRED_DAYS = 7;// 看到有 6 天后还有评论的,所以暂且设置为7天
    /**
     * 评论更新的间隔,单位为 分钟
     */
    public static final int COMMENT_UPDATE_PERIOD_MIN = 60 * 5;// 5 小时

    @Inject
    protected EventScheduler scheduler;

    @Inject
    protected ArticleRepository articleRepo;
    @Inject
    protected RawDataRepository rawCommentRepo;
    @Inject
    protected CommentRepository commentRepo;

    /**
     * Gson 是比较常用,不需要太多的实例,gson 是线程安全的.
     */
    protected static Gson gson = new Gson();

    protected boolean isArticleExpired(Long id)
    {
        Article article = articleRepo.findOne(id);

        return isArticleExpired(article);
    }

    protected boolean isArticleExpired(Article article)
    {
        if (article == null)
            return false;

        return daysAgoFromNow(article.getDate()) >= ARTICLE_EXPIRED_DAYS;
    }

    protected boolean isArticleCommentNeedUpdate(Article article)
    {
        return minutesAgoFromNow(article.getLastUpdateDate()) >= COMMENT_UPDATE_PERIOD_MIN;
    }

    protected static int minutesAgoFromNow(Date date)
    {
        checkNotNull(date);
        Minutes minutes = Minutes.minutesBetween(new DateTime(date), DateTime.now());
        return minutes.getMinutes();
    }

    protected static int daysAgoFromNow(Date date)
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
