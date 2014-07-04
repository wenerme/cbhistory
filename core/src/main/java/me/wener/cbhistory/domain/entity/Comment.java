package me.wener.cbhistory.domain.entity;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.persistence.ormlite.service.CommentServiceImpl;
import org.joda.time.LocalDateTime;

@Data
@Accessors(chain = true)
@DatabaseTable(daoClass = CommentServiceImpl.class, tableName = Comment.TABLE_NAME)
public class Comment implements Identifiable<Long>, CommentTable
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
    @DatabaseField(id = true)
    private Long tid;
    @DatabaseField
    private Long pid;
    @DatabaseField
    private Long sid;
    @DatabaseField
    private LocalDateTime date;
    @DatabaseField(width = NAME_LENGTH)
    private String name;
    /**
     * 所在地址
     */
    @SerializedName("host_name")
    @DatabaseField(width = HOSTNAME_LENGTH)
    private String hostName;
    @DatabaseField(width = COMMENT_LENGTH)
    private String comment;
    /**
     * 支持
     */
    @SerializedName("score")
    @DatabaseField
    private Integer pros;
    /**
     * 反对
     */
    @SerializedName("reason")
    @DatabaseField
    private Integer cons;
    @SerializedName("userid")
    @DatabaseField
    private Integer userId;
    @DatabaseField(width = ICON_LENGTH)
    private String icon;

    @Override
    public Long getId()
    {
        return getTid();
    }
}