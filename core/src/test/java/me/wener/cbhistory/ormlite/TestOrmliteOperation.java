package me.wener.cbhistory.ormlite;

import com.google.inject.Injector;
import java.io.IOException;
import java.util.Collection;
import javax.sql.DataSource;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.utils.Same;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

public class TestOrmliteOperation
{
    @Test
    @Ignore
    public void testSource()
    {
        Injector injector = App.getInjector();
        DataSource source = injector.getInstance(DataSource.class);
        ArticleService articleService = injector.getInstance(ArticleService.class);
        assert articleService.count() > 0;
    }
    @Test
    public void testTable() throws IOException
    {
        Injector injector = App.getInjector();
        ArticleService articleSvc = injector.getInstance(ArticleService.class);
        ArticleService articleSvcDup = injector.getInstance(ArticleService.class);

        assert articleSvc != null;
        assert articleSvc == articleSvcDup;

        System.out.println(articleSvc.count());
    }

    @Test
    @Ignore
    public void testQuery() throws IOException
    {
        long id = 287625;

        Injector injector = App.getInjector();

        ArticleService articleSvc = injector.getInstance(ArticleService.class);
        CommentService commentService = injector.getInstance(CommentService.class);

        Article article = articleSvc.findOne(id);
        Collection<Comment> comments = commentService.findAllBySid(article.getSid());

        System.out.println(comments.size());
        System.out.println(article);
        RawComment rawComment = CBHistory.getRawCommentFrom(article, comments);
        System.out.println(rawComment);

        RawData rawData = CBHistory.getRawDataFrom(article, comments);
        System.out.println(rawData);
    }

    @Test
    @Ignore
    public void testOrmliteWorkWithJodaDate() throws NoSuchFieldException
    {
        long id = 287625;

        Injector injector = App.getInjector();
        ArticleService articleSvc = injector.getInstance(ArticleService.class);

        Article article = articleSvc.findOne(id);
        System.out.println(article.getDate());

        assert article.getDate() != null;
        assert LocalDateTime.now().minusDays(200).isBefore(article.getDate());
        assert article.getLastUpdateDate() != null;
        LocalDateTime now = LocalDateTime.now();
        now.minusMillis(now.getMillisOfSecond());
        article.setLastUpdateDate(now);
        articleSvc.save(article);
        article = articleSvc.findOne(id);
        // 数据库可能不会保存的那么精确
        assert  now.minusMillis(now.getMillisOfSecond()).equals(article.getLastUpdateDate())
                || article.getLastUpdateDate().equals(now);
    }

    @Test
    public void testDateParse()
    {
        String str = "2014-04-29 14:39:23.0";

        Same.getDateTimeFormatter().parseLocalDateTime(str);
    }

    @Test
    @Ignore
    public void testQueryComment()
    {
        // 该 id 有 400 多条评论
        long id = 293839;

        Injector injector = App.getInjector();

        CommentService commentService = injector.getInstance(CommentService.class);

        Collection<Comment> comments = commentService.findAllBySid(id, 1);
        assert comments.size() == 100;
    }
}
