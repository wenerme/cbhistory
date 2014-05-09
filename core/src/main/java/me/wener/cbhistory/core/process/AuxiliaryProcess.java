package me.wener.cbhistory.core.process;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.core.event.ExceptionEvent;
import me.wener.cbhistory.core.event.FinishEvent;
import me.wener.cbhistory.core.event.StartEvent;

/**
 * 处理辅助性事件,主要是记录日志
 */
@Named
@Slf4j
public class AuxiliaryProcess
{

    // region 服务性的事件处理

    @Subscribe
    @AllowConcurrentEvents
    public void logEvents(Event e)
    {
        if (log.isDebugEnabled())
        {
            if (e instanceof StartEvent)
                log.debug("开始事件 {}" , e);
            else if (e instanceof FinishEvent)
                log.debug("结束事件 {}", e);

        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void unwrapStartEvent(StartEvent e)
    {
        Event event = e.getEvent();
        checkNotNull(event);
        Events.post(event);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void catchDeadEvent(DeadEvent e)
    {
        log.warn("检测到未处理的事件 event:{} source:{}", e.getEvent(), e.getSource());
    }

    @Subscribe
    public void handleException(ExceptionEvent e)
    {
        if (log.isErrorEnabled())
        {
            final String format = "当前事件为: %s\n当前订阅者为: %s\n订阅方法为: %s\nEventBus为: %s\n";
            String msg = "\n";
            log.error("检测到异常 :", e.getException());

            msg += "===================================\n";
            msg += "异常详细信息:\n";

            SubscriberExceptionContext ctx = e.getContext();
            if (ctx != null)
            {
                msg += String.format(format, ctx.getEvent(), ctx.getSubscriber(), ctx.getSubscriberMethod(), ctx.getEventBus());
            } else
                msg += "无上下文信息.\n";

            if (e.getExtra() != null)
            {
                msg += String.format("附加信息为: %s \n", e.getExtra());
            } else
                msg += "无附加信息.\n";

            msg += "===================================";
            log.error(msg);
        }
    }
// endregion

}
