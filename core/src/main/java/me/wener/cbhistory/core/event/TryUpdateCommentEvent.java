package me.wener.cbhistory.core.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.domain.Article;

@ToString
@Accessors(chain = true)
public class TryUpdateCommentEvent extends AbstractEvent
{
    @Getter
    private Article article;
    @Setter @Getter
    private int page = 1;

    public TryUpdateCommentEvent(Article article)
    {
        this.article = article;
    }
}
