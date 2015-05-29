package me.wener.cbhistory.core.event.process;

import lombok.Getter;
import lombok.ToString;
import me.wener.cbhistory.core.event.AbstractEvent;

@ToString
public class TryFoundArticleEvent extends AbstractEvent
{
    @Getter
    private String articleId;

    public TryFoundArticleEvent(String id)
    {
        this.articleId = id;
    }

    public TryFoundArticleEvent(Long id)
    {
        this.articleId = id.toString();
    }
}
