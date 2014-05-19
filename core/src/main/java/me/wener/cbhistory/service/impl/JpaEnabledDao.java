package me.wener.cbhistory.service.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.io.Serializable;
import java.sql.SQLException;

public class JpaEnabledDao<T, ID extends Serializable> extends BaseDaoImpl<T, ID>
{
    protected JpaEnabledDao(Class<T> dataClass) throws SQLException
    {
        super(dataClass);
    }

    protected JpaEnabledDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    protected JpaEnabledDao(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException
    {
        super(connectionSource, tableConfig);
    }

    @Override
    public void initialize() throws SQLException
    {
//        DatabaseTableConfig<T> config = new DatabaseTableConfig<>();
//        config.setDataClass(dataClass);
//        config.setTableName(getTableName());
//        config.setFieldConfigs(OrmliteJPAUtils.getFieldConfig(getConnectionSource(), getTableName(), dataClass));
//        config.initialize();
//
//        setTableConfig(config);

        super.initialize();
    }
//
//    private String getTableName()
//    {
//        Table table = dataClass.getAnnotation(Table.class);
//        if (table == null || Strings.isNullOrEmpty(table.name()))
//            return DatabaseTableConfig.extractTableName(dataClass);
//
//        return table.name();
//    }

}
