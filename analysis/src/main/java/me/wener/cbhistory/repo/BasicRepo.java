package me.wener.cbhistory.repo;

import java.io.Serializable;
import java.util.List;
import me.wener.cbhistory.domain.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BasicRepo<T, ID extends Serializable>
        extends JpaRepository<T, ID>, PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T>
{
}
