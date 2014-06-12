package me.wener.cbhistory.utils.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.joda.time.Instant;

public class InstantTypeConvert
        implements JsonDeserializer<Instant>, JsonSerializer<Instant>
{
    @Override
    public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context)
    {
        return new JsonPrimitive(src.getMillis());
    }

    @Override
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException
    {
        return new Instant(json.getAsLong());
    }
}
