package me.wener.cbhistory.persistence.ormlite;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDateType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 默认的 JodaDate 处理需要数据库数据为 long
 * 而目前使用的数据类型为 datetime,需要自己解析
 */
public class JodaDateType extends BaseDateType
{
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    public JodaDateType()
    {
        super(SqlType.DATE, new Class[]{DateTime.class, LocalDateTime.class});
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException
    {
        if (fieldType.getType() == DateTime.class)
            return formatter.parseDateTime(defaultStr);
        if (fieldType.getType() == LocalDateTime.class)
            return formatter.parseLocalDateTime(defaultStr);
        return null;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException
    {
        return print(javaObject);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos)
            throws SQLException
    {
        return parse(results.getString(columnPos), fieldType.getType());
    }

    private Object parse(String src, Class<?> type)
    {
        if (src == null)
            return null;
        if (type == DateTime.class)
            return formatter.parseDateTime(src);
        if (type == LocalDateTime.class)
            return formatter.parseLocalDateTime(src);

        return src;
    }

    private Object print(Object src)
    {
        if (src == null)
            return null;
        if (src instanceof DateTime)
            return ((DateTime) src).toDate();
        if (src instanceof LocalDateTime)
            return ((LocalDateTime) src).toDate();
        return src;
    }
}
