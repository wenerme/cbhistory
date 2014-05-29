package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistModule extends AbstractModule
{
    @Inject
    @Named("jdbc.url")
    String jdbcUrl;
    @Inject
    @Named("jdbc.username")
    String jdbcUser;

    @Override
    protected void configure()
    {
        log.info("当前数据库连接地址为: {}", jdbcUrl);
        log.info("当前数据库用户为: {}", jdbcUser);
        // 绑定 C3p0 连接池
        bind(DataSource.class)
                .toProvider(C3p0DataSourceProvider.class);
    }
}
