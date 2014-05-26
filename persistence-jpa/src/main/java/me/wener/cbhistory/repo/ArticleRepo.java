package me.wener.cbhistory.repo;

import javax.inject.Named;
import javax.inject.Singleton;
import me.wener.cbhistory.domain.entity.ArticleEntity;

@Named
@Singleton
public interface ArticleRepo extends BasicRepo<ArticleEntity, Long>
{
}
