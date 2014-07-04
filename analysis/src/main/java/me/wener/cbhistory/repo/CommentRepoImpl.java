package me.wener.cbhistory.repo;

import javax.inject.Named;
import me.wener.cbhistory.domain.entity.CommentEntity;
import me.wener.cbhistory.domain.entity.QCommentEntity;

@Named
public class CommentRepoImpl extends BasicCustom implements CommentRepoCustom
{

    @Override
    public CommentEntity firstComment()
    {
        QCommentEntity $ = QCommentEntity.commentEntity;

        return getCommentRepo().findOne($.tid.eq(getCommentRepo().firstCommentId()));
    }

    @Override
    public long countOfArea()
    {
        // TODO 考虑使用一条语句完成
        return getCommentRepo().allAreas().size();
    }
}
