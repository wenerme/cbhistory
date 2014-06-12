package me.wener.cbhistory.utils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import me.wener.cbhistory.utils.json.DateTimeTypeConvert;
import me.wener.cbhistory.utils.json.InstantTypeConvert;
import me.wener.cbhistory.utils.json.LocalDateTimeTypeConvert;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

/**
 * 抽取一些公共的常用对象, 这里包含的线程安全的
 */
public class Same
{
    private Same() {}

    public static DateTimeFormatter getDateTimeFormatter()
    {
        return LazyHolder.DATE_TIME_FORMATTER;
    }

    public static Gson getGson()
    {
        return LazyHolder.GSON;
    }

    private static class LazyHolder
    {
        private static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeConvert())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConvert())
                .registerTypeAdapter(Instant.class, new InstantTypeConvert())
                .create();

        private static final DateTimeFormatter DATE_TIME_FORMATTER;

        static
        {
            // region init DATE_TIME_FORMATTER
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            ArrayList<DateTimeParser> parser = Lists.newArrayList();
            // 确保时间和日期的格式更改被正确解析
            final String[] patterns = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                    "yyyy-MM-dd HH:mm:ss.SSSSSS",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd",};
            for (String pattern : patterns)
                parser.add(DateTimeFormat.forPattern(pattern).getParser());

            parser.add(ISODateTimeFormat.dateTime().getParser());

            DATE_TIME_FORMATTER = builder
                    .append(ISODateTimeFormat.dateTime().getPrinter()
                            , parser.toArray(new DateTimeParser[parser.size()]))
                    .toFormatter();
            // endregion
        }
    }
}
