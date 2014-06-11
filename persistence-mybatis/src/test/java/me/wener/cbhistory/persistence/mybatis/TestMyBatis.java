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
    public void testCount()
    {
        assert articleMapper.count() == articleService.count();
        assert commentMapper.count() == commentService.count();
    }
    @Test
    public void testById()
    {
        long aid = 287295;
        assert articleMapper.findById(aid).equals(articleService.findOne(aid));

        long cid = 9059525;
        assert commentMapper.findById(cid).equals(commentService.findOne(cid));
        // 查找不到都返回 null
        assert articleMapper.findById(0l) == articleService.findOne(0l);
        assert commentMapper.findById(0l) == commentService.findOne(0l);
    }
}
