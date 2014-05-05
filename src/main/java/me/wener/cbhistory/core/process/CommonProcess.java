package me.wener.cbhistory.core.process;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import java.util.Date;
import javax.inject.Inject;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.EventScheduler;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.repositories.ArticleRepository;
import me.wener.cbhistory.repositories.CommentRepository;
import me.wener.cbhistory.repositories.RawDataRepository;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * 抽象一些常用的东西
 */
@Slf4j
@Transactional(readOnly = true)
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

    /**
     * 文章更新间隔
     * <pre>
     * 单位为: 分钟
     * 默认 5 * 60
     * </pre>
     */
    @Value("${app.article.update.interval:300}")
    @Getter
    private int articleUpdateInterval = 5 * 60;
    /**
     * 文章过期因子,当距离过期时间低于这个值时,会以
     * <b>距离过期的分钟数/该因子 * 更新间隔</b> 来计算新的更新间隔时间
     * <pre>
     * 单位为: 分钟
     * 默认 2 * 60
     * </pre>
     */
    @Value("${app.article.update.factor:120}")
    @Getter
    private int articleUpdateFactor = 2 * 60;
    /**
     * 文章过期小时数
     */
    @Value("${app.article.expired.hours: 168}")
    @Getter
    private int articleExpiredHours = 7 * 24;

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

    /**
     * 判断该文章是否需要更新
     *
     * 文章更新策略,文章的更新需要更新
     */
    public boolean isArticleNeedUpdate(Article article)
    {
        checkNotNull(article);
        // 如果没有上次更新日期,则需要更新
        if (article.getLastUpdateDate() == null)
            return true;

        DateTime now = DateTime.now();
        DateTime lastUpdate = new DateTime(article.getLastUpdateDate());

        // 如果已经达到了更新间隔,则直接返回 true
        int minBetweenLastUpdate = Minutes.minutesBetween(lastUpdate,now).getMinutes();
        if (minBetweenLastUpdate > getArticleUpdateInterval())// 5 个小时的更新间隔
            return true;

        DateTime expiredDate = getCommentExpiredDate(article);

        // 缩短更新间隔的因子
        double factor = 1;

        // 如果距离评论过期时间非常近,则所更新间隔的因子变大
        // 尚未过期
        if (expiredDate.isAfter(now))
        {
            int minutes = Minutes.minutesBetween(now, expiredDate).getMinutes();
            if (minutes < getArticleUpdateFactor())// 如果还差 30 分钟,则认为该时间非常近
            {
                factor = (double)minutes/getArticleUpdateFactor();
            }
        }

        // 考虑上当前的因子
        return minBetweenLastUpdate > getArticleUpdateInterval()*factor;

    }
    public static DateTime getCommentExpiredDate(Article article)
    {
        return new DateTime(article.getDate()).plusDays(ARTICLE_EXPIRED_DAYS);
    }

    public boolean isArticleCommentNeedUpdate(Article article)
    {
        return minutesAgoFromNow(article.getLastUpdateDate()) >= COMMENT_UPDATE_PERIOD_MIN;
    }

    /**
     * 距离现在已经经过多少分钟
     */
    public static int minutesAgoFromNow(Date date)
    {
        checkNotNull(date);
        Minutes minutes = Minutes.minutesBetween(new DateTime(date), DateTime.now());
        return minutes.getMinutes();
    }

    /**
     * 距离现在已经经过多少小时
     */
    public static int hoursAgoFromNow(Date date)
    {
        checkNotNull(date);
        Hours minutes = Hours.hoursBetween(new DateTime(date), DateTime.now());
        return minutes.getHours();
    }

    /**
     * 距离现在已经经过多少天
     */
    public static int daysAgoFromNow(Date date)
    {
        checkNotNull(date);
        Days i = Days.daysBetween(new DateTime(date), DateTime.now());
        return i.getDays();
    }

    /**
     * 确保能获取到响应,默认将会尝试三次
     */
    public static HttpResponse insureResponse(HttpRequest request)
    {
        return insureResponse(request, 3);
    }
    public static HttpResponse insureResponse(HttpRequest request, int retryTimes)
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
