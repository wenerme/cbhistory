package me.wener.cbhistory.domain;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

@Data
@Entity
@Accessors(chain = true)
@ToString(exclude = {"comments","rawData","introduction"})
//@EqualsAndHashCode(exclude = {"comments"})
public class Article implements Persistable<Long>
{
//    @Id
//    @Expose(deserialize = false,serialize = false)
//    private Long id;

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

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.ALL})
    @Expose(deserialize = false)
    private Set<Comment> comments = Sets.newHashSet();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.ALL})
    @JoinColumn(name = "sid", referencedColumnName = "sid", nullable = false)
    @Expose(deserialize = false)
    private RawData rawData;
}