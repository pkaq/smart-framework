package org.smart4j.plugin.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Job {

    public enum Type {
        CRON, TIMER
    }

    Type type() default Type.CRON;

    String value();

    /* 当 type 为 TIMER 时，支持以下属性 */

    int second() default 0;

    int count() default 0;

    String start() default "";

    String end() default "";
}
