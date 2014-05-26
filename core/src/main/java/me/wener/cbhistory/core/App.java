package me.wener.cbhistory.core;

import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.core.event.TryDiscoverArticleBetweenDateEvent;
import me.wener.cbhistory.core.event.TryDiscoverArticleByUrlEvent;
import me.wener.cbhistory.core.modules.ChainInjector;
import me.wener.cbhistory.core.modules.ConfigureModule;
import me.wener.cbhistory.core.pluggable.AfterAppStartedEvent;
import me.wener.cbhistory.core.pluggable.AfterConfigureCompleteEvent;
import me.wener.cbhistory.core.pluggable.PluginLoadModule;
import me.wener.cbhistory.modules.AbstractPlugin;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;
import me.wener.cbhistory.core.process.ArticleProcess;
import me.wener.cbhistory.core.process.AuxiliaryProcess;
import me.wener.cbhistory.core.process.CommentProcess;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePeriod;

@Slf4j
@Singleton
public class App
{
    public static final long SECOND_MS = 1000;
    public static final long MINUTE_MS = SECOND_MS * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;
    private static Injector injector;

    public static Injector getInjector()
    {
        if (injector == null)
        {
            DateTime start = DateTime.now();
            log.info("正在启动程序...");

            injector = ChainInjector
                    .start(PropertiesModule
                            .none()
                            .withSystemProperties()
                            .withOptionalResource("default.properties", "db.properties", "app.properties"))
                    .and(Jsr250Module.class, CloseableModule.class)
                    .then(ConfigureModule.class)
                    .then(PersistModule.class)
                    .then(PluginLoadModule.class)
                    .getInjector();

            // 初始化
            injector.getInstance(App.class);
            // 触发配置完成事件
            AbstractPlugin.getEventBus().post(new AfterConfigureCompleteEvent());

            DateTime end = DateTime.now();
            log.info("程序配置完成 耗时: {} ms", new Duration(start, end).getMillis());
        }
        return injector;
    }

    public static void start()
    {
        // 开始调度
        getInjector().getInstance(App.class).setupSchedules();
        // 触发完成事件
        AbstractPlugin.getEventBus().post(new AfterAppStartedEvent());
    }

    public static void main(String[] args)
    {
        App.start();
    }

    /**
     * 注册处理事件
     */
    @Inject
    private void setupProcesses(AuxiliaryProcess auxiliaryProcess, CommentProcess commentProcess, ArticleProcess articleProcess)
    {
        Events.register(auxiliaryProcess);
        Events.register(commentProcess);
        Events.register(articleProcess);
    }

    /**
     * 这个需要手动调用,以便于测试时不用每次都启动
     */
    private void setupSchedules()
    {
        Timer timer = new Timer();

        timer.schedule(new EventPostTask("从主页检索文章",
                new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/"))
                , 5 * SECOND_MS, 30 * MINUTE_MS);

        timer.schedule(new EventPostTask("从排行榜检索文章",
                new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/top10.htm"))
                , 1 * MINUTE_MS, 10 * HOUR_MS);

        timer.schedule(new EventPostTask("从推荐页检索文章",
                new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/home/rank/show.htm"))
                , 2 * MINUTE_MS, 1 * HOUR_MS);

        // 每 10 小时发现过去 6-7 天的文章
        timer.schedule(new EventDiscoverArticleAgoTask()
                .setStartAgo(Days.days(7))
                .setEndAgo(Days.days(6))
                , 40 * SECOND_MS, 10 * HOUR_MS);

        // 每 6 小时发现过去 3-4 天的文章
        timer.schedule(new EventDiscoverArticleAgoTask()
                .setStartAgo(Days.days(4))
                .setEndAgo(Days.days(3))
                , 20 * MINUTE_MS, 6 * HOUR_MS);

        // 每 8 小时发现过去 1-2 天的文章
        timer.schedule(new EventDiscoverArticleAgoTask()
                .setStartAgo(Days.days(2))
                .setEndAgo(Days.days(1))
                , 40 * MINUTE_MS, 8 * HOUR_MS);
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @Accessors(chain = true)
    @Slf4j
    public static class EventDiscoverArticleAgoTask extends TimerTask
    {
        ReadablePeriod startAgo;
        ReadablePeriod endAgo;

        @Override
        public void run()
        {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minus(startAgo);
            LocalDateTime end = now.minus(endAgo);
            TryDiscoverArticleBetweenDateEvent event =
                    new TryDiscoverArticleBetweenDateEvent()
                            .setStart(start)
                            .setEnd(end);
            log.info("EventDiscoverArticleAgoTask 更新从 {} 到 {} 的文章", start, end);
            Events.post(event);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Slf4j
    public static class EventPostTask extends TimerTask
    {
        String description;
        Event event;

        @Override
        public void run()
        {
            log.info(description);
            Events.post(event);
        }
    }

}
