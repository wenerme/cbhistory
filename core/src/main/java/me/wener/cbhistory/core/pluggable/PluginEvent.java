package me.wener.cbhistory.core.pluggable;

import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.modules.IPlugin;

@Accessors(chain = true)
@Data
public class PluginEvent implements Event
{
    private Class<? extends IPlugin> plugin;

    public <T extends PluginEvent> T to(Class<T> type)
    {
        T event;
        try
        {
            event = type.newInstance();
            event.setPlugin(getPlugin());
        } catch (Exception e)
        {
            throw new RuntimeException("创建事件实例的时候失败:", e);
        }
        return event;
    }
}
