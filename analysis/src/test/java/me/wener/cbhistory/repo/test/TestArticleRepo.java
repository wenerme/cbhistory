package me.wener.cbhistory.repo.test;

import me.wener.cbhistory.core.App;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.spring.SpringContextConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestArticleRepo
{
    @Test
    public void test()
    {
        App.getInjector();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringContextConfig.class);
        context.refresh();

        ArticleRepo articleRepo = context.getBean(ArticleRepo.class);
        System.out.println(articleRepo.count());
        reportMemory();
    }
   @Test
    public void testSum()
    {
        App.getInjector();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringContextConfig.class);
        context.refresh();

        ArticleRepo articleRepo = context.getBean(ArticleRepo.class);

        System.out.println(articleRepo.countOfAllDiscuss());
    }

    private static void reportMemory()
    {
        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}
