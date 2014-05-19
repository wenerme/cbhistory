package study;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mycila.guice.ext.closeable.CloseableModule;
import com.mycila.guice.ext.jsr250.Jsr250Module;
import java.io.IOException;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.core.modules.ChainInjector;
import me.wener.cbhistory.core.modules.OrmlitePersistModule;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.service.ArticleService;
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

        Article article = articleSvc.findOne(id);

        System.out.println(article.getComments().size());
        System.out.println(article);
        RawComment rawComment = CBHistory.getRawCommentFrom(article);
        System.out.println(rawComment);

        RawData rawData = CBHistory.getRawDataFrom(article);
        System.out.println(rawData);
    }

    public void testSource()
    {

    }
}
