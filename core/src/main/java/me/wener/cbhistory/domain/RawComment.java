package me.wener.cbhistory.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 整个的评论对象,是json数据的root
 * 该对象不需要存储
 */
@Data
@Accessors(chain = true)
public class RawComment
{

    private Long sid;

    // 暂时不使用 Guava 的 Multimap, Gson 处理原生类时更方便.
    @SerializedName("cmntdict")
    private Map<String, List<CommentInfo>> commentDict;// = ArrayListMultimap.create();


    @SerializedName("hotlist")
    private Set<HotCommentInfo> hotList = Sets.newHashSet();

    @SerializedName("cmntstore")
    private Map<String, Comment> commentList = Maps.newHashMap();

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
    /*
      "comment_num": "28",
  "join_num": "28",
  "token": "67cf464da5ba82ba6ca7ba4a5b48537fcc6fa063",
  "view_num": 8721,
  "page": "1",
  "sid": "287053",
  "u": [],
  "dig_num": "3",
  "fav_num": "0"
     */
}
