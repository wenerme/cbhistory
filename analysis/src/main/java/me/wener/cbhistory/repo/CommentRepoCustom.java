package me.wener.cbhistory.repo;

import me.wener.cbhistory.domain.entity.CommentEntity;

public interface CommentRepoCustom
{
    CommentEntity firstComment();

    long countOfArea();
}
