package me.wener.cbhistory.core.event.process;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.wener.cbhistory.core.event.AbstractEvent;

@EqualsAndHashCode(callSuper = false)
@Data
public class TryDiscoverArticleByUrlEvent extends AbstractEvent
{
    private String url;

    public TryDiscoverArticleByUrlEvent()
    {
    }

    public TryDiscoverArticleByUrlEvent(String url)
    {
        this.url = url;
    }
}
