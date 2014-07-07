package me.wener.cbhistory.export;

import java.util.LinkedHashMap;
import javax.inject.Inject;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import me.wener.cbhistory.repo.Repos;
import org.joda.time.LocalDateTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PublisherActiveTimeExporter extends AbstractExporter
{
    @Inject
    protected ArticleRepo articleRepo;
    @Inject
    protected CommentRepo commentRepo;

    @Override
    public String getCode()
    {
        return "publisher-active-time";
    }

    @Override
    public String getTitle()
    {
        return "发布者活跃时间段";
    }

    @Override
    public void doExport(LocalDateTime startExportTime, LocalDateTime endExportTime)
    {
        {
            LinkedHashMap<Integer, Long> data = Repos.asLinkedHashMap(articleRepo.hourCount());
            export("total", "总计", asPieCount(data));
            export("total-cb", "cnBeta.COM", asPieCount(Repos.<Integer, Long>asLinkedHashMap(articleRepo
                    .hourCountBySource("cnBeta.COM"))));
            export("total-txkj", "腾讯科技", asPieCount(Repos.<Integer, Long>asLinkedHashMap(articleRepo
                    .hourCountBySource("腾讯科技"))));
            export("total-txsm", "腾讯数码", asPieCount(Repos.<Integer, Long>asLinkedHashMap(articleRepo
                    .hourCountBySource("腾讯数码"))));
            export("total-qdzj", "驱动之家", asPieCount(Repos.<Integer, Long>asLinkedHashMap(articleRepo
                    .hourCountBySource("驱动之家"))));
        }
        {
            // 导出每个月
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
            YearMonth start = new YearMonth(startExportTime);
            YearMonth end = new YearMonth(endExportTime);
            for (; !start.equals(end); start = start.plusMonths(1))
            {
                LocalDateTime startTime = new LocalDateTime(start.getYear(), start.getMonthOfYear(), 1, 0, 0);
                LocalDateTime endTime = startTime.plusMonths(1);

                LinkedHashMap<Integer, Long> data = Repos.asLinkedHashMap(articleRepo.hourCount(startTime, endTime));

                String category = formatter.print(start);
                export(category, null, asPieCount(data));
            }
        }
        save("info", getInfo());
    }
}
