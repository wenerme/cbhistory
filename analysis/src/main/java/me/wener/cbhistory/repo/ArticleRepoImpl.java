package me.wener.cbhistory.repo;

import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.template.DateTemplate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.wener.cbhistory.domain.entity.QArticleEntity;
import org.joda.time.LocalDateTime;

public class ArticleRepoImpl extends BasicCustom implements ArticleRepoCustom
{

    @Override
    public LinkedHashMap<String, Long> countPreSourceDesc()
    {
        return countPreSourceDesc(null, null);
    }

    @Override
    public LinkedHashMap<String, Long> countPreSourceDesc(LocalDateTime start, LocalDateTime end)
    {
        QArticleEntity $ = QArticleEntity.articleEntity;
        JPAQuery query = getQuery().from($);

        if (start != null && end != null)
            query.where($.date.between(start, end));
        else if (start != null)
            query.where($.date.goe(start));
        else if (end != null)
            query.where($.date.loe(end));

        List<Tuple> list = query
                .groupBy($.source)
                .orderBy($.count().desc())
                .list($.source, $.count());

        return toLinkedMap(list, $.source, $.count());
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
        return getCommentRepo().count() / (double) getArticleRepo().countOfAllDiscuss();
    }

    @Override
    public LinkedHashMap<Date, Long> countBySourceGroupByDateOrderByDateDesc(String source)
    {
        QArticleEntity $ = QArticleEntity.articleEntity;
        DateExpression<Date> date = DateTemplate.create(Date.class, "date({0})", $.date);
        List<Tuple> list = getQuery().from($)
                                     .where(source == null ? $.source.isNull() : $.source.eq(source))
                                     .groupBy(date)
                                     .orderBy(date.desc())
                                     .list(date, $.count());
        return toLinkedMap(list, date, $.count());
    }

    @Override
    public long countOfSource()
    {
        // TODO 考虑使用一条语句完成
        return getArticleRepo().allSource().size();
    }

}
