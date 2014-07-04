package me.wener.cbhistory.export;

import java.util.Comparator;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public abstract class ValueComparator<T> implements Comparator<Object>
{

    @Getter @Setter
    private boolean reverse = false;


    @Getter
    private Map<?, ? extends T> map;

    public ValueComparator(Map<?, ? extends T> map)
    {
        this.map = map;
    }

    public abstract int doCompare(T a, T b);
    public int compare(Object a, Object b)
    {

        return reverse?doCompare(map.get(b), map.get(a)):doCompare(map.get(a), map.get(b));
    }
}
