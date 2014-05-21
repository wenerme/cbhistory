package me.wener.cbhistory.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import me.wener.cbhistory.core.event.DiscoverArticleEvent;
import me.wener.cbhistory.core.event.FoundArticleEvent;
import me.wener.cbhistory.core.event.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.TryUpdateCommentEvent;
import me.wener.cbhistory.core.event.UpdateCommentEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.RawData;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestEvents
{
    @Test
    public void testDiscoverArticle() throws IOException
    {
        DiscoverArticleEvent event = new DiscoverArticleEvent(Files.toString(new File("C:\\site.html"), Charsets.UTF_8));
        Events.post(event);
    }

    @Test
    public void testParseOpAndGetComment() throws InterruptedException
    {
        Article detail = new Article();
        detail.setSid(287931l);
        detail.setSn("9524c");

        Events.post(new TryUpdateCommentEvent(detail));
        Thread.sleep(50000);
    }

    @Test
    public void testParseComment() throws Exception
    {
        Article detail = new Article();
        detail.setSid(287931l);
        detail.setSn("9524c");
        RawData raw = new Gson().fromJson(Files.toString(new File("C:\\cmt.json"), Charsets.UTF_8), RawData.class);

        Events.post(new UpdateCommentEvent(detail,raw));
        Thread.sleep(50000);
    }

    @Test
    public void testRealArticle()
    {
        Events.post(new TryFoundArticleEvent("287961"));
    }

    public static void main(String[] args) throws IOException
    {
        new TestEvents()
//                .testDiscoverArticle()
        .testRealArticle()
        ;
    }
}
