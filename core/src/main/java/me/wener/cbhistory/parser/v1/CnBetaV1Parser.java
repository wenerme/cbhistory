package me.wener.cbhistory.parser.v1;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jodd.jerry.Jerry;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.parser.CnBetaParser;
import me.wener.cbhistory.utils.CodecUtils;
import org.joda.time.LocalDateTime;

@Slf4j
public class CnBetaV1Parser implements CnBetaParser
{
    // 匹配出Gv信息,放在 data 分组
    public static final Pattern regGV = Pattern.compile("^GV\\.DETAIL[^\\{]+(?<data>\\{[^\\}]+})", Pattern.MULTILINE);
    public static final Pattern regMatchId = Pattern.compile("articles/(?<id>\\d+)");

    public Set<Long> idsInContent(String content)
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
    public Article parseToArticle(String content, Article article)
    {// 解析 HTML
        final Jerry doc = Jerry.jerry(content);
        article.setTitle(doc.$("#news_title").text().trim());
        String intro = CharMatcher.anyOf("\r\n").removeFrom(doc.$(".introduction p").text());
        article.setIntroduction(intro);

        {
            Jerry bar = doc.$(".title_bar");
            String tmp = bar.$(".where").text().trim();
            tmp = tmp.substring(tmp.indexOf("：") + 1);// 替换前缀
            article.setSource(tmp);
            Date date = CodecUtils.jsonToDate(bar.$(".date").text());
            article.setDate(new LocalDateTime(date));
        }

        return article;
    }

    public String getUrl(String id)
    {
        String url = "http://www.cnbeta.com/articles/%s.htm";
        url = String.format(url, id);
        return url;
    }
}
