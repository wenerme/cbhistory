package me.wener.cbhistory.core.event;

import lombok.Getter;

public class StartEvent extends AbstractEvent
{
    @Getter
    private Event event;

    public StartEvent(Event event)
    {
        this.event = event;
    }
}
