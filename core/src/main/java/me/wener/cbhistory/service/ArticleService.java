package me.wener.cbhistory.service;

import java.util.List;
import me.wener.cbhistory.domain.entity.Article;
import org.joda.time.LocalDateTime;

public interface ArticleService extends BasicService<Article, Long>
{
    List<Article> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
}
