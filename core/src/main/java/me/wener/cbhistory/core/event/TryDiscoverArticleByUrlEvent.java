package me.wener.cbhistory.core.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
