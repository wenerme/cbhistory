package me.wener.cbhistory.core;


import static com.google.common.base.Preconditions.*;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jodd.bean.BeanCopy;
import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.Comment;
import me.wener.cbhistory.domain.CommentInfo;
import me.wener.cbhistory.domain.HotCommentInfo;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.RawData;
import me.wener.cbhistory.util.CodecUtils;
import me.wener.cbhistory.util.ExcludeNotExposedField;

public class CBHistory
{
    /**
     * 附加在 JSON 数据中的前缀
     */
    private static final String JSON_PREFIX = "cnbeta";

    // region calcOp
    public static String calcOp(OpInfo info)
    {
        return calcOp(info.getPage(), info.getSid(), info.getSn(), 8);
    }

    public static String calcOp(Article detail)
    {
        return calcOp(1, detail.getSid(), detail.getSn(), 8);
    }

    public static String calcOp(String page, String sid, String sn, int n)
    {
        checkNotNull(page);
        checkNotNull(sid);
        return calcOp(Integer.parseInt(page), Integer.parseInt(sid), sn, n);
    }

    public static String calcOp(Integer page, Integer sid, String sn, int n)
    {
        return calcOp(page, (long) ((int) sid), sn, n);
    }

    public static String calcOp(Integer page, Long sid, String sn, int n)
    {
        checkNotNull(page);
        checkNotNull(sid);
        checkNotNull(sn);

        final String b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        final char[] b64Chars = b64.toCharArray();
        String op = "%s,%s,%s";
        op = String.format(op, page, sid, sn);
        op = CodecUtils.encodeBase64(op);

        {
            String extra = "";
            for (int i = 0; i < n; i++)
                extra += b64Chars[((int) Math.floor(Math.random() * b64Chars.length))];
            op += extra;
        }

        return CodecUtils.encodeURIComponent(op);
    }
    // endregion

    public static OpInfo parseOp(String op)
    {
        OpInfo info = new OpInfo();
        op = CodecUtils.decodeURIComponent(op);
        op = op.substring(0, op.indexOf("=") + 1);
        op = CodecUtils.decodeBase64(op);
        String[] parts = op.split(",");

        info.setPage(Integer.parseInt(parts[0]));
        info.setSid(Long.parseLong(parts[1]));
        info.setSn(parts[2]);

        return info;
    }


    private static Gson gson = new GsonBuilder()
            .addDeserializationExclusionStrategy(ExcludeNotExposedField.deserialize())
            .addSerializationExclusionStrategy(ExcludeNotExposedField.serialize())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static RawData getRawDataFrom(RawComment rawComment)
    {
        RawData rawData = new RawData();
        rawData.setSid(rawComment.getSid())
               .setLastUpdate(new Date())
               .setStatus("success");
        String json = JSON_PREFIX + gson.toJson(rawComment);
        rawData.setResult(CodecUtils.encodeBase64(json));
        return rawData;
    }

    public static RawData getRawDataFrom(Article article)
    {
        return getRawDataFrom(getRawCommentFrom(article));
    }

    private static Function<Comment, String> funcGetCommentTid = new Function<Comment, String>()
    {
        @Override
        public String apply(Comment input)
        {
            return input.getTid().toString();
        }
    };
    private static Ordering<Comment> orderCommentByPros = new Ordering<Comment>()
    {
        public int compare(Comment left, Comment right)
        {
            return Ints.compare(left.getPros(), right.getPros());
        }
    };

    public static RawComment getRawCommentFrom(Article article)
    {
        RawComment rawComment = new RawComment();
        List<Comment> comments = orderCommentByPros.reverse().sortedCopy(article.getComments());
        // 生成文章内容列表
        Map<String, Comment> commentMap = Maps
                .uniqueIndex(comments, funcGetCommentTid);

        // 生成热门列表
        Set<HotCommentInfo> hotList = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            Comment comment = comments.get(i);
            HotCommentInfo hotCommentInfo = new HotCommentInfo();

            BeanCopy.beans(comment, hotCommentInfo)
                    .exclude("parent")
                    .copy();

            hotList.add(hotCommentInfo);
        }

        // 生成回复评论列表
        Map<String, List<CommentInfo>> commentReply = Maps.newHashMap();
        for (Comment comment : comments) {
            if (comment.getPid() == null)
                continue;
            List<CommentInfo> list = Lists.newArrayList();
            commentReply.put(comment.getTid().toString(), list);

            Comment current = comment;
            do {
                current = commentMap.get(current.getPid().toString());
                if (current == null)
                    break;

                CommentInfo info = new CommentInfo();
                BeanCopy.beans(current, info)
                        .copy();
                list.add(info);
            } while (current.getPid() != null);

        }
        BeanCopy.beans(article, rawComment)
                .copy();
        rawComment.setCommentDict(commentReply);
        rawComment.setHotList(hotList);
        rawComment.setCommentList(commentMap);
        return rawComment;
    }
}
