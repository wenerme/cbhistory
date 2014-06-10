package me.wener.cbhistory.modules;

import com.google.common.reflect.Reflection;
import javax.inject.Inject;
import javax.sql.DataSource;
import me.wener.cbhistory.core.pluggable.PlugInfo;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.persistence.mybatis.DateTimeTypeHandler;
import me.wener.cbhistory.persistence.mybatis.LocalDateTimeTypeHandler;
import me.wener.cbhistory.persistence.mybatis.mappers.ArticleMapper;
import me.wener.cbhistory.persistence.mybatis.mappers.CommentMapper;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.mybatis.guice.MyBatisModule;

@PlugInfo(name = "MyBatis 持久层插件", author = "wener<wenermail@gmail.com>")
public class MyBatisPersistenceModule extends MyBatisModule implements IPlugin
{
    @Inject
    DataSource dataSource;

    @Override
    protected void initialize()
    {
//        addMapperClasses(Reflection.getPackageName(ArticleMapper.class));
//        addSimpleAliases(Reflection.getPackageName(Article.class));

        addMapperClass(ArticleMapper.class);
        addMapperClass(CommentMapper.class);

        addSimpleAlias(Article.class);
        addSimpleAlias(Comment.class);

        handleType(DateTime.class).with(DateTimeTypeHandler.class);
        handleType(LocalDateTime.class).with(LocalDateTimeTypeHandler.class);

        bindTransactionFactoryType(JdbcTransactionFactory.class);
    }

    @Override
    public void init()
    {}
}
