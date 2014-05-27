package me.wener.cbhistory.repo;

import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import org.joda.time.LocalDateTime;

@Named
@Singleton
public interface ArticleRepo
        extends BasicRepo<ArticleEntity, Long>
{
    Collection<ArticleEntity> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
}
