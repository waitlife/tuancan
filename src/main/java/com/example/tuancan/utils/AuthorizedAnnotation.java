package com.example.tuancan.utils;

import java.lang.annotation.*;

/**
 * @author xiaoqianyong
 * @description
 * @create 2019-04-01-21:19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface AuthorizedAnnotation {
    String value() default "";
}
