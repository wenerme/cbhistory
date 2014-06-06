package me.wener.cbhistory.server.madvoc.ace;

import com.google.common.reflect.Reflection;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import me.wener.cbhistory.core.pluggable.PluginLoadModule;
import me.wener.cbhistory.modules.IPlugin;
import me.wener.cbhistory.server.madvoc.MadvocServerFilter;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.reflections.Reflections;

public class AceServerFilter extends MadvocServerFilter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // 由于 ACE 下无法访问文件,所以无法扫描 classPath, 手动添加插件
        Reflections reflections = new Reflections(Reflection.getPackageName(IPlugin.class));
        PluginLoadModule.with(reflections.getSubTypesOf(IPlugin.class));
        LogManager.getRootLogger().setLevel(Level.INFO);
        LogManager.getLogger("me.wener.cbhistory").setLevel(Level.INFO);
        LogManager.getLogger("org.jodd.madvoc").setLevel(Level.DEBUG);
        super.init(filterConfig);
    }

}
