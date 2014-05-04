package me.wener.cbhistory;


import com.google.common.base.Joiner;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CorePackage;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.ProcessCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories("me.wener.cbhistory.repositories")
@EnableScheduling
@ImportResource("classpath:beans.xml")
@ComponentScan(basePackageClasses = {CorePackage.class},
        includeFilters = {@ComponentScan.Filter(Named.class)})
public class Application extends SpringBootServletInitializer
{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(applicationClass);
    }

    private static Class<Application> applicationClass = Application.class;

    @PostConstruct
    public void init()
    {
        log.debug("初始化程序...");
        Events.register(ProcessCenter.getInstance());
//        Events.register(EventScheduler.getInstance());
    }

    public static void main(String[] args) throws Exception
    {
        if (log.isDebugEnabled())
            log.debug("程序启动参数: " + Joiner.on(",").join(args));

        // 启动并初始化程序
        SpringApplication.run(applicationClass, args);

        log.debug("程序启动完成");
    }

    @Bean
    public ProcessCenter processCenter()
    {
        return ProcessCenter.getInstance();
    }
//    @Bean
//    public EventScheduler eventScheduler()
//    {
//        return EventScheduler.getInstance();
//    }

    @Bean
    public TaskScheduler taskScheduler()
    {
        return new ThreadPoolTaskScheduler();
    }

}
