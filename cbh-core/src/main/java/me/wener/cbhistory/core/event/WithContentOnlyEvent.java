package me.wener.cbhistory.core.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public abstract class WithContentOnlyEvent implements Event
{
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private String content;
}
