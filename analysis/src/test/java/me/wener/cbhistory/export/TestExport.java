package me.wener.cbhistory.export;

import javax.inject.Inject;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import me.wener.cbhistory.spring.SpringContextConfig;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextConfig.class)
//@Ignore
public class TestExport
{
    @Inject
    ApplicationContext ctx;
    @Inject
    ArticleRepo articleRepo;
    @Inject
    CommentRepo commentRepo;

    static
    {
        App.getInjector();
    }

    @Test
    public void testMgr()
    {
        AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        ExportMgr mgr = autowireCapableBeanFactory.createBean(ExportMgr.class);
        mgr.setBasePath("C:\\tmp");

        mgr.addExporter(SourceCountExporter.class);
        mgr.addExporter(AreaCountExporter.class);
        mgr.addExporter(CommenterActiveTimeExporter.class);
        mgr.addExporter(PublisherActiveTimeExporter.class);
        mgr.doExport(commentRepo.firstCommentDate(), LocalDateTime.now());
    }
    @Test
    public void test()
    {
        AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        SourceCountExporter sourceCount = autowireCapableBeanFactory.createBean(SourceCountExporter.class);
        sourceCount.setBasePath("C:\\");
        sourceCount.doExport(commentRepo.firstCommentDate(), LocalDateTime.now());
    }
    @Test
    public void testAreaCount()
    {
        AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        AreaCountExporter exporter = autowireCapableBeanFactory.createBean(AreaCountExporter.class);
        exporter.setBasePath("C:\\tmp");
        exporter.doExport(commentRepo.firstCommentDate(), LocalDateTime.now());
    }
    @Test
    public void testHourCount()
    {
        AutowireCapableBeanFactory autowireCapableBeanFactory = ctx.getAutowireCapableBeanFactory();
        CommenterActiveTimeExporter exporter = autowireCapableBeanFactory.createBean(CommenterActiveTimeExporter.class);
        exporter.setBasePath("C:\\tmp");
        exporter.doExport(commentRepo.firstCommentDate(), LocalDateTime.now());
    }
}
