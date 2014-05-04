package me.wener.cbhistory.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.ProcessCenter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Named
public class AppConfiguration
{
    @Value("${app.log.level:null}")
    private String logLevel = null;

    @PostConstruct
    public void init()
    {
        // 添加事件监听
        Events.register(ProcessCenter.getInstance());

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
}
