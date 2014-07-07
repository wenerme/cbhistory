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
import org.springframework.data.repository.query.Param;

@Named
@Singleton
public interface ArticleRepo
        extends BasicRepo<ArticleEntity, Long>, ArticleRepoCustom
{
    Collection<ArticleEntity> findAllByDateBetween(LocalDateTime start, LocalDateTime end);

    Collection<ArticleEntity> findAllBySource(String source);

    @Query("SELECT sum(a.discussCount) from ArticleEntity a")
    long countOfAllDiscuss();

    @Query("select a.source from ArticleEntity a group by a.source")
    List<String> allSource();

    long countBySource(String source);

    long countBySourceAndDateBetween(String source, LocalDateTime start, LocalDateTime end);

    long countByDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("select e.source, count(*) from ArticleEntity e group by e.source")
    List<Object[]> sourceCount();

    @Query("SELECT  min(a.date) from ArticleEntity a")
    LocalDateTime firstArticleDate();

    @Query("select hour(e.date) as _hour, count(*) as _num from ArticleEntity e " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCount();

    @Query("select hour(e.date) as _hour, count(*) as _num from ArticleEntity e " +
            "where e.source = :source " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCountBySource(@Param("source") String source);


    @Query("select hour(e.date) as _hour, count(*) as _num from ArticleEntity e " +
            "where e.date >:start and e.date<:end " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
