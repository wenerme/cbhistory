package me.wener.cbhistory.domain.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Persistable;

@Data
@Table(name = CommentTable.TABLE_NAME)
@Entity
@Accessors(chain = true)
@EqualsAndHashCode(exclude = {"article", "parent"})
@ToString(exclude = {"article", "parent"})
//@Indexed
//@Analyzer(impl = IKAnalyzer.class)
public class CommentEntity
        implements Persistable<Long>, Identifiable<Long>, CommentTable
{
    // @DocumentId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;
    private Long pid;
    private Long sid;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime date;

    @Column(length = NAME_LENGTH)
    private String name;

    @Column(length = HOSTNAME_LENGTH)
    private String hostName;

    // @Field
    @Column(length = COMMENT_LENGTH)
    private String comment;

    private Integer pros;

    private Integer cons;
    private Integer userId;
    @Column(length = ICON_LENGTH)
    private String icon;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sid", nullable = false, insertable = false, updatable = false)
    private ArticleEntity article;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "tid",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), nullable = true, insertable = false, updatable = false)
    private CommentEntity parent;

    @Override
    public Long getId()
    {
        return getTid();
    }

    @Override
    public boolean isNew()
    {
        return getId() == null;
    }
}
