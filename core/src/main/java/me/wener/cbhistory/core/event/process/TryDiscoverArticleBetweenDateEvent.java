package me.wener.cbhistory.core.event.process;

import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.event.Event;
import org.joda.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TryDiscoverArticleBetweenDateEvent implements Event
{
    private LocalDateTime start;
    private LocalDateTime end;
}
