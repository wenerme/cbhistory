package me.wener.cbhistory.export;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import me.wener.cbhistory.export.LabelValue;
import me.wener.cbhistory.spring.SpringContextConfig;
import me.wener.cbhistory.utils.Same;
import org.joda.time.LocalDateTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextConfig.class)
//@Ignore
public class TextManultExport
{
    @Inject
    ApplicationContext ctx;
    @Inject
    ArticleRepo articleRepo;
    @Inject
    CommentRepo commentRepo;
    @PersistenceContext
    EntityManager entityManager;
    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    static
    {
        App.getInjector();
    }

    @Test
    public void test()
    {
        // key, values
        // 导出所有文章发布源, 以天分组
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, Map<String, Long>> countDate = Maps.newHashMap();

        {
//            String source = allSource.get(1);
            String source = "安兔兔";
            Map<Date, Long> dateCount = articleRepo.countBySourceGroupByDateOrderByDateDesc(source);
            System.out.println(dateCount);
            Map<String, Long> dateStrCount = Maps.newHashMap();
            for (Map.Entry<Date, Long> entry : dateCount.entrySet())
            {
                dateStrCount.put(dateParser.format(entry.getKey()), entry.getValue());
            }
        }
    }

    @Test
    public void exportAll() throws IOException
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

        Map<String, Map<String, Long>> countDate = Maps.newHashMap();
        List<String> allSource = articleRepo.findAllSource();
        for (String source : allSource)
        {
            Map<Date, Long> dateCount = articleRepo.countBySourceGroupByDateOrderByDateDesc(source);
            System.out.println(dateCount);
            Map<String, Long> dateStrCount = Maps.newHashMap();
            for (Map.Entry<Date, Long> entry : dateCount.entrySet())
            {
                if (entry.getKey() != null)
                    dateStrCount.put(dateParser.format(entry.getKey()), entry.getValue());
                else
                    System.out.println("NULL: " + entry);
            }

            countDate.put(source, dateStrCount);
        }

        Files.write(Same.getGson().toJson(countDate), new File("C:\\data.json"), Charsets.UTF_8);
    }

    @Test
    public void sourceTotal() throws IOException
    {
        final long count = articleRepo.count();
        LinkedHashMap<String, Long> sourceDesc = articleRepo.countPreSourceDesc();
        List<LabelValue> result = Lists.newArrayList();
        int i = 0;
        int sum = 0;
        for (final Map.Entry<String, Long> entry : sourceDesc.entrySet())
        {
            if (i++ == 10)
                break;
            sum += entry.getValue();
            result.add(new LabelValue(entry.getKey(), entry.getValue()));
        }

        result.add(new LabelValue("其他", count - sum));

        Files.write(Same.getGson().toJson(result), new File("C:\\total-top-source-count.json"), Charsets.UTF_8);
    }

    @Test
    public void sourceCount() throws IOException
    {
        {
            final long count = articleRepo.count();
            LinkedHashMap<String, Long> sourceDesc = articleRepo.countPreSourceDesc();

            Files.write(Same.getGson().toJson(asSourceCount(sourceDesc, count)),
                    new File("C:\\source-count-total.json"), Charsets.UTF_8);
        }
        // 统计每个月
        {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM");
            YearMonth start = new YearMonth(articleRepo.firstArticleDate());
            YearMonth end = YearMonth.now();
            for (; !start.equals(end); start = start.plusMonths(1))
            {
                LocalDateTime startTime = new LocalDateTime(start.getYear(), start.getMonthOfYear(), 1, 0, 0);
                LocalDateTime endTime = startTime.plusMonths(1);
                long count = articleRepo.countByDateBetween(startTime, endTime);

                Object data = asSourceCount(articleRepo.countPreSourceDesc(startTime, endTime), count);
                Files.write(Same.getGson().toJson(data),
                        new File("C:\\source-count-"+formatter.print(start)+".json"), Charsets.UTF_8);
            }
        }
    }

    public Object asSourceCount(LinkedHashMap<String, Long> sourceDesc, long count)
    {
        List<LabelValue> result = Lists.newArrayList();
        int i = 0;
        int sum = 0;
        for (final Map.Entry<String, Long> entry : sourceDesc.entrySet())
        {
            if (i++ == 10)
                break;
            sum += entry.getValue();
            result.add(new LabelValue(entry.getKey(), entry.getValue()));
        }

        result.add(new LabelValue("其他", count - sum));
        return result;
    }

}
