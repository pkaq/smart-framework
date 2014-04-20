package org.smart4j.plugin.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.smart4j.plugin.cache.Expiry;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {

    String value();

    long expiry() default Expiry.ETERNAL;
}
