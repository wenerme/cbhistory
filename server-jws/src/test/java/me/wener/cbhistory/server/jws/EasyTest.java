package me.wener.cbhistory.server.jws;

import jodd.http.HttpUtil;
import jodd.http.HttpValuesMap;
import org.junit.Test;

public class EasyTest
{
    @Test
    public void testParseQuery()
    {
        String query = "name=wener&age=20&alias=10&alias=20";
        HttpValuesMap map = HttpUtil.parseQuery(query, true);
        System.out.println(map);
        assert map.getFirst("name").equals("wener");
        assert map.getFirst("age").equals("20");
    }
}
