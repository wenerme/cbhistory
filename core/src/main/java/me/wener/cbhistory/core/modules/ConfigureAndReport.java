package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import com.google.inject.internal.util.Classes;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import me.wener.cbhistory.utils.prop.Prop;
import me.wener.cbhistory.utils.prop.PropsModule;
import org.slf4j.LoggerFactory;

@Slf4j
public class ConfigureAndReport
{
    @Prop(value = "app.info.version", optional = true)
    private String appVersion = "";

    @Prop("app.info.title")
    private String title;
    @Prop("app.info.logo")
    private String logo;
    @Prop("app.info.author.name")
    private String authorName;
    @Prop("app.info.author.email")
    private String authorEmail;

    @Prop("app.log.level")
    String logLevel;

    @PostConstruct
    private void report()
    {
        log.warn("{} - {} v {}", title, logo, appVersion);
        log.warn("Written by {} <{}>", authorName, authorEmail);

        try
        {
            Class.forName("ch.qos.logback.classic.Logger");

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
