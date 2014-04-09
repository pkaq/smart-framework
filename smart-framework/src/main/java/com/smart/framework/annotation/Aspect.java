package com.smart.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    String pkg() default "";

    String cls() default "";

    Class<? extends Annotation> annotationClass() default Aspect.class;
}
