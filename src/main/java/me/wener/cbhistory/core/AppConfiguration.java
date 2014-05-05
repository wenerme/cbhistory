package me.wener.cbhistory.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.process.ArticleProcess;
import me.wener.cbhistory.core.process.AuxiliaryProcess;
import me.wener.cbhistory.core.process.CommentProcess;
import me.wener.cbhistory.core.process.ProcessCenter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Named
public class AppConfiguration
{
    @Value("${app.log.level:null}")
    private String logLevel = null;

    @Inject
    ArticleProcess articleProcess;
    @Inject
    AuxiliaryProcess auxiliaryProcess;
    @Inject
    CommentProcess commentProcess;

    @PostConstruct
    public void init()
    {

        // 配置日志等级
        if (logLevel != null)
        {
            Level level = Level.valueOf(logLevel);
            Logger root = (Logger) LoggerFactory.getLogger("me.wener.cbhistory");
            root.setLevel(level);
            log.info("检测到日志等级设置: {} 将设置为 {}", logLevel, level);
        }

        log.info("完成程序配置的初始化.");
    }

    @PostConstruct
    public void registerProcess()
    {
        Events.register(auxiliaryProcess);
        Events.register(commentProcess);
        Events.register(articleProcess);
    }
}
