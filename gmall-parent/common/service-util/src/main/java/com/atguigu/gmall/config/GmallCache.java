package com.atguigu.gmall.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @time: 2020/12/6 0:19
 * @author: LIANGBO
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    public String skuPrefix() default "sku:";

    public String spuPrefix() default "spu:";

    public String prefix() default "GmallCache:";

}
