package me.wener.cbhistory.parser.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.entity.Comment;
import me.wener.cbhistory.parser.Response;
import me.wener.cbhistory.parser.v1.CnBetaV1Parser;
import me.wener.cbhistory.parser.v1.ResponseData;

@Slf4j
public class CnBetaV2Parser extends CnBetaV1Parser
{

    public static final Pattern regMatchId = Pattern.compile("articles/(?<id>\\d+)");

    public Set<Long> findArticleIds(String content)
    {
        Set<Long> ids = Sets.newHashSet();
        Matcher matcher = regMatchId.matcher(content);
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

    @Override
    public Collection<Comment> asComments(Article article, String content)
    {
        JsonElement parse = new JsonParser().parse(content);
        Response status = GSON.fromJson(parse, ResponseData.class);
        Preconditions.checkState(status.isSuccess(), "响应失败");
        return asCommentWithJSON(article, parse.getAsJsonObject().get("result"));
    }

    @Override
    public String opCode(Article article, int page)
    {
        return String.format("%s,%s,%s", page, article.getSid(), article.getSn());
    }
}
