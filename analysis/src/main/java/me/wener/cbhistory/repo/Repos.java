package me.wener.cbhistory.repo;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Repos
{
    private static final Repos self = new Repos();
    private Repos()
    {
    }

    /**
     * 尝试将返回的类型转换为一个 LinkedHashMap
     */
    public static <K, V> LinkedHashMap<K, V> asLinkedHashMap(List<Object[]> list)
    {
        return asLinkedHashMap(list, 0);
    }
    public static <K, V> LinkedHashMap<K, V> asLinkedHashMap(List<Object[]> list, int offset)
    {
        return addToMap(Maps.<K, V>newLinkedHashMap(), list, offset);
    }

    @SuppressWarnings("unchecked")
    public static <K, V, M extends Map<K,V>> M addToMap(M map, List<Object[]> list, int offset)
    {
        for (Object[] objects : list)
        {
            map.put((K) objects[offset], (V) objects[1 + offset]);
        }
        return map;
    }
}
