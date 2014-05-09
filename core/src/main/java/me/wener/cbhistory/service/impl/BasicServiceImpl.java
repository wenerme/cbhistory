package me.wener.cbhistory.service.impl;

import com.google.inject.Inject;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import java.io.Serializable;
import java.sql.SQLException;
import javax.inject.Named;
import javax.sql.DataSource;
import me.wener.cbhistory.service.BasicService;

public abstract class BasicServiceImpl<T, ID extends Serializable>
        extends BaseDaoImpl<T, ID>
        implements BasicService<T, ID>
{
    protected BasicServiceImpl(Class<T> dataClass) throws SQLException
    {
        super(dataClass);
    }

    

    @Inject
    private void initDao(DataSource dataSource, @Named("jdbc.url") String url) throws SQLException
    {
        setConnectionSource(new DataSourceConnectionSource(dataSource, url));
        initialize();
    }
}
