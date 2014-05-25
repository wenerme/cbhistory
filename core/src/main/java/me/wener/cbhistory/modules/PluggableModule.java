package me.wener.cbhistory.modules;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import lombok.Getter;

public abstract class PluggableModule extends AbstractModule implements IPlugin
{
    @Getter
    private static EventBus eventBus = new EventBus(PluggableModule.class.toString());

    @Override
    public void init()
    {

    }
}
