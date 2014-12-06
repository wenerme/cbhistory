package me.wener.cbhistory.parser.v1;

import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.utils.SysUtils;
import org.junit.Test;

public class V1ParserTest
{
    @Test
    public void test()
    {
        String content = SysUtils.tryGetResourceAsString("pages/v2/home.html");
        CnBetaV1Parser parser = getCnBetaParser();
        System.out.println(parser.idsInContent(content));
    }

    @Test
    public void testArticle()
    {
        String content = SysUtils.tryGetResourceAsString("pages/v2/article.html");
        CnBetaV1Parser parser = getCnBetaParser();
        System.out.println(parser.parseToArticle(content, new Article()));
    }

    private CnBetaV1Parser getCnBetaParser() {return new CnBetaV1Parser();}
}
