package me.wener.cbhistory.modules;

import com.google.common.eventbus.Subscribe;
import java.io.InputStream;
import java.util.Map;
import javax.inject.Inject;
import javax.sql.DataSource;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.core.pluggable.PlugInfo;
import me.wener.cbhistory.core.pluggable.event.AfterConfigureCompleteEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.persistence.mybatis.DateTimeTypeHandler;
import me.wener.cbhistory.persistence.mybatis.LocalDateTimeTypeHandler;
import me.wener.cbhistory.persistence.mybatis.mappers.ArticleMapper;
import me.wener.cbhistory.persistence.mybatis.mappers.BaseMapper;
import me.wener.cbhistory.persistence.mybatis.mappers.CommentMapper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.mybatis.guice.MyBatisModule;

@PlugInfo(name = "MyBatis 持久层插件", author = "wener<wenermail@gmail.com>")
public class MyBatisPersistenceModule extends MyBatisModule implements IPlugin
{
    public static final String XML_CONFIG = "me/wener/cbhistory/persistence/mybatis/mybatis-config.xml";
    @Inject
    private static Configuration configuration;

    @Override
    protected void initialize()
    {
        environmentId("dev");

//        new Configuration();

//        setClassPathResource("me.wener.cbhistory.persistence.mybatis.mappers/mybatis-config.xml");
//        addMapperClasses(Reflection.getPackageName(ArticleMapper.class));
//        addSimpleAliases(Reflection.getPackageName(Article.class));

//        environmentId("dev");
//

        addMapperClass(ArticleMapper.class);
        addMapperClass(CommentMapper.class);

        addSimpleAlias(Article.class);
        addSimpleAlias(Comment.class);
//
        handleType(DateTime.class).with(DateTimeTypeHandler.class);
        handleType(LocalDateTime.class).with(LocalDateTimeTypeHandler.class);
//
        bindTransactionFactoryType(JdbcTransactionFactory.class);
    }

    @Override
    public void init()
    {
        AbstractPlugin.getEventBus().register(this);
    }

    @Subscribe
    public void afterStarted(AfterConfigureCompleteEvent e)
    {
        AbstractPlugin.getEventBus().unregister(this);
        SqlSessionFactory factory = App.getInjector().getInstance(SqlSessionFactory.class);
        Configuration config = factory.getConfiguration();

        if (false)
        {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(XML_CONFIG))
            {
                Configuration xmlConfig = new XMLConfigBuilder(in)
                        .parse();
                Map<String, XNode> fragments = config.getSqlFragments();
                for (Map.Entry<String, XNode> entry : xmlConfig.getSqlFragments().entrySet())
                {
                    if (! fragments.containsKey(entry.getKey()))
                    {
                        fragments.put(entry.getKey(),entry.getValue());
                    }
                }
                // 配置已完成, 确认不会出错.
                config.getMappedStatements().clear();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
