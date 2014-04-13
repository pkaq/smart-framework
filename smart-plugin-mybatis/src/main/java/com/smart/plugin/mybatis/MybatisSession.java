package com.smart.plugin.mybatis;

import java.lang.annotation.*;

/**
 * Description: 自动处理Mybatis的Session
 * Author: liuzh
 * Update: liuzh(2014-04-09 10:57)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MybatisSession {

}
