package me.wener.cbhistory.persistence.ormlite.service;

import com.google.inject.Inject;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import org.joda.time.LocalDateTime;

@Slf4j
public class ArticleServiceImpl
        extends BasicServiceImpl<Article, Long>
        implements ArticleService
{
    @Inject
    public ArticleServiceImpl(ConnectionSource connectionSource) throws SQLException
    {
        super(connectionSource, Article.class);
    }

    @Override
    public List<Article> findAllByDateBetween(LocalDateTime start, LocalDateTime end)
    {
        QueryBuilder<Article, Long> builder = queryBuilder();
        List<Article> list;
        try
        {
            list = builder.where()
                          .between("date", start.toString(), end.toString())
                          .query();
        } catch (SQLException e)
        {
            log.error("findAllByDateBetween({},{})", start, end);
            log.error("发生异常: ", e);
            list = Collections.emptyList();
        }
        return list;
    }
}
