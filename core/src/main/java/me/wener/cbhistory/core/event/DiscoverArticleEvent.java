package me.wener.cbhistory.core.event;

import lombok.Getter;

public class DiscoverArticleEvent extends AbstractEvent
{
    @Getter
    private String content;

    public DiscoverArticleEvent(String content)
    {
        this.content = content;
    }
}
