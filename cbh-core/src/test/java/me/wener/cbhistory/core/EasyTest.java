package me.wener.cbhistory.core;

import me.wener.cbhistory.utils.SysUtils;
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
        SysUtils.reportSystemInfo();
    }
}
