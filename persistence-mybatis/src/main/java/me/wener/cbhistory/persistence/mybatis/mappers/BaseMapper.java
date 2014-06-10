package me.wener.cbhistory.persistence.mybatis.mappers;

import java.io.Serializable;
import java.util.List;

public interface BaseMapper<T, ID extends Serializable>
{
    int delete(T entity);
    int deleteById(ID id);
    int deleteByIds(List<ID> ids);
    List<T> findAll();
    List<T> findById();
    List<T> findByIds(List<ID> ids);
    long count();
}
