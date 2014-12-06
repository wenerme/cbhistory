package me.wener.cbhistory.domain.entity;

public interface CommentTable extends CBHistoryTable
{
    //+-------------+--------------+----------+----------+---------------------+
    //| comment_len | hostname_len | name_len | icon_len | 查询日期            |
    //+-------------+--------------+----------+----------+---------------------+
    //|         958 |           36 |       40 |       74 | 2014-05-26 22:47:44 |
    //+-------------+--------------+----------+----------+---------------------+

    public static final String TABLE_NAME = TABLE_PREFIX + "comment";
    public static final int COMMENT_LENGTH = 1024;
    public static final int HOSTNAME_LENGTH = 64;
    public static final int NAME_LENGTH = 64;
    public static final int ICON_LENGTH = 128;

}
