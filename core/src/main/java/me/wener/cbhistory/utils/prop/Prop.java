package me.wener.cbhistory.utils.prop;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface Prop
{
    String value();

    /**
     * 如果注入的值为 null, 则会抛出异常
     */
    boolean optional() default false;

    /**
     * 指明集合类型包含元素的类型  List&lt;type&gt;, Map&lt;string,type&gt;
     */
//    Class<?> type() default String.class;

    boolean fullKey() default false;

    boolean withSubValue() default false;

    /**
     * 是否忽略定义的 {@link PropSection}
     */
    boolean ignoreSection() default false;
}
