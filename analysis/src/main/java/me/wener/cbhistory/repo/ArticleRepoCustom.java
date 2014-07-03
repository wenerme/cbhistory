package me.wener.cbhistory.repo;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public interface ArticleRepoCustom
{
    /**
     * 根据文章发布源统计文章发布数量, 返回的是 LinkedHashMap, 因此是有序的
     */
    LinkedHashMap<String, Long> countPreSourceDesc();

    /**
     * 根据文章发布源统计文章发布数量, 返回的是 LinkedHashMap, 因此是有序的
     */
    LinkedHashMap<String, Long> countPreSourceDesc(@Nullable LocalDateTime start,@Nullable  LocalDateTime end);

    /**
     * 根据每月中的天数统计发布数量
     */
    Map<Integer, Long> countPreDay();
    /**
     * 根据每月中统计发布数量
     */
    Map<Integer, Long> countPreMonth();

    /**
     * 收集率
     */
    double collectionRate();

    LinkedHashMap<Date, Long> countBySourceGroupByDateOrderByDateDesc(String source);
}
