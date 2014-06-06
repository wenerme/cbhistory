package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import com.google.inject.internal.util.Classes;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ConfigureModule extends AbstractModule
{
//    @Inject
//    @Named("cbhistory.version")
    private String appVersion = "";
    @Inject
    @Named("app.log.level")
    String logLevel;

    @Override
    protected void configure()
    {

    }
    @PostConstruct
    private void report()
    {

        try
        {
            Class.forName("ch.qos.logback.classic.Logger");

            log.info("当前程序版本: {}", appVersion);
            log.info("设置日志记录等级为: {}", logLevel);
            Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            logger.setLevel(Level.toLevel(logLevel));
            logger = (Logger)LoggerFactory.getLogger("me.wener.cbhistory");
            logger.setLevel(Level.toLevel(logLevel));
        } catch (ClassNotFoundException e)
        {
            log.warn("当前无 logback 支持, 忽略 loglevel");
        }


    }
}
