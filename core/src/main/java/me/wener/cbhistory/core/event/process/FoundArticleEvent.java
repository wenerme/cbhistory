package me.wener.cbhistory.core.event.process;

import lombok.ToString;
import me.wener.cbhistory.core.event.WithContentEvent;

@ToString(callSuper = true)
public class FoundArticleEvent extends WithContentEvent<Long>
{
    public FoundArticleEvent(Long id, String content)
    {
        setContent(content);
        setId(id);
    }
}
