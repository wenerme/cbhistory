package me.wener.cbhistory.repo;

import com.google.common.collect.Maps;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Expression;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.Getter;

public class BasicCustom
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
    protected <K,V> LinkedHashMap<K,V> toLinkedMap(List<Tuple> list, Expression<K> key, Expression<V> val)
    {
        LinkedHashMap<K, V> map = Maps.newLinkedHashMap();
        for (Tuple tuple : list)
        {
            map.put(tuple.get(key), tuple.get(val));
        }
        return map;
    }
}
