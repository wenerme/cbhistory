package me.wener.cbhistory.util;

import lombok.NonNull;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.Event;

public class ExtensionMethods
{
    public static String format(@NonNull String format, Object... args)
    {
        return String.format(format, args);
    }

    public static Event post(@NonNull Event e)
    {
        Events.post(e);
        return e;
    }

    public static <T> T or(T obj, T ifNull)
    {
        return obj != null ? obj : ifNull;
    }
}
