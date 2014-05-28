package me.wener.cbhistory.modules;

import me.wener.cbhistory.spring.SpringContextConfig;

public class SpringModule extends AbstractPluginModule
{
    @Override
    protected void configure()
    {
        requestStaticInjection(SpringContextConfig.class);
    }
}
