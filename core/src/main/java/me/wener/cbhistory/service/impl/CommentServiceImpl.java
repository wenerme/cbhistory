package me.wener.cbhistory.service.impl;

import java.sql.SQLException;
import me.wener.cbhistory.domain.Comment;
import me.wener.cbhistory.service.CommentService;

public class CommentServiceImpl
        extends BasicServiceImpl<Comment, Long>
        implements CommentService
{
    public CommentServiceImpl() throws SQLException
    {
        super(Comment.class);
    }
}
