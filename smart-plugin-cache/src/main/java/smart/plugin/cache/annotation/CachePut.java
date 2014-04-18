package smart.plugin.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import smart.plugin.cache.Expiry;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {

    String value();

    long expiry() default Expiry.ETERNAL;
}
