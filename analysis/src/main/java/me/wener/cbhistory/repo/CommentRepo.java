package me.wener.cbhistory.repo;

import java.util.Collection;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.CommentEntity;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Named
@Singleton
public interface CommentRepo
        extends BasicRepo<CommentEntity, Long>, CommentRepoCustom
{
    Collection<CommentEntity> findAllByDateIsNull();
    Collection<CommentEntity> findAllByDateIsNotNull();


    long countByDateIsNull();
    long countByDateIsNotNull();
    long countByHostNameNotNull();
    long countByDateBetween(LocalDateTime start, LocalDateTime end);
    long countByHostNameIsNotNullAndDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("select e.hostName from CommentEntity e " +
            "where e.hostName is not null group by e.hostName")
    List<String> allAreas();

    @Query("select min(a.date) from CommentEntity a")
    LocalDateTime firstCommentDate();
    @Query("select min(a.tid) from CommentEntity a")
    long firstCommentId();

    @Query("select e.hostName, count(e.id) as _num from CommentEntity e group by e.hostName order by _num desc ")
    List<Object[]> areaCount();

    @Query("select e.hostName, count(e.id) as _num " +
            "from CommentEntity e " +
            "where e.date > :start and e.date < :end " +
            "group by e.hostName order by _num desc ")
    List<Object[]> areaCount(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
}
