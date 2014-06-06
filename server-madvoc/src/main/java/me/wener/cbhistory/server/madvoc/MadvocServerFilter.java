package me.wener.cbhistory.server.madvoc;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import jodd.madvoc.MadvocServletFilter;
import me.wener.cbhistory.core.App;

public class MadvocServerFilter extends MadvocServletFilter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        App.start();

        super.init(filterConfig);
    }
}
