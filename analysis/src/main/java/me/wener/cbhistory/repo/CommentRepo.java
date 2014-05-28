package me.wener.cbhistory.repo;

import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.CommentEntity;

@Named
@Singleton
public interface CommentRepo
        extends BasicRepo<CommentEntity, Long>, CommentRepoCustom
{
    Collection<CommentEntity> findAllByDateIsNull();
    Collection<CommentEntity> findAllByDateIsNotNull();


    long countByDateIsNull();
    long countByDateIsNotNull();
}
