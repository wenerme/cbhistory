package me.wener.cbhistory.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Collection;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.service.impl.ArticleServiceImpl;

@Data
@Accessors(chain = true)
@DatabaseTable(daoClass = ArticleServiceImpl.class, tableName = Article.TABLE_NAME)
public class Article implements Identifiable<Long>, CBHistoryTable
{
    public static final String TABLE_NAME = TABLE_PREFIX + "article";
    @DatabaseField(id = true)
    @SerializedName("SID")
    private Long sid;
    @DatabaseField(width = 16)
    @SerializedName("SN")
    private String sn;
    @DatabaseField(width = 125)
    private String title;
    @DatabaseField(width = 512)// 为了避免长度过长
    private String introduction;
    /**
     * 稿源
     */
    @DatabaseField(width = 64)
    private String source;
    @DatabaseField
    private Date date;
    /**
     * 阅读数量
     */
    @DatabaseField
    @SerializedName("view_num")
    private Integer readCount;
    /**
     * 评论数量
     */
    @DatabaseField
    @SerializedName("comment_num")
    private Integer discussCount;
    @DatabaseField
    @SerializedName("join_num")
    private Integer joinNum;
    @DatabaseField(width = 64)
    private String token;
    @DatabaseField
    @SerializedName("dig_num")
    private Integer digNum;
    @DatabaseField
    @SerializedName("fav_num")
    private Integer favNum;
    @DatabaseField
    @Expose(deserialize = false, serialize = false)
    private Date lastUpdateDate;

    @Override
    public Long getId()
    {
        return getSid();
    }

    public Article setSid(int sid)
    {
        setSid((long) sid);
        return this;
    }

    public Article setSid(Long sid)
    {
        this.sid = sid;
        return this;
    }

    // FIXME 移除该字段
    public Collection<Comment> getComments()
    {return null;}
}