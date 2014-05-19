package me.wener.cbhistory.core;

import com.google.inject.Injector;
import me.wener.cbhistory.core.event.TryDiscoverArticleByUrlEvent;
import me.wener.cbhistory.core.event.TryFoundArticleEvent;
import org.junit.Test;

public class GuiceEventTest
{
    @Test
    public void test() throws InterruptedException
    {
        Injector injector = App.getInjector();
        TryDiscoverArticleByUrlEvent event = new TryDiscoverArticleByUrlEvent("http://www.cnbeta.com/");
        Events.post(event);
        Thread.sleep(2000);
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
