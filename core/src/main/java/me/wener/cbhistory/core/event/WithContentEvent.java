package me.wener.cbhistory.core.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@ToString(exclude = "content")
public abstract class WithContentEvent<ID> extends WithIdentityEvent<ID>
{
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private String content;
}
