package me.wener.cbhistory.core.event;

import java.util.Collection;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
public class TryFoundAllArticleEvent implements Event
{
    private Collection<Long> ids;
    private String description = null;
}
