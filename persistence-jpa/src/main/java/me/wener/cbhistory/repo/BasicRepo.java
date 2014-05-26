package me.wener.cbhistory.repo;

import java.io.Serializable;
import me.wener.cbhistory.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicRepo<T, ID extends Serializable> extends JpaRepository<T, ID>
{
}
