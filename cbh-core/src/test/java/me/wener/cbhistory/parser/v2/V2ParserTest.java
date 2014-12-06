package me.wener.cbhistory.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.parser.Response;
import me.wener.cbhistory.parser.v1.ResponseData;
import me.wener.cbhistory.utils.Same;
import me.wener.cbhistory.utils.SysUtils;
import org.junit.Test;

public class V2ParserTest
{
    @Test
    public void test()
    {
        CnBetaV2Parser parser = getCnBetaParser();

        String cmt = SysUtils.tryGetResourceAsString("pages/v2/cmt.json");
        Gson gson = Same.getGson();

        JsonParser jsonParser = new JsonParser();
        JsonElement parse = jsonParser.parse(cmt).getAsJsonObject().get("result").getAsJsonObject();
        System.out.println(parse);
        System.out.println(gson.fromJson(parse.toString(), RawComment.class));
        Response data = gson.fromJson(cmt, ResponseData.class);

        System.out.println(parser.asComments(new Article(), cmt));
    }

    @Test
    public void basicTest()
    {
        CnBetaV2Parser parser = getCnBetaParser();
        {
            String content = SysUtils.tryGetResourceAsString("pages/v2/article.html");
            Article a = parser.asArticle(content);
            assertNotNull(a);
            System.out.println(a);
            assertEquals("96d81", a.getSn());
            assertEquals(352259l, (long) a.getSid());
        }
    }

    private CnBetaV2Parser getCnBetaParser() {return new CnBetaV2Parser();}
}
