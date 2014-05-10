package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.sql.DataSource;

public class PersistModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(DataSource.class)
                .toProvider(C3p0DataSourceProvider.class);
    }
}
