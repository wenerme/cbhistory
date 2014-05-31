package me.wener.cbhistory.repo;

import com.google.common.collect.Maps;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Expression;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.Getter;

public class BaseCustom
{
    @Inject
    @Getter
    private CommentRepo commentRepo;
    @Inject
    @Getter
    private ArticleRepo articleRepo;
    @Getter(AccessLevel.PROTECTED)
    @PersistenceContext
    private EntityManager entityManager;

    protected JPAQuery getQuery() {return new JPAQuery(entityManager);}

    protected <K,V> Map<K,V> toMap(List<Tuple> list, Expression<K> key, Expression<V> val)
    {
        Map<K, V> map = Maps.newHashMap();
        for (Tuple tuple : list)
        {
            map.put(tuple.get(key), tuple.get(val));
        }
        return map;
    }
}
