package me.wener.cbhistory.core.event;

import java.util.Arrays;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

/**
 * 不要再使用该事件来持久化实体,操作过程过长,直接使用repo
 */
@Deprecated
public class PersistEntityEvent extends AbstractEvent
{
    @Getter
    private Iterable<? extends Persistable> entities;

    @SafeVarargs
    public <E extends Persistable> PersistEntityEvent(E ... entities)
    {
        this.entities = Arrays.asList(entities);
    }

    public <E extends Persistable> PersistEntityEvent(Iterable<E> entities)
    {
        this.entities = entities;
    }
}
