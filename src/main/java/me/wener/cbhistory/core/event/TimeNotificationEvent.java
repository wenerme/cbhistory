package me.wener.cbhistory.core.event;


import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class TimeNotificationEvent extends AbstractEvent
{
    @Getter
    private int totalMinutes;

    public TimeNotificationEvent(Integer totalMinutes)
    {
        this.totalMinutes = totalMinutes;
    }
}
