package me.wener.cbhistory.modules;

import com.google.common.eventbus.EventBus;
import lombok.Getter;

public abstract class AbstractPlugin implements IPlugin
{
    @Getter
    private static EventBus eventBus = new EventBus(IPlugin.class.toString());
    @Override
    public void init()
    {

    }
}
