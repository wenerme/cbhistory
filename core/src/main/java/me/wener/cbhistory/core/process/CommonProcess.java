package me.wener.cbhistory.core.process;

import static com.google.common.base.Preconditions.*;

import com.google.gson.Gson;
import javax.inject.Inject;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.utils.Same;
import me.wener.cbhistory.utils.prop.Prop;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

/**
 * 抽象一些常用的东西
 */
@Slf4j
public abstract class CommonProcess
{

    /**
     * Gson 是比较常用,不需要太多的实例,gson 是线程安全的.
     */
    protected static Gson gson = Same.getGson();
    @Inject
    protected ArticleService articleSvc;
    @Inject
    protected CommentService commentSvc;
    /**
     * 文章更新间隔
     * <pre>
     * 单位为: 分钟
     * 默认 5 * 60
     * </pre>
     */
    @Prop("app.article.update.interval")
    @Getter
    private int articleUpdateInterval = 5 * 60;
    /**
     * 文章更新因子,当距离过期时间低于这个值时,会以
     * <b>距离过期的分钟数/该因子 * 更新间隔</b> 来计算新的更新间隔时间
     * <pre>
     * 单位为: 分钟
     * 默认 2 * 60
     * </pre>
     */
    @Prop("app.article.update.factor")
    @Getter
    private int articleUpdateFactor = 2 * 60;
    /**
     * 文章过期小时数
     */
    @Prop("app.article.expired.hours")
    @Getter
    private int articleExpiredHours = 7 * 24;

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
        int totalTimes = retryTimes;
        do
        {
            try
            {
                response = request.send();
            } catch (jodd.http.HttpException ex)
            {
                log.warn("第 {} 次 获取响应失败: {}", totalTimes - retryTimes + 1, ex.getMessage());
            }
        } while (response == null && retryTimes-- > 0);

        if (response != null && response.statusCode() != 200)
        {
            log.error("获取 URL 返回状态码异常 status: {} 请求的url为: {},参数: {}"
                    , response.statusCode(), request.url(), request.query());
        }

        return response;
    }

    /**
     * 判断该文章是否需要更新
     * <p/>
     * 文章更新策略,文章的更新需要更新
     */
    public boolean isArticleNeedUpdate(Article article)
    {
        checkNotNull(article);
        // 如果没有上次更新日期,则需要更新
        if (article.getLastUpdateDate() == null)
            return true;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdate = article.getLastUpdateDate();

        // 如果已经达到了更新间隔,则直接返回 true
        int minBetweenLastUpdate = Minutes.minutesBetween(lastUpdate, now).getMinutes();
        if (minBetweenLastUpdate > getArticleUpdateInterval())// 5 个小时的更新间隔
            return true;

        LocalDateTime expiredDate = getCommentExpiredDate(article);

        // 缩短更新间隔的因子
        double factor = 1;

        // 如果距离评论过期时间非常近,则所更新间隔的因子变大
        // 尚未过期
        if (expiredDate.isAfter(now))
        {
            int minutes = Minutes.minutesBetween(now, expiredDate).getMinutes();
            if (minutes < getArticleUpdateFactor())// 如果还差 30 分钟,则认为该时间非常近
            {
                factor = (double) minutes / getArticleUpdateFactor();
            }
        }

        // 考虑上当前的因子
        return minBetweenLastUpdate > getArticleUpdateInterval() * factor;

    }

    public LocalDateTime getCommentExpiredDate(Article article)
    {
        return new LocalDateTime(article.getDate()).plusHours(articleExpiredHours);
    }
}
