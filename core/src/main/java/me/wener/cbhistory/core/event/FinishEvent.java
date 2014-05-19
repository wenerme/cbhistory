package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.ToString;

@ToString
public class FinishEvent extends AbstractEvent
{
    @Getter
    private Event event;

    public FinishEvent(Event event)
    {
        this.event = event;
    }
}
