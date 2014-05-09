package me.wener.cbhistory.core;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.eventbus.Subscribe;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.event.Event;
import me.wener.cbhistory.core.event.TimeNotificationEvent;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.MultiValueMap;

@Slf4j
@Named
public class EventScheduler
{
    @Inject
    TaskScheduler scheduler;
    private ArrayListMultimap<Event, Date> events = ArrayListMultimap.create();

    public void schedule(Runnable runnable, Date date)
    {
        scheduler.schedule(runnable, date);
    }
    public void schedule(Runnable runnable, DateTime date)
    {
        schedule(runnable, date.toDate());
    }
    public boolean schedule(Event event, Date date)
    {
        return schedule(event, date, true);
    }
    public boolean schedule(Event event, Date date, boolean noSame)
    {
        if (noSame && isScheduled(event))
        {
            if (log.isDebugEnabled())
                log.debug("事件调度已经存在,添加失败: @{} -> {}",date, event);
            return false;
        }
        if (log.isDebugEnabled())
            log.debug("添加事件调度: @{} -> {}",date, event);

        events.put(event, date);
        schedule(new EventPostRunner(event), date);
        return true;
    }
    public boolean isScheduled(Event event)
    {
        return events.containsKey(event);
    }
    public ImmutableList<Date> getScheduleDates(Event event)
    {
        return ImmutableList.copyOf(events.get(event));
    }

    static class EventPostRunner implements Runnable
    {
        Event event;

        EventPostRunner(Event event)
        {
            this.event = event;
        }

        @Override
        public void run()
        {
            Events.post(event);
        }
    }
}
