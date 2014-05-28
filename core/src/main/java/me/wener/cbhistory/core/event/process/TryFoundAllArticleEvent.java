package me.wener.cbhistory.core.event.process;

import java.util.Collection;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.event.Event;

@ToString
@Data
@Accessors(chain = true)
public class TryFoundAllArticleEvent implements Event
{
    private Collection<Long> ids;
    private String description = null;
}
