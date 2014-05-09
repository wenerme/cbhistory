import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Ignore
public class StudySchduleByTest
{
    @Test
    public void launch()
    {


    }

    public static void main(String[] args)
    {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SchApp.class);

        TaskScheduler sh;
    }

    @Configuration
    @EnableAsync
    @EnableScheduling
    @Slf4j
    public static class SchApp
    {
//        @Inject
//        Executor executor;
        TaskExecutor executor;
        @Inject
        TaskScheduler scheduler;

        @Bean
        public TaskScheduler taskScheduler()
        {
            return new ThreadPoolTaskScheduler();
        }

        @PostConstruct
        public void tester()
        {
            scheduler.schedule(new LogRunner("after five"), DateTime.now().plusSeconds(5).toDate());
        }

        @Data
        @Accessors(chain = true)
        @AllArgsConstructor
        public static class LogRunner implements Runnable
        {
            String message;
            @Override
            public void run()
            {
                log.info("Run:"+message);
            }

        }

        @Async
        void doAsync()
        {
            log.info("doAsync");
        }
        @Async
        void doAsync2()
        {
            log.info("doAsync");
        }

        public static class SimpleTasks
        {

            @Scheduled(fixedDelay = 1000)
            public void doFixedDelay() throws Exception
            {
                Thread.sleep(2000);
                log.info("doFixedDelay");
            }
            @Scheduled(fixedDelay = 1000)
            public void doFixedDelayNoSleep() throws Exception
            {
                log.info("doFixedDelayNoSleep");
            }

            @Scheduled( fixedRate = 1000)
            public void doFixedRate() throws Exception
            {
                Thread.sleep(2000);
                log.info("doFixedRate");
            }

            @Scheduled( fixedRate = 1000)
            public void doFixedRateNoSleep() throws Exception
            {
                log.info("doFixedRateNoSleep");
            }
        }

    }

}
