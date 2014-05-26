package me.wener.cbhistory.persistence.ormlite.service;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.service.CommentService;

@Slf4j
public class CommentServiceImpl
        extends BasicServiceImpl<Comment, Long>
        implements CommentService
{
    public static final int PAGE_SIZE = 100;

    @Inject
    public CommentServiceImpl(ConnectionSource connectionSource) throws SQLException
    {
        super(connectionSource, Comment.class);
    }

    @Override
    public Collection<Comment> findAllBySid(long sid)
    {
        try
        {
            return queryForEq("sid", sid);
        } catch (SQLException e)
        {
            log.error("findAllBySid( " + sid + " ) 时出现异常.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Comment> findAllBySid(long sid, int page)
    {
        Preconditions.checkArgument(page > 0, "页号必须大于 0");

        try
        {
            QueryBuilder<Comment, Long> builder = queryBuilder();
            builder.where().eq("sid", sid);
            return builder
                    .offset((page - 1) * PAGE_SIZE)
                    .limit(PAGE_SIZE)
                    .query();
        } catch (SQLException e)
        {
            log.error("findAllBySid( " + sid + "," + page + " ) 时出现异常.", e);
        }
        return Collections.emptyList();
    }
}
