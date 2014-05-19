package me.wener.cbhistory.core;

import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
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
                            .withOptionalResource("default.properties", "db.properties"))
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

    @Inject
    AuxiliaryProcess auxiliaryProcess;
    @Inject
    CommentProcess commentProcess;
    @Inject
    ArticleProcess articleProcess;

    @PostConstruct
    private void setup()
    {
        Events.register(auxiliaryProcess);
        Events.register(commentProcess);
        Events.register(articleProcess);
    }


    public static void main(String[] args)
    {
        Injector injector = getInjector();
    }

}
