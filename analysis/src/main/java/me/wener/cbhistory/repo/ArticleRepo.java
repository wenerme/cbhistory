package me.wener.cbhistory.repo;

import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import me.wener.cbhistory.domain.entity.CommentEntity;
import org.joda.time.LocalDateTime;

@Named
@Singleton
public interface ArticleRepo
        extends BasicRepo<ArticleEntity, Long>
{
    Collection<ArticleEntity> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
    Collection<ArticleEntity> findAllBySource(String source);

    long countBySource(String source);

}
