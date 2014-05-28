package me.wener.cbhistory.repo;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CommentRepoImpl implements CommentRepoCustom
{
    @Inject
    CommentRepo repo;

}
