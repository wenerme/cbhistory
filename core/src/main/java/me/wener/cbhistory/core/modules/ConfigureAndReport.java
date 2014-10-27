package me.wener.cbhistory.core.modules;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.utils.prop.Prop;
import org.slf4j.LoggerFactory;

@Slf4j
public class ConfigureAndReport
{
    @Prop("app.log.level")
    String logLevel;
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

    @PostConstruct
    private void report()
    {
        log.warn("{} - {} v {}", title, logo, appVersion);
        log.warn("Written by {} <{}>", authorName, authorEmail);

        try
        {
            Class.forName("ch.qos.logback.classic.Logger");

            log.info("设置日志记录等级为: {}", logLevel);
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            logger.setLevel(Level.toLevel(logLevel));
            logger = (Logger) LoggerFactory.getLogger("me.wener.cbhistory");
            logger.setLevel(Level.toLevel(logLevel));
        } catch (ClassNotFoundException e)
        {
            log.warn("当前无 logback 支持, 忽略 loglevel");
        }
    }
}
