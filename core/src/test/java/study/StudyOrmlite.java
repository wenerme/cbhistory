package study;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import me.wener.cbhistory.core.modules.PersistModule;
import me.wener.cbhistory.core.modules.PropertiesModule;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.service.ArticleService;
import org.junit.Test;

public class StudyOrmlite
{
    @Test
    public void testTable() throws IOException
    {
        Injector injector = Guice.createInjector(
                new PropertiesModule().withOptionalResource("default.properties","db.properties"),
                new PersistModule());
        ArticleService articleSvc = injector.getInstance(ArticleService.class);
        ArticleService articleSvcDup = injector.getInstance(ArticleService.class);

        assert articleSvc != null;
        assert articleSvc == articleSvcDup;


    }
}
