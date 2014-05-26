package me.wener.cbhistory.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.LocalDateTime;
import org.springframework.data.domain.Persistable;

@Entity
@Data
@Accessors(chain = true)
@Table(name = CommentTable.TABLE_NAME)
public class CommentEntity
        implements Persistable<Long>
        , Identifiable<Long>, CommentTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;
    private Long pid;
    private Long sid;
    private LocalDateTime date;

    @Column(length = NAME_LENGTH)
    private String name;

    @Column(length = HOSTNAME_LENGTH)
    private String hostName;
    @Column(length = COMMENT_LENGTH)
    private String comment;

    private Integer pros;

    private Integer cons;
    private Integer userId;
    @Column(length = ICON_LENGTH)
    private String icon;

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
