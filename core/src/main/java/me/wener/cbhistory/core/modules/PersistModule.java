package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.sql.DataSource;

public class PersistModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        // 绑定 C3p0 连接池
        bind(DataSource.class)
                .toProvider(C3p0DataSourceProvider.class);
    }
}
