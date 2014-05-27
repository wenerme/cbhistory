package me.wener.cbhistory.modules;

import me.wener.cbhistory.spring.AppConfig;

public class SpringModule extends AbstractPluginModule
{
    @Override
    protected void configure()
    {
        requestStaticInjection(AppConfig.class);
    }
}
