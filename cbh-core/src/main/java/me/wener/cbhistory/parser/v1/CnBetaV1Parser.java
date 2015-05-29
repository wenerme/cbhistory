package me.wener.cbhistory.parser.v1;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.parser.CnBetaParser;
import me.wener.cbhistory.parser.Response;
import me.wener.cbhistory.utils.CodecUtils;
import me.wener.cbhistory.utils.Same;
import org.joda.time.LocalDateTime;

@Slf4j
public class CnBetaV1Parser implements CnBetaParser
{
    // 匹配出Gv信息,放在 data 分组
    public static final Pattern REG_GV = Pattern.compile("^\\s*GV.DETAIL\\s*=(?<data>[^;]+)", Pattern.MULTILINE);
    public static final Pattern REG_ARTICLES_ID = Pattern.compile("articles/(?<id>\\d+)");
    public static final Gson GSON = Same.getGson();
    public static final CharMatcher CLR_MATCHER = CharMatcher.anyOf("\r\n");

    public Set<Long> findArticleIds(String content)
    {
        Set<Long> ids = Sets.newHashSet();
        Matcher matcher = REG_ARTICLES_ID.matcher(content);
        while (matcher.find())
        {
            String id = matcher.group("id");
            try
            {
                ids.add(Long.parseLong(id));
            } catch (Exception ex) {log.error("解析ID出现异常", ex);}
        }
        return ids;
    }

    /**
     * 解析失败返回 {@code null}
     */
    @Override
    public Article asArticle(String content)
    {
        Article article = new Article();
        // 解析出文章的详细信息
        Matcher matcher = CnBetaV1Parser.REG_GV.matcher(content);
        if (matcher.find())
        {
            String data = matcher.group("data");
            CodecUtils.mergeTo(data, article);
        } else
        {
            return null;
        }

        // 解析 HTML
        final Jerry doc = Jerry.jerry(content);
        article.setTitle(doc.$("#news_title").text().trim());
        String intro = CLR_MATCHER.removeFrom(doc.$(".introduction p").text());
        article.setIntroduction(intro);

        {
            Jerry bar = doc.$(".title_bar");
            String tmp = bar.$(".where").text().trim();
            tmp = tmp.substring(tmp.indexOf("：") + 1);// 替换前缀
            article.setSource(tmp);
            Date date = CodecUtils.toDate(bar.$(".date").text());
            article.setDate(new LocalDateTime(date));
        }

        return article;
    }

    @Override
    public String urlOfId(String id)
    {
        String url = "http://www.cnbeta.com/articles/%s.htm";
        url = String.format(url, id);
        return url;
    }

    /**
     * @param article 该评论JSON的文章信息, 会更新该article相应的信息
     * @param content 从服务器获取的JSON内容
     * @return 评论内容
     */
    @Override
    public Collection<Comment> asComments(Article article, String content)
    {
        // 调整内容格式
        String result = CodecUtils.decodeBase64(content);
        result = result.replaceFirst("^cnbeta", "");// 去除前缀

        return asCommentWithJSON(article, new JsonParser().parse(result));
    }

    public Collection<Comment> asComments(Article article, Response content)
    {
        return asComments(article, content.getContent());
    }

    protected Collection<Comment> asCommentWithJSON(Article article, JsonElement result)
    {
        RawComment rawComment;
        Collection<Comment> comments;

        // 解析
        rawComment = GSON.fromJson(result, RawComment.class);
        // 更新文章附加的其他信息
        CodecUtils.mergeTo(result, article);

        comments = rawComment.getCommentList().values();

        for (Comment comment : comments)
        {
            // 将 pid 为 0 的值置为空, 因为 id 为 0 的评论是不存在的
            if (comment.getPid() != null && comment.getPid() == 0)
                comment.setPid(null);
            // 将匿名人士的名字设置为 null
            if (comment.getName() != null && comment.getName().equals("匿名人士"))
                comment.setName(null);
            // 不存储 userId = 0
            if (comment.getUserId() != null && comment.getUserId() == 0)
                comment.setUserId(null);
            // 有可能为空字符串
            if (Strings.isNullOrEmpty(comment.getIcon()))
                comment.setIcon(null);
        }
        return comments;
    }

    @Override
    public Response asResponse(String content)
    {
        ResponseData data = GSON.fromJson(content, ResponseData.class);
        data.setContent(content);
        return data;
    }

    @Override
    public String opCode(Article article, int page)
    {
        return CBHistory.calcOp(article, page);
    }
}
