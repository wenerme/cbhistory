package me.wener.cbhistory.utils.prop;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 使在该类中使用 {@link Prop} 时有一个默认的 Section
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface PropSection
{
    String value();
}
