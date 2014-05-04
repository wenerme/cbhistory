package me.wener.cbhistory.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.TryDiscoverArticleByUrlEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 驱动整个程序自动更新,自动发现
 */
@Named
@Slf4j
public class ScheduleEvents
{
//        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/"));
//        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/top10.htm"));
//        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/home/rank/show.htm"));

    public static final long SECOND_MS = 1000;
    public static final long MINUTE_MS = SECOND_MS * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;

    @Inject
    TaskScheduler scheduler;

    @PostConstruct
    public void init()
    {

    }

    @Scheduled(initialDelay = 20 * SECOND_MS, fixedRate = 5 * HOUR_MS)
    public void discoverArticlesInHomePage()
    {
        log.info("从主页检索文章");
        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/"));
    }
    @Scheduled(initialDelay = 20 * MINUTE_MS, fixedRate = 1 * DAY_MS)
    public void discoverArticlesInTopTen()
    {
        log.info("从排行榜检索文章");
        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/top10.htm"));
    }
    @Scheduled(initialDelay = 40 * SECOND_MS, fixedRate = 40 * MINUTE_MS)
    public void discoverArticlesInHomeRank()
    {
        log.info("从推荐页检索文章");
        Events.post(new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/home/rank/show.htm"));
    }
    @Scheduled(initialDelay = 10 * SECOND_MS, fixedRate = 1 * HOUR_MS)
    public void callGC()
    {
        log.info("请求垃圾收集.");
        System.gc();
    }
}
