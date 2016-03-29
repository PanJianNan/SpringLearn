package com.yabadun.annotation;

import java.lang.annotation.*;

/**
 * RequestMapping
 *
 * @author panjn
 * @date 2016/3/4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface RequestMapping {
    String value() default "";
}
