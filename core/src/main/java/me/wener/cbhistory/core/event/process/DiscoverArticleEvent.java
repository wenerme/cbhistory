package me.wener.cbhistory.core.event.process;

import lombok.Getter;
import me.wener.cbhistory.core.event.AbstractEvent;
import me.wener.cbhistory.core.event.WithContentOnlyEvent;

public class DiscoverArticleEvent extends WithContentOnlyEvent
{
    public DiscoverArticleEvent(String content)
    {
        this.setContent(content);
    }
}
