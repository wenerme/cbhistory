package me.wener.cbhistory.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.core.event.ExceptionEvent;
import me.wener.cbhistory.core.event.FinishEvent;
import me.wener.cbhistory.core.event.StartEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 全局的事件总线,所有全局级的时间都由该对象处理.
 */
@Slf4j
public final class Events
{
    private static Events self = new Events();
    private static EventBus bus;

    private Events()
    {
    }

    static
    {
        // ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingDeque<>();
        Executor executor = new ThreadPoolExecutor(5, 300, 5, TimeUnit.MINUTES, blockingQueue);
        bus = new AsyncEventBus(executor, new SubscriberExceptionHandler()
        {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context)
            {
                ExceptionEvent event = new ExceptionEvent(exception, context);
                bus.post(event);
            }
        });
    }

    /**
     * 发布事件<br/>
     * 该操作是<b>异步</b>的,操作完后会立即返回
     */
    public static Events post(Event e)
    {
        if (log.isDebugEnabled())
            log.debug("发布事件 " + e);
        bus.post(e);
        return self;
    }

    /**
     * 注册监听对象
     */
    public static Events register(Object object)
    {
        if (log.isDebugEnabled())
            log.debug("注册监听器 " + object);
        bus.register(object);
        return self;
    }

    /**
     * 注销监听对象
     */
    public static Events unregister(Object object)
    {
        if (log.isDebugEnabled())
            log.debug("注销监听器 " + object);
        bus.unregister(object);
        return self;
    }

    /**
     * 将该事件封装为一个 {@link me.wener.cbhistory.core.event.StartEvent}
     */
    public static Events start(Event e)
    {
        post(new StartEvent(e));
        return self;
    }

    /**
     * 将该事件封装为一个 {@link me.wener.cbhistory.core.event.FinishEvent}
     */
    public static Events finish(Event e)
    {
        post(new FinishEvent(e));
        return self;
    }

}
