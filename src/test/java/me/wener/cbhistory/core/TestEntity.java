package me.wener.cbhistory.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.io.File;
import java.util.Arrays;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import me.wener.cbhistory.Application;
import me.wener.cbhistory.core.event.TryFoundArticleEvent;
import me.wener.cbhistory.core.event.UpdateCommentEvent;
import me.wener.cbhistory.core.process.ProcessCenter;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.repositories.ArticleRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@TransactionConfiguration(defaultRollback=false)
@Transactional
@ContextConfiguration(classes = {Application.class})
@Ignore
public class TestEntity
{
    @Inject
    ApplicationContext ctx;

//    @PersistenceUnit
//    EntityManagerFactory emf;
    EntityManager em;

    @Inject
    ArticleRepository articleRepository;

    @Inject
    public void postProcess(EntityManagerFactory emf)
    {
        em = emf.createEntityManager();
    }

    @Test
    public void launchFirst()
    {
    }

    @Test
    public void useEmf()
    {
//        EntityManager entityManager = emf.createEntityManager();
        EntityManager entityManager = em;
        entityManager.getTransaction().begin();

        Article entity = new Article().setSid(1).setTitle("wener");
        entityManager.persist(entity);
        entityManager.persist(entity.setIntroduction("xiaoxiao"));

        entityManager.getTransaction().commit();
    }

    @Test
    public void simpleData()
    {
        Article article = new Article();
        article.setTitle("wener");
        articleRepository.save(article);

        System.out.println(articleRepository.findAll());
    }

    @Test
    public void testEm()
    {
        Article article = new Article();
        article.setTitle("wener");
        em.persist(article);
        em.persist(article);

        TypedQuery<Article> query = em.createQuery("SELECT a FROM Article a", Article.class);
        System.out.println(query.getResultList());
    }

    @Test
    public void testItor()
    {
        Iterable<Article> iterable = Arrays.asList(new Article().setTitle("wener"), new Article().setTitle("xiaoxiao"));

        em.persist(iterable);// 这样是不可以的

        TypedQuery<Article> query = em.createQuery("SELECT a FROM Article a", Article.class);
        System.out.println(query.getResultList());
    }

    @Test
    public void persistComment() throws Exception
    {
        Events.register(ProcessCenter.getInstance());
//        Events.register(EventScheduler.getInstance());

        Article article = new Article();
        article.setSid(287625);
        article.setSn("3feab");
        RawData raw = new Gson().fromJson(Files.toString(new File("C:\\cmt.json"), Charsets.UTF_8), RawData.class);

        Events.post(new UpdateCommentEvent(article, raw));
        Thread.sleep(50000);
    }

    @Test
    public void testReal() throws Exception
    {
        Events.post(new TryFoundArticleEvent("287961"));
        Thread.sleep(60000);
    }

}

