package me.wener.cbhistory.domain.entity;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
@Table(name = ArticleTable.TABLE_NAME)
@Entity
@Accessors(chain = true)
public class ArticleEntity
        implements Persistable<Long>, Identifiable<Long>, ArticleTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;
    @Column(length = SN_LENGTH)
    private String sn;
    @Column(length = TITLE_LENGTH)
    private String title;
    @Column(length = INTRO_LENGTH)
    private String introduction;
    /**
     * 稿源
     */
    @Column(length = SOURCE_LENGTH)
    private String source;
    private LocalDateTime date;
    /**
     * 阅读数量
     */
    private Integer readCount;
    /**
     * 评论数量
     */
    private Integer discussCount;
    private Integer joinNum;
    @Column(length = TOKEN_LENGTH)
    private String token;
    private Integer digNum;
    private Integer favNum;
    private LocalDateTime lastUpdateDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "article")
    private Set<CommentEntity> comments = Sets.newHashSet();

    @Override
    public Long getId()
    {
        return getSid();
    }

    @Override
    public boolean isNew()
    {
        return getId() == null;
    }
}
