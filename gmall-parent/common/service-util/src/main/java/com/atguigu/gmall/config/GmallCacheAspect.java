package com.atguigu.gmall.config;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @time: 2020/12/6 0:24
 * @author: LIANGBO
 */
@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    @SneakyThrows
    @Around("@annotation(com.atguigu.gmall.config.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {
        //1.声明一个object对象
        Object result = null;
        String cacheKey = "";
        //2.获取方法信息(动态代理切点利用反射形式获取类的信息)
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
             //2.1获取方法名
        String name = methodSignature.getMethod().getName();
            //2.2返回行和参数
        Class returnType = methodSignature.getReturnType();
        Object[] args = point.getArgs();
            //2.3拼接参数
        for (Object arg : args) {
            cacheKey = cacheKey + ":" + arg;
        }
        //3.得到注解信息
        GmallCache gmallCache = methodSignature.getMethod().getAnnotation(GmallCache.class);
        //4.查询缓存
        result = redisTemplate.opsForValue().get(cacheKey);
        //5.判断缓存中是否
        if (null == result) {
            System.out.println("获得分布式锁");
            //6.添加分布式锁
            String key = UUID.randomUUID().toString().toString();
            Boolean keys = redisTemplate.opsForValue().setIfAbsent(cacheKey + ":lock", key, 2, TimeUnit.SECONDS);

            if (keys) {
                //7.线程拿到执行AOP方法
                result = point.proceed();
                //8.判断执行同步缓存
                if (null == result) {
                    redisTemplate.opsForValue().set(cacheKey, result, 2, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(cacheKey, result);
                }
                //9.释放分布式锁
                String openkey = (String) redisTemplate.opsForValue().get(cacheKey + ":lock");
                if (key.equals(openkey)) {
                    System.out.println("释放了锁");
                    //释放了锁
                    redisTemplate.delete(cacheKey + ":lock");
                }
            } else {
                //没有拿到自旋锁，方法进行自旋
                System.out.println("没有拿到自旋锁，方法进行自旋");
                Thread.sleep(5000);
                //经过几秒后重新查询缓存
                return redisTemplate.opsForValue().get(cacheKey);

            }
        }
        return result;
    }
}
