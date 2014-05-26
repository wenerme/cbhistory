package me.wener.cbhistory.util.json;

import com.google.common.base.Strings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import me.wener.cbhistory.util.Same;
import org.joda.time.DateTime;

public final class DateTimeTypeConvert
        implements JsonDeserializer<DateTime>,
        JsonSerializer<DateTime>
{
    @Override
    public DateTime deserialize(final JsonElement json,
                                final Type type,
                                final JsonDeserializationContext context)
            throws JsonParseException
    {

        return Strings.isNullOrEmpty(json.getAsString()) ? null : Same.getDateTimeFormatter().parseDateTime(json.getAsString());
    }

    @Override
    public JsonElement serialize(final DateTime src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context)
    {
        return new JsonPrimitive(src == null ? "" : src.toString());
    }
}