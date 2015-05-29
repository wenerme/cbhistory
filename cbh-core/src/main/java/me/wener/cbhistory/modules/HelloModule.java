package me.wener.cbhistory.modules;

import me.wener.cbhistory.core.pluggable.PlugInfo;

@PlugInfo(name = "简单的测试模块", author = "wener<wenermail@gmail.com>")
public class HelloModule extends AbstractPluginModule
{
    public HelloModule()
    {
        getEventBus().register(this);
    }

    @Override
    protected void configure()
    {
        System.out.println("Hello, configuring.");
    }
}
