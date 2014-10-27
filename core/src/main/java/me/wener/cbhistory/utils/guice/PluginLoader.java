package me.wener.cbhistory.utils.guice;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Reflection;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginLoader<T>
{
    private Collection<Class<T>> plugins = null;
    @Getter
    private String packageName;
    @Getter
    private Class<T> pluginType;

    private PluginLoader(Class<T> pluginType, String packageName)
    {
        this.packageName = packageName;
        this.pluginType = pluginType;
    }

    /**
     * 获取一个 {@code pluginType} 类型,包为该类型所在包的插件加载器
     */
    public static <PT> PluginLoader<? extends PT> of(Class<PT> pluginType)
    {
        return of(pluginType, Reflection.getPackageName(pluginType));
    }

    public static <PT> PluginLoader<? extends PT> of(Class<PT> pluginType, String packageName)
    {
        PluginLoader<PT> service = new PluginLoader<>(pluginType, packageName);

        return service;
    }

    @SuppressWarnings("unchecked")
    private void initialize()
    {
        if (plugins != null)
            return;
        plugins = Sets.newHashSet();

        ClassPath classPath;
        try
        {
            classPath = ClassPath.from(pluginType.getClassLoader());

            ImmutableSet<ClassPath.ClassInfo> infos = classPath.getTopLevelClasses(packageName);
            for (ClassPath.ClassInfo info : infos)
            {
                Class<?> clazz = info.load();
                if (pluginType.isAssignableFrom(clazz))
                    plugins.add((Class<T>) clazz);
            }
        } catch (Exception e)
        {
            // 有可能当前环境无法访问 path, 这时候这里会抛出异常
            log.warn("扫描 ClassPath 时出现异常, 可能当前环境无法操作, 已略过插件( {} )扫描. 异常信息: {}"
                    , pluginType, e.getMessage());
        }

    }

    public ImmutableSet<Class<T>> getPlugins()
    {
        if (plugins == null)
            initialize();
        return ImmutableSet.copyOf(plugins);
    }

    public Set<Class<T>> getPluginsAnnotatedBy(final Class<? extends Annotation> annotation)
    {
        Set<Class<T>> classes = Sets.newHashSet();
        for (Class<T> plugin : getPlugins())
        {
            if (plugin.isAnnotationPresent(annotation))
            {
                classes.add(plugin);
            }
        }

        return classes;
    }
}
