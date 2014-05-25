package me.wener.cbhistory.core.pluggable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlugInfo
{
    /** 是否加载, 默认为 true  */
    boolean load() default true;
    /** 作者信息 */
    String author() default "unknown";
    /** 插件名 */
    String name() default "unknown";
}
