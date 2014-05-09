package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.ToString;
import me.wener.cbhistory.domain.Article;

@ToString
public class TryUpdateCommentEvent extends AbstractEvent
{
    @Getter
    private Article article;

    public TryUpdateCommentEvent(Article article)
    {
        this.article = article;
    }
}
