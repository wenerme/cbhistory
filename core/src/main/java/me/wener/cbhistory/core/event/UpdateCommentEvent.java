package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.ToString;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.RawData;

@ToString
public class UpdateCommentEvent extends AbstractEvent
{
    @Getter
    private Article article;
    @Getter
    private RawData rawContent;

    public UpdateCommentEvent(Article article, RawData content)
    {
        this.article = article;
        this.rawContent = content;
    }
    public UpdateCommentEvent(TryUpdateCommentEvent e, RawData content)
    {
        this.article = e.getArticle();
        this.rawContent = content;
    }
}
