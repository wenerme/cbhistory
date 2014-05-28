package me.wener.cbhistory.core;

import com.google.inject.Injector;
import me.wener.cbhistory.core.event.process.TryDiscoverArticleByUrlEvent;
import me.wener.cbhistory.core.event.process.TryFoundArticleEvent;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GuiceEventTest
{
    @Test
    public void test() throws InterruptedException
    {
        Injector injector = App.getInjector();
        TryDiscoverArticleByUrlEvent event = new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/");
        Events.post(event);
        Thread.sleep(60000);
    }
    @Test
    public void testSingle() throws InterruptedException
    {
        TryFoundArticleEvent event = new TryFoundArticleEvent(293093l);
        Injector injector = App.getInjector();
//        TryDiscoverArticleByUrlEvent event = new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/");
        Events.post(event);
        Thread.sleep(50000);
    }
}
