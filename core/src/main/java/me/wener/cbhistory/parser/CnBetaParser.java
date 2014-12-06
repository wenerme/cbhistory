package me.wener.cbhistory.parser;

import me.wener.cbhistory.domain.entity.Article;

/**
 * 页面内容解析器
 */
public interface CnBetaParser
{

    Article parseToArticle(String content, Article article);
}
