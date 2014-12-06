package me.wener.cbhistory.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.parser.RawData;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import me.wener.cbhistory.service.RawDataService;

@Singleton
public class RawDataServiceCacheImpl implements RawDataService
{
    private static Cache<String, RawData> dataCache = null;
    static
    {
        // 初始化缓存对象
        getDataCache();
    }
    @Inject
    private ArticleService articleSvc;
    @Inject
    private CommentService commentSvc;

    public static Cache<String, RawData> getDataCache()
    {
        if (dataCache == null)
        {
            dataCache = CacheBuilder
                    .newBuilder()
                            // 最多缓存 1000 条
                    .maximumSize(1000)
                            // 读取后 10 分钟失效
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .build();
        }
        return dataCache;
    }

    @Override
    public void delete(Long id)
    {
        String key;
        int page = 1;
        key = getKey(id, page);

        do
        {
            dataCache.invalidate(key);
            page++;
            key = getKey(id, page);
        } while (dataCache.getIfPresent(key) != null);
    }

    @Override
    public RawData findBySid(Long id, int page)
    {
        if (id == null)
            return null;
        String key = getKey(id, page);
        RawData data = dataCache.getIfPresent(key);
        if (data != null)
            return data;

        Article article = articleSvc.findOne(id);
        if (article == null)
            return null;

        // 放入缓存
        data = CBHistory.getRawDataFrom(article, commentSvc.findAllBySid(id, page));
        dataCache.put(key, data);

        return data;
    }

    private String getKey(Long id, int page)
    {
        String key = "%s_%s";
        key = String.format(key, id, page);
        return key;
    }
}
