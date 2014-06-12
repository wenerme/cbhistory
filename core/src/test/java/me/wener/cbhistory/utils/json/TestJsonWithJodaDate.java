package me.wener.cbhistory.utils.json;

import lombok.Data;
import lombok.experimental.Accessors;
import me.wener.cbhistory.utils.Same;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.junit.Test;

public class TestJsonWithJodaDate
{
    @Test
    public void testJoda()
    {

        final DateTime dateTime = DateTime.now();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final Instant instant = Instant.now();


        assert isJsonEnabled(instant);
        assert isJsonEnabled(localDateTime);
        assert isJsonEnabled(dateTime);

        Object v = new AllValues().setDt(dateTime).setI(instant).setLdt(localDateTime);
        assert isJsonEnabled(v);
    }

    boolean isJsonEnabled(Object o)
    {
        String json = Same.getGson().toJson(o);
        Object obj = Same.getGson().fromJson(json, o.getClass());

        System.out.printf("JSON: %s\nOBJECT: %s\n equals?: %s \n"
                , json, obj, obj.equals(o));

        return obj.equals(o);
    }

    @Data
    @Accessors(chain = true)
    public static class AllValues
    {
        DateTime dt;
        LocalDateTime ldt;
        Instant i;
    }

}