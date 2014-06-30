package me.wener.cbhistory.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import me.wener.cbhistory.core.pluggable.PlugInfo;
import me.wener.cbhistory.persistence.ormlite.JodaDateType;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.persistence.ormlite.service.ArticleServiceImpl;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.service.RawDataService;
import me.wener.cbhistory.persistence.ormlite.service.CommentServiceImpl;
import me.wener.cbhistory.service.impl.RawDataServiceCacheImpl;
import me.wener.cbhistory.utils.prop.Prop;

@PlugInfo(name = "Ormlite 持久层模块", author = "wener<wenermail@gmail.com>")
public class OrmlitePersistModule extends AbstractPluginModule
{

    @Prop("jdbc.url")
    String jdbcUrl;
    @Inject
    DataSource dataSource;
    @Override
    protected void configure()
    {
        DataPersisterManager.registerDataPersisters(new JodaDateType());

        bind(RawDataService.class)
                .to(RawDataServiceCacheImpl.class);

        try
        {
            DataSourceConnectionSource source = new DataSourceConnectionSource(dataSource, jdbcUrl);
            bind(ConnectionSource.class)
                    .toInstance(source);
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }


        bind(ArticleService.class)
                .to(ArticleServiceImpl.class);

        bind(CommentService.class)
                .to(CommentServiceImpl.class);

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
