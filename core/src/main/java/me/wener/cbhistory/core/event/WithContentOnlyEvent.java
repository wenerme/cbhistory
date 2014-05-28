package me.wener.cbhistory.core.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.event.WithIdentityEvent;

@Accessors(chain = true)
public abstract class WithContentOnlyEvent implements Event
{
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String content;
}
