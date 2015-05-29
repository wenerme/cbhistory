package me.wener.cbhistory.domain.entity;

public interface ArticleTable extends CBHistoryTable
{
//    +---------------+----------------------+---------------+----------------+------------+---------------------+
//    | max_title_len | max_introduction_len | max_token_len | max_source_len | max_sn_len | 查询日期            |
//    +---------------+----------------------+---------------+----------------+------------+---------------------+
//    |            89 |                  834 |            40 |             28 |          5 | 2014-05-26 22:42:47 |
//    +---------------+----------------------+---------------+----------------+------------+---------------------+
    public static final String TABLE_NAME = TABLE_PREFIX + "article";
    public static final int SN_LENGTH = 8;
    public static final int TITLE_LENGTH = 126;
    public static final int INTRO_LENGTH = 1024;
    public static final int TOKEN_LENGTH = 64;
    public static final int SOURCE_LENGTH = 64;
}
