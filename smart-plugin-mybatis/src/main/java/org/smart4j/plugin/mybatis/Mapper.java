package org.smart4j.plugin.mybatis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapper注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Mapper {
    /**
     * xml路径 - 如果不配置xml信息,那么只能使用mybatis的注解方法
     * <br>如@Select("select * from User where userid = #{userid}")
     * <br>配置xml信息后，会注册该xml
     * <br>该路径为相对于classpath的路径
     * @return
     */
    String value() default "";
}
