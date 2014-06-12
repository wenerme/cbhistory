package learning;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LazyInitTest
{
    @Test
    public void test()
    {
        System.out.println("Start Test");

        System.out.println("LoadExp is "+LoadExp.class);
        System.out.println("B is "+LoadExp.b);
        System.out.println("Done B");
        System.out.println("A is "+LoadExp.a);
        System.out.println("Done A");
        System.out.println("new LoadExp is "+new LoadExp());

        System.out.println("End Test");
    }
    public static class LoadExp
    {
        private final static A a  = new A();
        private final static String b = "123";
        static {
            System.out.println("Static Block");
        }
    }
}

class A
{
    A()
    {
        System.out.println("New A");
    }
}
