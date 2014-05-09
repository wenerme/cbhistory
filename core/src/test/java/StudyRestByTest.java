import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.Application;
import me.wener.cbhistory.core.CorePackage;
import me.wener.cbhistory.core.controller.CBAdapterController;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Ignore
public class StudyRestByTest
{
    @Controller
    @EnableAutoConfiguration
    @EnableTransactionManagement
    @EnableJpaRepositories("me.wener.cbhistory.repositories")
    @EnableScheduling
    @ImportResource("classpath:beans.xml")
    @Import(Application.class)
    @ComponentScan(basePackageClasses = {CorePackage.class},
            includeFilters = {@ComponentScan.Filter(Named.class)})
    public static class SampleController {

        @RequestMapping("/")
        @ResponseBody
        String home() {
            return "Hello World!";
        }

        public static void main(String[] args) throws Exception {
            SpringApplication.run(SampleController.class, args);
        }
    }
}
