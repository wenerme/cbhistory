package me.wener.cbhistory.core;

import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.core.event.TryDiscoverArticleByUrlEvent;
import me.wener.cbhistory.core.modules.ChainInjector;
import me.wener.cbhistory.core.modules.OrmlitePersistModule;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;
import me.wener.cbhistory.core.process.ArticleProcess;
import me.wener.cbhistory.core.process.AuxiliaryProcess;
import me.wener.cbhistory.core.process.CommentProcess;
import org.joda.time.DateTime;
import org.joda.time.Duration;

@Slf4j
public class App
{
    private static Injector injector;

    public static Injector getInjector()
    {
        if (injector == null) {
            DateTime start = DateTime.now();
            log.info("正在启动程序...");

            injector = ChainInjector
                    .start(PropertiesModule
                            .none()
                            .withOptionalResource("default.properties", "db.properties", "app.properties"))
                    .and(Jsr250Module.class, CloseableModule.class)
                    .then(PersistModule.class)
                    .then(OrmlitePersistModule.class)
                    .getInjector();

            // 初始化
            injector.getInstance(App.class);

            DateTime end = DateTime.now();
            log.info("程序启动完成 耗时: {} ms", new Duration(start, end).getMillis());
        }
        return injector;
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

    public static final long SECOND_MS = 1000;
    public static final long MINUTE_MS = SECOND_MS * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long DAY_MS = HOUR_MS * 24;

    @Inject
    private void setupLogging(@Named("app.log.level") String  logLevel)
    {
        log.info("设置日志等级为: {}", logLevel);
    }

    @PostConstruct
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

    public static void main(String[] args)
    {
        Injector injector = getInjector();
    }

}
