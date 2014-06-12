package me.wener.cbhistory.utils;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import jodd.props.Props;
import lombok.Getter;

public class PropsModule extends AbstractModule
{
    @Getter
    private final Props props;
    private final InProps inProps;

    private PropsModule(Props props)
    {
        this.props = props;
        this.inProps = InProps.in(props);
    }

    public static PropsModule of(Props props)
    {
        return new PropsModule(props);
    }

    @Override
    protected void configure()
    {
        bindListener(Matchers.any(), new TypeListener()
        {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter)
            {
                Class<? super I> clazz = type.getRawType();
                for (Field field : clazz.getDeclaredFields())
                {
                    Prop prop = field.getAnnotation(Prop.class);
                    if (prop == null)
                        continue;

                    encounter.register(new PropInjector<I>(prop, field));
                }
            }
        });
    }
    private static enum TargetType
    {
        Normal, List, Map
    }
    private static class PropInjectException extends RuntimeException
    {
        public PropInjectException(String message)
        {
            super(message);
        }

        public PropInjectException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
    private class PropInjector<T> implements MembersInjector<T>
    {
        private final Prop prop;
        private final Field field;
        private TargetType targetType = TargetType.Normal;
        private Class<?> type;

        PropInjector(Prop prop, Field field)
        {
            this.prop = prop;
            this.field = field;
            field.setAccessible(true);

            type = field.getType();
            if (type == List.class)
            {
                targetType = TargetType.List;
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                type = (Class<?>) genericType.getActualTypeArguments()[0];
            }else if (type == Map.class)
            {
                targetType = TargetType.Map;
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                type = (Class<?>) genericType.getActualTypeArguments()[1];
            }
        }

        @Override
        public void injectMembers(T instance)
        {
            try {
                Object val = null;
                switch (targetType)
                {
                    case Normal:
                        val = inProps.as(prop.value(), type);
                        break;
                    case List:
                        val = inProps.asList(prop.value(), type, prop.withSubValue());
                        break;
                    case Map:
                        val = inProps.asMap(prop.value(), type, prop.fullKey(), prop.withSubValue());
                        break;
                }

                if (! prop.optional())
                {
                    if (val == null
                            || (val instanceof Map && ((Map) val).isEmpty())
                            || (val instanceof List && ((List) val).isEmpty()))
                    {
                        String msg = "以 %s 方式注入 %s 失败, 注入类型为 %s.";
                        throw new PropInjectException(String.format(msg, targetType, prop.value(), type));
                    }
                }

                field.set(instance, val);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
