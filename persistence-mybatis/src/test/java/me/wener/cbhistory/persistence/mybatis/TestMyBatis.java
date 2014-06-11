package me.wener.cbhistory.persistence.mybatis;

import com.google.common.annotations.Beta;
import javax.inject.Inject;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.persistence.mybatis.mappers.ArticleMapper;
import me.wener.cbhistory.persistence.mybatis.mappers.CommentMapper;
import me.wener.cbhistory.service.ArticleService;
import me.wener.cbhistory.service.CommentService;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;

public class TestMyBatis
{
    @Inject
    ArticleMapper articleMapper;
    @Inject
    CommentMapper commentMapper;
    @Inject
    ArticleService articleService;
    @Inject
    CommentService commentService;
    @Inject
    SqlSession sqlSession;
    @Before
    public void test()
    {
        App.getInjector().injectMembers(this);
    }

    @Test
    public void testSelectAll()
    {
        assert articleMapper.count() == articleService.count();
        assert commentMapper.count() == commentService.count();
    }
}
