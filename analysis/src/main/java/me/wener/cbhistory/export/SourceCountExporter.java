package me.wener.cbhistory.export;

import java.util.LinkedHashMap;
import javax.inject.Inject;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import org.joda.time.LocalDateTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SourceCountExporter extends AbstractExporter
{
    @Inject
    protected ArticleRepo articleRepo;
    @Inject
    protected CommentRepo commentRepo;

    @Override
    public String getCode()
    {
        return "source-count";
    }

    @Override
    public String getTitle()
    {
        return "发布者统计";
    }

    @Override
    public void doExport(LocalDateTime startExportTime, LocalDateTime endExportTime)
    {
        {
            final long count = articleRepo.count();
            LinkedHashMap<String, Long> sourceDesc = articleRepo.countPreSourceDesc();
            export("total", "总计", asPieCount(sourceDesc, count, getLimit()));
        }
        // 统计每个月
        {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
            YearMonth start = new YearMonth(startExportTime);
            YearMonth end = new YearMonth(endExportTime);
            for (; !start.equals(end); start = start.plusMonths(1))
            {
                LocalDateTime startTime = new LocalDateTime(start.getYear(), start.getMonthOfYear(), 1, 0, 0);
                LocalDateTime endTime = startTime.plusMonths(1);
                long count = articleRepo.countByDateBetween(startTime, endTime);

                Object data = asPieCount(articleRepo.countPreSourceDesc(startTime, endTime), count, getLimit());
                export(formatter.print(start), null, data);
            }
        }
        // 导出信息
        save("info", getInfo());
    }

}
