package me.wener.cbhistory.core.pluggable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.pluggable.event.AfterLoadPluginEvent;
import me.wener.cbhistory.core.pluggable.event.BeforeLoadPluginEvent;
import me.wener.cbhistory.core.pluggable.event.CancelPluginLoadingEvent;
import me.wener.cbhistory.modules.AbstractPluginModule;
import me.wener.cbhistory.modules.IPlugin;

@Slf4j
public class PluginLoadModule extends AbstractPluginModule
{
    @Inject
    private Injector injector;
    private static Collection<Class<? extends IPlugin>> pluginClass = Lists.newCopyOnWriteArrayList();
    private final PlugInfo defaultInfo = new PlugInfo()
    {
        @Override
        public boolean load()
        {
            return true;
        }

        @Override
        public String author()
        {
            return "";
        }

        @Override
        public String name()
        {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return PlugInfo.class;
        }
    };

    /**
     * 静态添加插件
     */
    public static void with(Class<? extends IPlugin> clazz)
    {
        pluginClass.add(clazz);
    }
    public static void with(Iterable<Class<? extends IPlugin>> clazz)
    {
        Iterables.addAll(pluginClass, clazz);
    }
    @Override
    protected void configure()
    {
        PluginLoader<? extends IPlugin> loader = PluginLoader.of(IPlugin.class);
        Map<IPlugin, PlugInfo> plugins = Maps.newHashMap();

        // 解析出插件信息 并实例化
        pluginClass.addAll(loader.getPlugins());
        pluginClass = Lists.newCopyOnWriteArrayList(Sets.newHashSet(pluginClass));

        for (Class<? extends IPlugin> clazz : pluginClass)
        {
            PlugInfo plugInfo = clazz.getAnnotation(PlugInfo.class);
            if (plugInfo == null)
                plugInfo = defaultInfo;
            if (Modifier.isInterface(clazz.getModifiers())
                    || Modifier.isAbstract(clazz.getModifiers())
                    || !plugInfo.load())
            {
                pluginClass.remove(clazz);
                continue;
            }
            try
            {
                plugins.put(clazz.newInstance(), plugInfo);
            } catch (Exception e)
            {
                throw new RuntimeException("初始化插件时发生异常: ", e);
            }
        }
        log.info("共发现插件 {} 个", pluginClass.size());
        // 加载插件
        for (Map.Entry<IPlugin, PlugInfo> entry : plugins.entrySet())
        {
            IPlugin plugin = entry.getKey();
            PlugInfo info = entry.getValue();

            BeforeLoadPluginEvent event = new BeforeLoadPluginEvent();
            event.setPlugin(plugin.getClass());
            getEventBus().post(event);
            if (event.isCanceled())
            {
                getEventBus().post(event.to(CancelPluginLoadingEvent.class));
                continue;
            }

            log.info("加载插件 {} {} BY {}", plugin.getClass().getSimpleName(), info.name(), info.author());
            //
            injector.injectMembers(plugin);
            plugin.init();
            if (plugin instanceof Module)
                install((Module) plugin);
            //

            getEventBus().post(event.to(AfterLoadPluginEvent.class));
        }
    }
}
