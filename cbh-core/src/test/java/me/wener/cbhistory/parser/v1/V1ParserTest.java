package me.wener.cbhistory.parser.v1;

import me.wener.cbhistory.utils.SysUtils;
import org.junit.Test;

public class V1ParserTest
{
    @Test
    public void test()
    {
        String content = SysUtils.tryGetResourceAsString("pages/v2/home.html");
        CnBetaV1Parser parser = getCnBetaParser();
        System.out.println(parser.findArticleIds(content));
    }

    @Test
    public void testArticle()
    {
        String content = SysUtils.tryGetResourceAsString("pages/v2/article.html");
        CnBetaV1Parser parser = getCnBetaParser();
        System.out.println(parser.asArticle(content));
    }

    private CnBetaV1Parser getCnBetaParser() {return new CnBetaV1Parser();}
}
