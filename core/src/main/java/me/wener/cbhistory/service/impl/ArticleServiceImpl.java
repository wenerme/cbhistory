package me.wener.cbhistory.service.impl;

import com.google.inject.Inject;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.service.ArticleService;

public class ArticleServiceImpl
        extends BasicServiceImpl<Article, Long>
        implements ArticleService
{
    @Inject
    public ArticleServiceImpl(ConnectionSource connectionSource) throws SQLException
    {
        super(connectionSource, Article.class);
    }
}
