package me.wener.cbhistory.persistence.ormlite.service;

import com.google.inject.Inject;
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
}
