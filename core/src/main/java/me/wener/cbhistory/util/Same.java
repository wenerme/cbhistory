package me.wener.cbhistory.util;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Primitives;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collections;
import me.wener.cbhistory.util.json.DateTimeTypeConvert;
import me.wener.cbhistory.util.json.InstantTypeConvert;
import me.wener.cbhistory.util.json.LocalDateTimeTypeConvert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
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
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            ArrayList<DateTimeParser> parser = Lists.newArrayList();
            parser.add(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").getParser());
            parser.add(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").getParser());
            parser.add(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser());
            parser.add(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser());
            parser.add(DateTimeFormat.forPattern("yyyy-MM-dd").getParser());
            parser.add(ISODateTimeFormat.dateTime().getParser());

            dateTimeFormatter = builder
                    .append(ISODateTimeFormat.dateTime().getPrinter()
                            , parser.toArray(new DateTimeParser[parser.size()]))
                    .toFormatter();
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
