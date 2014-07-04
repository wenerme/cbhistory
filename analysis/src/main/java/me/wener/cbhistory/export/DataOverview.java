package me.wener.cbhistory.export;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.Data;

@Data
public class DataOverview
{
    @SerializedName("文章总数")
    private long articleCount;
    @SerializedName("评论总数")
    private long commentCount;
    @SerializedName("区域总数")
    private long areaCount;
    @SerializedName("发布者总数")
    private long sourceCount;

    @SerializedName("最早评论日期")
    private Date firstCommentDate;
    @SerializedName("最早文章日期")
    private Date firstArticleDate;
    @SerializedName("最早有评论的文章日期")
    private Date firstArticleHaveCommentDate;

    @SerializedName("作者")
    private String author;
    @SerializedName("作者邮箱")
    private String authorEmail;

    @SerializedName("生成日期")
    private Date generateDate;
}
