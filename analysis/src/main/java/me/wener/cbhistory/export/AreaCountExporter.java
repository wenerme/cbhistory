package me.wener.cbhistory.export;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import org.joda.time.LocalDateTime;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AreaCountExporter extends AbstractExporter
{
    @Inject
    protected ArticleRepo articleRepo;
    @Inject
    protected CommentRepo commentRepo;
    Pattern regProvince = Pattern.compile("^(?<province>.*?省)");



    @Override
    public String getCode()
    {
        return "area-count";
    }

    @Override
    public String getTitle()
    {
        return "喷子密集度统计";
    }

    protected LinkedHashMap<String, Long> reduceToTopArea(LinkedHashMap<String, Long> data)
    {
        Map<String, Long> map = Maps.newHashMap();
        for (Map.Entry<String, Long> entry : data.entrySet())
        {
            String key = entry.getKey();

            String area = getTopArea(key);
            map.put(area, entry.getValue() + (map.containsKey(area) ? map.get(area) : 0));
        }

        TreeMap<String, Long> treeMap = new TreeMap<>(new NumberValueComparator<>(map).setReverse(true));
        treeMap.putAll(map);

        return Maps.newLinkedHashMap(treeMap);
    }

    final static protected Map<String, String> topAreaPrefix = Maps.newHashMap();

    static
    {
        topAreaPrefix.put("广西", "省");
        topAreaPrefix.put("北京", "市");
        topAreaPrefix.put("上海", "市");
        topAreaPrefix.put("天津", "市");
        topAreaPrefix.put("新疆", "省");

        topAreaPrefix.put("内蒙古", "内蒙古自治区");
        topAreaPrefix.put("宁夏", "宁夏回族自治区");

        for (Map.Entry<String, String> entry : topAreaPrefix.entrySet())
        {
            // 将单个值的拼接键值
            if (entry.getValue().length() < 2)
            {
                entry.setValue(entry.getKey()+entry.getValue());
            }
        }
    }

    /**
     * 获取顶级区域
     */
    public String getTopArea(String area)
    {
        if (Strings.isNullOrEmpty(area))
            return area;

        Matcher matcher = regProvince.matcher(area);
        if (matcher.find())
        {
            return matcher.group("province");
        }

        for (Map.Entry<String, String> entry : topAreaPrefix.entrySet())
        {
            if (area.startsWith(entry.getKey()))
            {
                return entry.getValue();
            }
        }

        return area;
    }

    /**
     * 使用前缀来过滤
     */
    public LinkedHashMap<String, Long> filterByPrefix(LinkedHashMap<String, Long> data, String prefix)
    {
        LinkedHashMap<String, Long> map = Maps.newLinkedHashMap();

        for (Map.Entry<String, Long> entry : data.entrySet())
        {
            if (entry.getKey() != null && entry.getKey().startsWith(prefix))
            {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public void doExport(LocalDateTime startExportTime, LocalDateTime endExportTime)
    {
        {

            LinkedHashMap<String, Long> data = asLinkedHashMap(commentRepo.areaCount());
            long count = commentRepo.countByHostNameNotNull();
            export("total", "总计", asPieCount(data, count, getLimit()));

            // 顶级区域
            export("total-province", "一级区域", asPieCount(reduceToTopArea(data), count, getLimit()));

            // 热门地区
            export("total-sh", "上海", asPieCount(filterByPrefix(data,"上海")));
            export("total-bj", "北京", asPieCount(filterByPrefix(data,"北京")));
            export("total-gd", "广东", asPieCount(filterByPrefix(data,"广东")));
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
                long count = commentRepo.countByHostNameIsNotNullAndDateBetween(startTime, endTime);

                LinkedHashMap<String, Long> srcData = asLinkedHashMap(commentRepo.areaCount(startTime, endTime));
                String category = formatter.print(start);
                export(category, category + "-前十五", asPieCount(reduceToTopArea(srcData), count, getLimit()));
            }

        }
        save("info", getInfo());
    }
}
