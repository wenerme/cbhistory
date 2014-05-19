package me.wener.cbhistory.repositories;

import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.service.ArticleService;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long>, ArticleService
{
}
