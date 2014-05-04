package me.wener.cbhistory.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Data
@Entity
@ToString(exclude = {"article","parent"})
@EqualsAndHashCode(exclude = {"article"})
public class Comment implements Persistable<Long>
{
    /*
     "tid": "9041995",
      "pid": "0",
      "sid": "287053",
      "date": "2014-04-28 07:56:50",
      "name": "匿名人士",
      "host_name": "湖南省长沙市",
      "comment": "典型马后炮总结",
      "score": "7",
      "reason": "4",
      "userid": "0",
      "icon": ""
     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Expose(deserialize = false,serialize = false)
//    private Long id;

    @Override
    public Long getId()
    {
        return getTid();
    }

    @Override
    public boolean isNew()
    {
        return false;
    }

    public Comment setTid(Long tid)
    {
        this.tid = tid;
        return this;
    }

    @Id
    private Long tid;
    private Long pid;
    private Long sid;

    private Date date;
    private String name;
    /**
     * 所在地址
     */
    @SerializedName("host_name")
    private String hostName;
    @Column(length = 400)// 默认长度为320, 给 400足够了
    private String comment;
    /**
     * 支持
     */
    @SerializedName("score")
    private Integer pros;
    /**
     * 反对
     */
    @SerializedName("reason")
    private Integer cons;
    @SerializedName("userid")
    private Integer userId;
    private String icon;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sid", nullable = false, insertable = false, updatable = false)
    @Expose(deserialize = false)
    private Article article;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "sid",
            // 因为有可能评论被删除了,所以不需要添加外键约束
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            nullable = true, insertable = false, updatable = false)
    @Expose(deserialize = false)
    private Comment parent;

}
