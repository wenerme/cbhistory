package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.inject.Singleton;
import javax.sql.DataSource;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.service.impl.ArticleServiceImpl;
import me.wener.cbhistory.service.impl.CommentServiceImpl;

public class PersistModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(DataSource.class)
                .toProvider(C3p0DataSourceProvider.class);

        bind(ArticleService.class)
                .to(ArticleServiceImpl.class)
                .in(Singleton.class);

        bind(CommentService.class)
                .to(CommentServiceImpl.class)
                .in(Singleton.class);
    }
}
