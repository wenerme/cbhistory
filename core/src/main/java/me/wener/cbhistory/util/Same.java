package me.wener.cbhistory.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wener.cbhistory.util.json.DateTimeTypeConvert;
import me.wener.cbhistory.util.json.InstantTypeConvert;
import me.wener.cbhistory.util.json.LocalDateTimeTypeConvert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Same
{
    private static DateTimeFormatter dateTimeFormatter = null;
    private static Gson gson = null;
    private Same(){}

    public static DateTimeFormatter getDateTimeFormatter()
    {
        if (dateTimeFormatter == null)
        {
            dateTimeFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
        }
        return dateTimeFormatter;
    }

    public static Gson getGson()
    {
        if (gson == null)
        {
            gson = new GsonBuilder()
                    .registerTypeAdapter(DateTime.class, new DateTimeTypeConvert())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConvert())
                    .registerTypeAdapter(Instant.class, new InstantTypeConvert())
                    .create();
        }

        return gson;
    }
}
