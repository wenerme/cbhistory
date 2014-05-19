package me.wener.cbhistory.service.impl;

import com.google.inject.Inject;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.service.CommentService;

public class CommentServiceImpl
        extends BasicServiceImpl<Comment, Long>
        implements CommentService
{
    @Inject
    public CommentServiceImpl(ConnectionSource connectionSource) throws SQLException
    {
        super(connectionSource, Comment.class);
    }
}
