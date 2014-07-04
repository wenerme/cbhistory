package me.wener.cbhistory.repo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import me.wener.cbhistory.domain.entity.CommentEntity;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;

@Named
@Singleton
public interface ArticleRepo
        extends BasicRepo<ArticleEntity, Long>, ArticleRepoCustom
{
    Collection<ArticleEntity> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
    Collection<ArticleEntity> findAllBySource(String source);

    @Query("SELECT sum(a.discussCount) from ArticleEntity a")
    long sumOfDiscussCount();

    @Query("select a.source from ArticleEntity a group by a.source")
    List<String> findAllSource();

    long countBySource(String source);
    long countBySourceAndDateBetween(String source, LocalDateTime start, LocalDateTime end);
    long countByDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT  min(a.date) from ArticleEntity a")
    LocalDateTime firstArticleDate();
}
