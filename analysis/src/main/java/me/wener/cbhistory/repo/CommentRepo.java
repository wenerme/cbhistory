package me.wener.cbhistory.repo;

import java.util.Collection;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import me.wener.cbhistory.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Named
@Singleton
public interface CommentRepo extends BasicRepo<CommentEntity, Long>
{
    Collection<CommentEntity> findAllByDateIsNull();
}
