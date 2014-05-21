package study;

import com.google.inject.Injector;
import java.io.IOException;
import java.util.Collection;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import org.junit.Ignore;
import org.junit.Test;

public class StudyOrmlite
{
    @Test
    @Ignore
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

    public void testSource()
    {

    }
}
