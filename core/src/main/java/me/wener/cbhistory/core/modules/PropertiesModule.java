package me.wener.cbhistory.core.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * 辅助加载 properties 的模块,后添加的会覆盖先添加的
 */
@Slf4j
public class PropertiesModule extends AbstractModule
{

    Properties properties = new Properties();

    @Override
    protected void configure()
    {
        Names.bindProperties(binder(), properties);
    }

    /**
     * 绑定 properties,使用 Named 来注入
     */
    public PropertiesModule withProperties(Properties prop)
    {
        for (String name : prop.stringPropertyNames())
            properties.setProperty(name, prop.getProperty(name));
        return this;
    }

    /**
     * @see PropertiesModule#withProperties(java.util.Properties)
     */
    public PropertiesModule withProperties(Map<String, String> prop)
    {
        for (Map.Entry<String, String> entry : prop.entrySet())
            properties.setProperty(entry.getKey(), entry.getValue());
        return this;
    }

    /**
     * 读取 ClassPath 中的资源文件
     * @throws IOException
     */
    public PropertiesModule withResource(String path) throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        log.info("尝试加载属性文件 {} {}", path, is==null?"文件未找到!":"");
        if (is == null)
            throw new FileNotFoundException("未找到资源 " + path);

        Properties prop = new Properties();
        prop.load(is);
        withProperties(prop);
        is.close();

        return this;
    }

    /**
     * 和 {@link me.wener.cbhistory.core.modules.PropertiesModule#withResource(String)}
     * 类似,但允许资源不存在
     * @throws IOException
     */
    public PropertiesModule withOptionalResource(String path) throws IOException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        log.info("尝试加载属性文件 {} {}", path, is==null?"文件未找到!":"");
        if (is != null)
        {
            Properties prop = new Properties();
            prop.load(is);
            withProperties(prop);
            is.close();
        }

        return this;
    }

    /**
     * @throws IOException
     * @see me.wener.cbhistory.core.modules.PropertiesModule#withOptionalResource(String)
     */
    public PropertiesModule withOptionalResource(String ... resources) throws IOException
    {
        for (String resource : resources)
            withOptionalResource(resource);

        return this;
    }
}
