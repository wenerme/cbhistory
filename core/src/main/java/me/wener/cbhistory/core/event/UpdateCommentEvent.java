package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.RawData;

@ToString
@Accessors(chain = true)
public class UpdateCommentEvent extends AbstractEvent
{
    @Getter
    private Article article;
    @Getter
    private RawData rawContent;

    @Setter
    @Getter
    private int page = 1;

    public UpdateCommentEvent(Article article, RawData content)
    {
        this.article = article;
        this.rawContent = content;
    }
}
