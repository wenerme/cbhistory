package me.wener.cbhistory.service;

import java.util.Collection;
import me.wener.cbhistory.domain.entity.Comment;

public interface CommentService extends BasicService<Comment, Long>
{
    Collection<Comment> findAllBySid(long sid);
}
