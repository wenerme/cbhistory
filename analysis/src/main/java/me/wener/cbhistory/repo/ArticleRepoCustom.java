package me.wener.cbhistory.repo;

import java.util.Map;

public interface ArticleRepoCustom
{
    /**
     * 根据文章发布源统计文章发布数量
     */
    Map<String, Long> countPreSource();

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
}
