package me.wener.cbhistory.repositories;

import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.service.CommentService;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long>, CommentService
{
}
