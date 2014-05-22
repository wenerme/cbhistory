package me.wener.cbhistory.core.event;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TryDiscoverArticleBetweenDateEvent implements Event
{
    private LocalDateTime start;
    private LocalDateTime end;
}
