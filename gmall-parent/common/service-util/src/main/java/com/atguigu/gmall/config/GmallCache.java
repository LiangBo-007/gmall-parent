package com.atguigu.gmall.config;

/**
 * @description:
 * @time: 2020/12/6 0:19
 * @author: LIANGBO
 */
public @interface GmallCache {
    public String skuPrefix() default "sku:";

    public String spuPrefix() default "spu:";

    public String prefix() default "GmallCache:";

}
