package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.sql.DataSource;
import me.wener.cbhistory.service.RawDataService;
import me.wener.cbhistory.service.impl.RawDataServiceCacheImpl;

public class PersistModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(DataSource.class)
                .toProvider(C3p0DataSourceProvider.class);
        bind(RawDataService.class)
                .to(RawDataServiceCacheImpl.class);
    }
}
