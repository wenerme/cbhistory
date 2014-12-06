package me.wener.cbhistory.core.event.process;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.event.AbstractEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.parser.Response;

@ToString
@Accessors(chain = true)
public class UpdateCommentEvent extends AbstractEvent
{
    @Getter
    private Article article;
    @Getter
    private Response content;

    @Setter
    @Getter
    private int page = 1;

    public UpdateCommentEvent(Article article, Response content)
    {
        this.article = article;
        this.content = content;
    }
}
