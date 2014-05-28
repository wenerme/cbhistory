package me.wener.cbhistory.domain.entity;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Persistable;

@Data
@Table(name = ArticleTable.TABLE_NAME)
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(exclude = "comments")
@ToString(exclude = "comments")
//@Indexed
//@Analyzer(impl = IKAnalyzer.class)
public class ArticleEntity
        implements Persistable<Long>, Identifiable<Long>, ArticleTable
{
    //    @DocumentId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;
    @Column(length = SN_LENGTH)
    private String sn;

    //    @Field
//    @Boost(value = 2.0F)
    @Column(length = TITLE_LENGTH)
    private String title;

    //    @Field
    @Column(length = INTRO_LENGTH)
    private String introduction;
    /**
     * 稿源
     */
    @Column(length = SOURCE_LENGTH)
    private String source;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
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
