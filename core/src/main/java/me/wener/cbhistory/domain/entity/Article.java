package me.wener.cbhistory.domain.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.persistence.ormlite.service.ArticleServiceImpl;
import org.joda.time.LocalDateTime;

@Data
@Accessors(chain = true)
@DatabaseTable(daoClass = ArticleServiceImpl.class, tableName = Article.TABLE_NAME)
public class Article implements Identifiable<Long>, ArticleTable
{
    @DatabaseField(id = true)
    @SerializedName("SID")
    private Long sid;
    @DatabaseField(width = SN_LENGTH)
    @SerializedName("SN")
    private String sn;
    @DatabaseField(width = TITLE_LENGTH)
    private String title;
    @DatabaseField(width = INTRO_LENGTH)// 为了避免长度过长
    private String introduction;
    /**
     * 稿源
     */
    @DatabaseField(width = SOURCE_LENGTH)
    private String source;
    @DatabaseField
    private LocalDateTime date;
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
    private LocalDateTime lastUpdateDate;

    @Override
    public Long getId()
    {
        return getSid();
    }

}