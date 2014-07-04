package me.wener.cbhistory.export;

import java.util.Comparator;
import java.util.Map;
import lombok.Getter;

public class NumberValueComparator<V extends Number & Comparable> extends ValueComparator<V>
{

    public NumberValueComparator(Map<?, ? extends V> base)
    {
        super(base);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int doCompare(V a, V b)
    {
        return a.compareTo(b);
    }
}
