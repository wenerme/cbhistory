package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.utils.prop.Prop;

@Slf4j
public class PersistModule extends AbstractModule
{
    @Prop("jdbc.url")
    String jdbcUrl;
    @Prop("jdbc.username")
    String jdbcUser;

    @Override
    protected void configure()
    {
        log.debug("当前数据库连接地址为: {}", jdbcUrl);
        log.debug("当前数据库用户为: {}", jdbcUser);
        // 绑定 C3p0 连接池
        bind(DataSource.class)
                .toProvider(C3p0DataSourcePropProvider.class);
    }
}
