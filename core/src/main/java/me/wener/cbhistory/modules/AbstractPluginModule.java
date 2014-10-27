package me.wener.cbhistory.modules;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import lombok.Getter;

public abstract class AbstractPluginModule extends AbstractModule implements IPlugin
{
    @Getter
    private static EventBus eventBus = AbstractPlugin.getEventBus();

    @Override
    public void init()
    {

    }
}
