package me.wener.cbhistory.core.process;

import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.Events;
import me.wener.cbhistory.core.event.TryDiscoverArticleBetweenDateEvent;
import me.wener.cbhistory.core.event.TryUpdateCommentEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.util.Same;
import org.junit.Test;

public class ArticleProcessTest
{
    @Test
    public void testBetween() throws InterruptedException
    {
        App.getInjector();
        TryDiscoverArticleBetweenDateEvent event = new TryDiscoverArticleBetweenDateEvent()
                .setStart(Same.getDateTimeFormatter().parseLocalDateTime("2014-5-15"))
                .setEnd(Same.getDateTimeFormatter().parseLocalDateTime("2014-5-16"));

        Events.post(event);
        Thread.sleep(2000);
    }


    @Test
    public void testParseOpAndGetComment() throws InterruptedException
    {
        App.getInjector();
        Article detail = new Article();
        detail.setSid(293837l);
        detail.setSn("152f0");

        Events.post(new TryUpdateCommentEvent(detail));
        Thread.sleep(50000);
    }
}
