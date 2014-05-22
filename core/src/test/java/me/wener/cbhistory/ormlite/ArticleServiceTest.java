package me.wener.cbhistory.ormlite;

import com.google.inject.Injector;
import java.util.List;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.util.Same;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.junit.Test;

public class ArticleServiceTest
{
    @Test
    public void testBetween()
    {
        Injector injector = App.getInjector();
        ArticleService articleSvc = injector.getInstance(ArticleService.class);
        // '2014-5-15' and '2014-5-16';
        List<Article> list = articleSvc.findAllByDateBetween(new LocalDateTime("2014-5-15"),
                new LocalDateTime("2014-5-16"));

        System.out.println(list.size());
        assert list.size() > 0;

        // '2014-5-15 20:00' and '2014-5-16 12:00'
        List<Article> newList = articleSvc.findAllByDateBetween(
                Same.getDateTimeFormatter().parseLocalDateTime("2014-5-15 20:00"),
                Same.getDateTimeFormatter().parseLocalDateTime("2014-5-16 12:00"));
        assert newList.size() > 0;

        System.out.println(newList.size());
        // 确保 时间也是被查询了的
        assert newList.size() != list.size();
    }
    @Test
    public void testDateFormat()
    {
        // this is ok
        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2014-5-15 20:00:00");
        DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2014-5-15");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//                .append(DateTimeFormat.forPattern("yyyy-MM-dd"))
                .append(null,
                        new DateTimeParser[]{DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser()})
                .toFormatter();

        try
        {
            formatter.parseLocalDateTime("2014-5-15 20:00:00");
        }catch (Exception ignored){System.out.println("Failed A:"+ignored.getMessage());}
        try
        {
            formatter.parseLocalDateTime("2014-05-15 20:00:00");
        }catch (Exception ignored){System.out.println("Failed B:"+ignored.getMessage());}
        try
        {
            formatter.parseLocalDateTime("2014-5-15");
        }catch (Exception ignored){System.out.println("Failed C:"+ignored.getMessage());}
        try
        {
            formatter.parseLocalDateTime("2014-05-15");
        }catch (Exception ignored){System.out.println("Failed D:"+ignored.getMessage());}
    }
}
