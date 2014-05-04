package me.wener.cbhistory.core.event;

import java.util.Arrays;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

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
