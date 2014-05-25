package me.wener.cbhistory.core.pluggable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Reflection;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import lombok.Getter;

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

        ClassPath classPath;
        try
        {
            classPath = ClassPath.from(pluginType.getClassLoader());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        plugins = Sets.newHashSet();
        ImmutableSet<ClassPath.ClassInfo> infos = classPath.getTopLevelClasses(packageName);
        for (ClassPath.ClassInfo info : infos)
        {
            Class<?> clazz = info.load();
            if (pluginType.isAssignableFrom(clazz))
                plugins.add((Class<T>) clazz);
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
