package me.wener.cbhistory.domain;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.service.impl.ArticleServiceImpl;
import org.springframework.data.domain.Persistable;

@Data
@Entity
@Accessors(chain = true)
@ToString(exclude = {"comments","rawData","introduction"})
@Table(name = Article.TABLE_NAME)
@DatabaseTable(daoClass = ArticleServiceImpl.class)
public class Article implements Persistable<Long>, CBHistoryTable
{
    public static final String TABLE_NAME = TABLE_PREFIX+"article";

    @Override
    public Long getId()
    {
        return getSid();
    }

    @Override
    public boolean isNew()
    {
        return false;
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

    @Id
    @SerializedName("SID")
    private Long sid;

    @SerializedName("SN")
    private String sn;

    private String title;
    @Column(length = 512)// 为了避免长度过长
    private String introduction;
    /**
     * 稿源
     */
    private String source;
    private Date date;

    /**
     * 阅读数量
     */
    @SerializedName("view_num")
    private Integer readCount;
    /**
     * 评论数量
     */
    @SerializedName("comment_num")
    private Integer discussCount;
    @SerializedName("join_num")
    private Integer joinNum;
    private String token;

    @SerializedName("dig_num")
    private Integer digNum;
    @SerializedName("fav_num")
    private Integer favNum;

    @Expose(deserialize = false, serialize = false)
    private Date lastUpdateDate;

//    @ForeignCollectionField(foreignFieldName = "article")
//    // 评论单独保存,不再需要 cascade
////    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
//    @Expose(deserialize = false)
//    private Collection<Comment> comments = Sets.newHashSet();
    public Collection<Comment> getComments(){return null;}

//    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.ALL})
//    @JoinColumn(name = "sid", referencedColumnName = "sid", nullable = false)
//    @Expose(deserialize = false)
    // TODO 移除该字段
    @Deprecated
    transient private RawData rawData;

}