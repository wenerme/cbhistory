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
import org.joda.time.LocalDateTime;

public class LocalDateTimeTypeConvert
        implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime>
{
    @Override
    public LocalDateTime deserialize(JsonElement json,
                                     Type typeOfT,
                                     JsonDeserializationContext context)
            throws JsonParseException
    {
        return Strings.isNullOrEmpty(json.getAsString()) ? null: Same.getDateTimeFormatter().parseLocalDateTime(json.getAsString()) ;
    }

    @Override
    public JsonElement serialize(LocalDateTime src,
                                 Type typeOfSrc,
                                 JsonSerializationContext context)
    {
        return new JsonPrimitive(src == null ? "" : src.toString());
    }
}

