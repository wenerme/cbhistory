package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.ToString;

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
