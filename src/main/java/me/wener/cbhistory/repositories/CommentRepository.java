package me.wener.cbhistory.repositories;

import javax.inject.Named;
import me.wener.cbhistory.domain.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long>
{
}
