package me.wener.cbhistory;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.List;
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
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    @Bean
    public TaskScheduler taskScheduler()
    {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * 配置需要加载的属性文件,如果属性文件不存在,则不添加
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        String[] resources = {"default.properties", "db.properties","app.properties"};
        List<Resource> resourceLocations = Lists.newArrayList();

        for (String resource : resources)
        {
            ClassPathResource classPathResource = new ClassPathResource(resource);
            if (classPathResource.exists())
            {
                log.info("加载属性文件: "+resource);
                resourceLocations.add(classPathResource);
            }
        }

        p.setLocations(resourceLocations.toArray(new Resource[0]));
        return p;
    }

}
