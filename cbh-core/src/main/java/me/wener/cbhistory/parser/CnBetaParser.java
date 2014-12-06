package me.wener.cbhistory.parser;

import java.util.Collection;
import java.util.Set;
import me.wener.cbhistory.core.event.process.TryUpdateCommentEvent;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;

/**
 * 页面内容解析器
 */
public interface CnBetaParser
{
    Article asArticle(String content);

    String urlOfId(String id);

    Collection<Comment> asComments(Article article, String content);

    Response asResponse(String context);

    Collection<Comment> asComments(Article article, Response response);

    Set<Long> findArticleIds(String content);

    String opCode(Article article, int page);
}
