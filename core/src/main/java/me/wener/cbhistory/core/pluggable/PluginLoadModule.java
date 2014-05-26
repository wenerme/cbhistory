package me.wener.cbhistory.core.pluggable;

import com.google.common.collect.Maps;
import com.google.inject.Module;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.modules.AbstractPluginModule;
import me.wener.cbhistory.modules.IPlugin;

@Slf4j
public class PluginLoadModule extends AbstractPluginModule
{
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

    @Override
    protected void configure()
    {
        PluginLoader<? extends IPlugin> loader = PluginLoader.of(IPlugin.class);
        Map<IPlugin, PlugInfo> plugins = Maps.newHashMap();

        // 实例化
        for (Class<? extends IPlugin> clazz : loader.getPlugins())
        {
            PlugInfo plugInfo = clazz.getAnnotation(PlugInfo.class);
            if (plugInfo == null)
                plugInfo = defaultInfo;
            if (Modifier.isInterface(clazz.getModifiers())
                    || Modifier.isAbstract(clazz.getModifiers())
                    || !plugInfo.load())
            {
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
            requestInjection(plugin);
            plugin.init();
            if (plugin instanceof Module)
                install((Module) plugin);
            //

            getEventBus().post(event.to(AfterLoadPluginEvent.class));
        }
    }
}
