package me.wener.cbhistory.server.jws;

import javax.xml.ws.Endpoint;
import jodd.http.HttpUtil;
import jodd.http.HttpValuesMap;
import me.wener.cbhistory.core.App;
import org.junit.Test;

public class TestRunJWS
{
    public void test()
    {
        App.getInjector();
    }

    @Test
    public void testParseQuery()
    {
        String query = "name=wener&age=20&alias[]=10&alias[]=20";
        HttpValuesMap map = HttpUtil.parseQuery(query, true);
        System.out.println(map);
    }
}
