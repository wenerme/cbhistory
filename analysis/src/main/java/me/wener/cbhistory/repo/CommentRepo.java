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

    @Query("select e.hostName, count(e.id) as _num from CommentEntity e " +
            "group by e.hostName order by _num desc ")
    List<Object[]> areaCount();

    /**
     * 以小时分组, 返回每小时的统计数量
     * TODO 这里 group by 应该用 _hour, 但是使用 _hour 会出错
     *
     * @return Map&lt;Integer, Long&gt;
     */
    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCount();

    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e " +
            "where e.hostName like :area " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCountByAreaLike(@Param("area") String area);

    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e " +
            "where e.date >:start and e.date<:end " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e where e.date is not NULL " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCountNotNull();

    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e " +
            "where e.hostName like :area and e.date is not NULL " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCountNotNullByAreaLike(@Param("area") String area);

    @Query("select hour(e.date) as _hour, count(*) as _num from CommentEntity e " +
            "where e.date is not NULL and e.date >:start and e.date<:end " +
            "group by hour(e.date) order by _hour desc")
    List<Object[]> hourCountNotNull(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select e.hostName, count(e.id) as _num " +
            "from CommentEntity e " +
            "where e.date > :start and e.date < :end " +
            "group by e.hostName order by _num desc ")
    List<Object[]> areaCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
