package me.wener.cbhistory.service.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.service.ArticleService;

public class ArticleServiceImpl
        extends BasicServiceImpl<Article, Long>
        implements ArticleService
{
    public ArticleServiceImpl() throws SQLException
    {
        super(Article.class);
    }

}
