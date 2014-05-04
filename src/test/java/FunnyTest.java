import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

@Ignore
public class FunnyTest
{

    @Test
    public void testGetBoolean()
    {
        assertEquals(Boolean.getBoolean("true"), false);
        assertEquals(Boolean.getBoolean("false"), false);
        assertEquals(Boolean.getBoolean("yes"), false);
    }

    @Test
    public void testCmptor()
    {
        String[] str = {"0", "1", "2", "3", "4", "5"};
        List<Integer> list = new ArrayList<Integer>();
        for (String s : str)
        {
            list.add(Integer.valueOf(s));
        }

        int result = Collections.binarySearch(list, 1, new Comparator<Integer>()
        {
            @Override
            public int compare(Integer o1, Integer o2)
            {
                return o1 < o2 ? -1 : (o1 == o2 ? 0 : 1);
            }
        });

        System.out.println(result);
        System.out.println((Integer) 1 == (Integer) 1);

        Integer i1 = null;
        Integer i2 = null;
        i1 = 1;
        i2 = 1;
        System.out.println(i1 == i2);
        System.out.println(Integer.valueOf("1") == Integer.valueOf("1"));
    }

    @Test
    public void testGeneric()
    {
        // 必须显示的指定类型参数
        Set<Number> set = FunnyTest.<Number>addIn((Set<Integer>) null, (Set<Double>) null);
    }

    // 正确的做法
    public static <E> Set<E> addIn(Set<? extends E> s1, Set<? extends E> s2)
    {
        return null;
    }

    @Test
    public void testVargs()
    {
        oneOrMore(1);

        ConcurrentMap map = new ConcurrentHashMap();
        map.putIfAbsent(1, 2);
    }

    public void oneOrMore(int first, int... rest)
    {
    }

}
