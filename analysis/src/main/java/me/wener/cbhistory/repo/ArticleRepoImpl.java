package me.wener.cbhistory.repo;

import com.mysema.query.Tuple;
import java.util.List;
import java.util.Map;
import me.wener.cbhistory.domain.entity.QArticleEntity;

public class ArticleRepoImpl extends BasicCustom implements ArticleRepoCustom
{

    @Override
    public Map<String, Long> countPreSource()
    {
        QArticleEntity $ = QArticleEntity.articleEntity;
        List<Tuple> list = getQuery().from($)
                                .groupBy($.source)
                                .list($.source, $.count());

        return toMap(list, $.source, $.count());
    }

    @Override
    public Map<Integer, Long> countPreDay()
    {
        QArticleEntity $ = QArticleEntity.articleEntity;
        List<Tuple> list = getQuery().from($)
                                .groupBy($.date.dayOfMonth())
                                .list($.date.dayOfMonth(), $.count());

        return toMap(list, $.date.dayOfMonth(), $.count());
    }

    @Override
    public Map<Integer, Long> countPreMonth()
    {
        QArticleEntity $ = QArticleEntity.articleEntity;
        List<Tuple> list = getQuery().from($)
                                .groupBy($.date.month())
                                .list($.date.month(), $.count());

        return toMap(list, $.date.month(), $.count());
    }

    @Override
    public double collectionRate()
    {
        return getCommentRepo().count()/(double)getArticleRepo().sumOfDiscussCount();
    }

}
