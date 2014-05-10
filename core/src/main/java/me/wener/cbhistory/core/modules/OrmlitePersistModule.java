package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.service.impl.CommentServiceImpl;

public class OrmlitePersistModule extends AbstractModule
{
//    @Inject
//    DataSource dataSource;
//    @Inject @Named("jdbc.url")
//    String jdbcUrl;

    @Override
    protected void configure()
    {
//        ArticleServiceImpl articleSvc;
//        CommentServiceImpl commentSvc;
//        ConnectionSource source;
//        try
//        {
//            articleSvc = new ArticleServiceImpl();
//            commentSvc = new CommentServiceImpl();
//            source = new DataSourceConnectionSource(dataSource, jdbcUrl);
//            DaoManager.registerDao(source, articleSvc);
//            DaoManager.registerDao(source, commentSvc);
//
//        } catch (SQLException e)
//        {
//            throw new RuntimeException(e);
//        }
//        bind(ConnectionSource.class)
//                .toInstance(source);
//        bind(ArticleService.class)
//            .toInstance(articleSvc);
//        bind(CommentService.class)
//                .toInstance(commentSvc);

        bind(ConnectionSource.class)
                .toProvider(new Provider<ConnectionSource>()
                {
                    @Inject
                    Provider<DataSource> dataSourceProvider;
                    @Inject @Named("jdbc.url")
                    String jdbcUrl;

                    @Override
                    public ConnectionSource get()
                    {
                        try
                        {
                            return new DataSourceConnectionSource(dataSourceProvider.get(), jdbcUrl);
                        } catch (SQLException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                });


        bind(ArticleService.class)
                .toProvider(new Provider<ArticleService>()
                {
                    @Inject
                    Provider<ConnectionSource> connectionSourceProvider;

                    @Override
                    @SneakyThrows
                    public ArticleService get()
                    {
                        return (ArticleService) DaoManager.createDao(connectionSourceProvider.get(), Article.class);
                    }
                });

        bind(CommentService.class)
                .to(CommentServiceImpl.class)
                .asEagerSingleton();

    }

    static class DaoProvider<D extends Dao<T, ?>, T> implements Provider<D>
    {
        Class<T> providerClass;
        @Inject
        Provider<ConnectionSource> connectionSourceProvider;

        DaoProvider(Class<T> providerClass)
        {
            this.providerClass = providerClass;
        }

        @Override
        @SneakyThrows
        public D get()
        {
            try
            {
                return DaoManager.createDao(connectionSourceProvider.get(), providerClass);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }

        public static <D extends Dao<T, ?>, T> Provider<D> provide(Class<T> clazz)
        {
            return new DaoProvider<>(clazz);
        }
    }
}
