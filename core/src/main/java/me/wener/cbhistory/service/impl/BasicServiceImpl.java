package me.wener.cbhistory.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableInfo;
import com.j256.ormlite.table.TableUtils;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Named;
import javax.persistence.Table;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.service.BasicService;

@Slf4j
public class BasicServiceImpl<T, ID extends Serializable>
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

        DatabaseTableConfig<T> config = DatabaseTableConfig.fromClass(getConnectionSource(), dataClass);
        config.setTableName(getTableName());
        setTableConfig(config);

        initialize();
    }

    private String getTableName()
    {
        Table table = dataClass.getAnnotation(Table.class);
        if (table == null || Strings.isNullOrEmpty(table.name()))
            return DatabaseTableConfig.extractTableName(dataClass);

        return table.name();
    }

    @Override
    public <S extends T> S save(S entity)
    {
        try
        {
            createOrUpdate(entity);
        } catch (SQLException e)
        {
            log.error("保存或更新实体时发生异常. entity:"+entity, e);
        }
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities)
    {
        for (S entity : entities)
            save(entity);
        return entities;
    }

    @Override
    public T findOne(ID id)
    {
        T entity = null;
        try
        {
            entity = queryForId(id);
        } catch (SQLException e)
        {
            log.error("查询 id 时发生异常. id: {}", id);
        }
        return entity;
    }

    @Override
    public boolean exists(ID id)
    {
        return findOne(id) == null;
    }

    @Override
    public Iterable<T> findAll()
    {
        List<T> list = null;
        try
        {
            list = queryForAll();
        } catch (SQLException e)
        {
            log.error("查询所有时发生异常",e);
        }
        return list != null? list: Lists.<T>newArrayList();
    }

    protected String idColumn()
    {
        return getTableInfo().getIdField().getColumnName();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids)
    {
        List<T> list = null;

        try
        {
            QueryBuilder<T, ID> builder = queryBuilder();
            list = builder.where().in(idColumn(), ids).query();
        } catch (SQLException e)
        {
            log.error("查询 id 列表时发生异常 ids:"+ Joiner.on(',').join(ids),e);
        }

        return list != null? list: Lists.<T>newArrayList();
    }

    @Override
    public long count()
    {
        try
        {
            return countOf();
        } catch (SQLException e)
        {
            log.error("查询总数的时候发生异常.", e);
        }
        return 0;
    }

    @Override
    public void delete(ID id)
    {
        try
        {
            deleteById(id);
        } catch (SQLException e)
        {
            log.error("删除 ID 时发生异常 ID:" + id, e);
        }
    }

    @Override
    public void deleteBy(T entity)
    {
        try
        {
            delete(entity);
        } catch (SQLException e)
        {
            log.error("删除时实体时发生异常 entity:"+entity,e);
        }
    }


    @Override
    public void delete(Iterable<? extends T> entities)
    {
        for (T entity : entities)
            deleteBy(entity);
    }

    @Override
    public void deleteAll()
    {
        try
        {
            TableUtils.clearTable(getConnectionSource(), dataClass);
        } catch (SQLException e)
        {
            throw new RuntimeException("清空表时发生异常", e);
        }
    }
}
