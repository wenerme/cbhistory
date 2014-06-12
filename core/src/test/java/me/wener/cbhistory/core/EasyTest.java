package me.wener.cbhistory.core;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EasyTest
{
    @Test
    public void launch()
    {
        App.getInjector();
    }
    @Test
    public void report()
    {
        App.reportSystemInfo();
    }
}
