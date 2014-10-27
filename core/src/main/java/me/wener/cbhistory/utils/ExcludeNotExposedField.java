package me.wener.cbhistory.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;
import lombok.Getter;

public class ExcludeNotExposedField implements ExclusionStrategy
{
    private static ExcludeNotExposedField serialize;
    private static ExcludeNotExposedField deserialize;
    @Getter
    private boolean forSerialize;

    /**
     * @param whenSerialization 如果为true,则使用 expose 的 serialize 参数,否则为 deserialize
     */
    private ExcludeNotExposedField(boolean whenSerialization)
    {
        this.forSerialize = whenSerialization;
    }

    public static ExcludeNotExposedField serialize()
    {
        if (serialize == null)
            serialize = new ExcludeNotExposedField(true);
        return serialize;
    }

    public static ExcludeNotExposedField deserialize()
    {
        if (deserialize == null)
            deserialize = new ExcludeNotExposedField(false);
        return deserialize;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f)
    {
        Expose expose = f.getAnnotation(Expose.class);
        // 注意,返回的值为是否跳过,所以需要取反
        if (expose != null)
        {
            if (forSerialize)
                return !expose.serialize();
            else
                return !expose.deserialize();
        }

        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz)
    {
        return false;
    }
}
