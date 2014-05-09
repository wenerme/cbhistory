package me.wener.cbhistory.domain;

import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class HotCommentInfo extends CommentInfo
{
    /*"tid": "9042329",
      "pid": "0",
      "sid": "287053",
      "parent": "",
      "thread": ""
      */
    private String parent;
    private String thread;
}
