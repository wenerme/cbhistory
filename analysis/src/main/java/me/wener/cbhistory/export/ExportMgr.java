package me.wener.cbhistory.export;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.transaction.Transactional;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Transactional
public class ExportMgr extends AbstractExporter implements ApplicationContextAware
{
    private ApplicationContext ctx;
    private List<Exporter> exporters = Lists.newArrayList();

    @Inject
    protected ArticleRepo articleRepo;
    @Inject
    protected CommentRepo commentRepo;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        ctx = applicationContext;
    }

    @Override
    public String getCode()
    {
        return "data";
    }

    @Override
    public String getTitle()
    {
        return "cbhistory 数据统计";
    }

    public void addExporter(Exporter exporter)
    {
        exporters.add(exporter);

    }
    public void addExporter(Class<? extends Exporter> type)
    {
        addExporter(getInstance(type));
    }
    protected<T> T getInstance(Class<? extends T> type)
    {
        return ctx.getAutowireCapableBeanFactory().createBean(type);
    }

    @Override
    public void doExport(LocalDateTime startExportTime, LocalDateTime endExportTime)
    {
        List<String> codes = Lists.newArrayList();
        // 逐个导出
        {
            for (Exporter exporter : exporters)
            {
                codes.add(exporter.getCode());

                exporter.setBasePath(getBasePath());
                exporter.setLimit(getLimit());

                exporter.doExport(startExportTime,endExportTime);
            }
        }
        DataOverview overview = new DataOverview();
        overview.setAuthor("wener");
        overview.setAuthorEmail("wenermail(AT)gmail.com");

        overview.setArticleCount(articleRepo.count());
        overview.setSourceCount(articleRepo.countOfSource());
        overview.setCommentCount(commentRepo.count());
        overview.setAreaCount(commentRepo.countOfArea());
        overview.setAllCommentCount(articleRepo.countOfAllDiscuss());
        overview.setCommentGatherRatio((double)overview.getCommentCount()/overview.getAllCommentCount());

        overview.setFirstArticleDate(articleRepo.firstArticleDate().toDate());
        overview.setFirstCommentDate(commentRepo.firstCommentDate().toDate());
        overview.setGenerateDate(new Date());

        overview.setFirstArticleHaveCommentDate(commentRepo.firstComment().getArticle().getDate().toDate());

        // 导出信息
        Map<String, Object> info = getInfo();
        info.put("codes", codes);
        info.put("info", overview);
        save("info", info);
    }
}
