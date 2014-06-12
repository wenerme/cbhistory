package me.wener.cbhistory.utils;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

/**
 * 使用 Gson 时将其合并到另外一个实例
 */
public class InstanceCreatorWithInstance<T> implements InstanceCreator<T>
{
    T instance;

    public InstanceCreatorWithInstance(T instance)
    {
        this.instance = instance;
    }

    @Override
    public T createInstance(Type type)
    {
        return instance;
    }
}
