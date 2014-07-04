package me.wener.cbhistory.repo.test;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mysema.query.jpa.impl.JPAQuery;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import me.wener.cbhistory.core.App;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import me.wener.cbhistory.domain.entity.CommentEntity;
import me.wener.cbhistory.domain.entity.QArticleEntity;
import me.wener.cbhistory.domain.entity.QCommentEntity;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import me.wener.cbhistory.spring.SpringContextConfig;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextConfig.class)
@Transactional
public class TestWithSpring
{
    @Inject
    ApplicationContext ctx;
    @Inject
    ArticleRepo articleRepo;
    @Inject
    CommentRepo commentRepo;
    @PersistenceContext
    EntityManager entityManager;
    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    static
    {
        App.getInjector();
    }

    @Test
    @Ignore
    public void test()
    {
        System.out.println(articleRepo.count());
    }

    @Test
    public void testId()
    {
        long id = 287961l;
        ArticleEntity article = articleRepo.findOne(id);
        assert article.getComments().size() > 0;
        Iterable<CommentEntity> commentWithParent = Iterables
                .filter(article.getComments(), new Predicate<CommentEntity>()
                {
                    @Override
                    public boolean apply(@Nullable CommentEntity input)
                    {
                        Preconditions.checkNotNull(input);
                        return input.getParent() != null;
                    }
                });
        assert Iterables.size(commentWithParent) > 0;
        System.out.println("with parent: " + Iterables.size(commentWithParent));
    }

    @Test
    public void testQueryDsl()
    {
        JPAQuery query = new JPAQuery(entityManager);
        QCommentEntity $ = QCommentEntity.commentEntity;
        long count = query.from($).where($.date.isNull()).count();
        System.out.println("无日期的评论数量: " + count);

        assert count == commentRepo.findAllByDateIsNull().size();
        assert count == commentRepo.countByDateIsNull();
    }

    @Test
    public void testQueryDslBetween()
    {
        JPAQuery query = new JPAQuery(entityManager);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(2);

        int size = articleRepo.findAllByDateBetween(start, end).size();
        QArticleEntity $ = QArticleEntity.articleEntity;
        long count = query.from($).where($.date.gt(start).and($.date.lt(end))).count();
        assert size > 0;
        assert size == count;
    }

    @Test
    public void testFirstArticleDate()
    {
        LocalDateTime date = articleRepo.firstArticleDate();
        assert date != null;
        System.out.println(date);
    }

    @Test
    public void testAllAreas()
    {
        List<String> areas = commentRepo.allAreas();
        System.out.println(areas);
        assert areas != null;
    }

    @Test
    public void testAllAreasCount()
    {
        List<Object[]> areaCount = commentRepo.areaCount();
        System.out.println("Length " + areaCount.size());
        assert areaCount.size() > 0;
    }

    @Test
    public void testAllAreasCountWithDate()
    {
        LocalDateTime start = commentRepo.firstCommentDate();
        List<Object[]> areaCount = commentRepo.areaCount(start, start.plusDays(5));
        System.out.println("Length " + areaCount.size());
        assert areaCount.size() > 0;
    }

    @Test
    public void testFindOne()
    {
        CommentEntity commentEntity = commentRepo.firstComment();
        System.out.println(commentEntity);
        assert commentEntity != null;
    }
}
