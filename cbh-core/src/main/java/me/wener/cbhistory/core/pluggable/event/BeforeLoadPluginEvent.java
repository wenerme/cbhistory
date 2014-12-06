package me.wener.cbhistory.core.pluggable.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BeforeLoadPluginEvent extends PluginEvent
{
    boolean canceled = false;
}
