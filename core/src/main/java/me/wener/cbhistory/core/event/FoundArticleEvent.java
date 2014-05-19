package me.wener.cbhistory.core.event;

import lombok.Getter;
import lombok.ToString;

@ToString(exclude = "content")
public class FoundArticleEvent extends AbstractEvent
{

    @Getter
    private Long articleId;
    @Getter
    private String content;

    public FoundArticleEvent(Long articleId, String content)
    {
        this.content = content;
        this.articleId = articleId;
    }
}
