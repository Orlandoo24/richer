package com.astrosea.richer.annotation;//package com.astrosea.richer.annotation;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
///**
// * 基于 Redisson 分布式锁注解组件
// */
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface RedissonLock {
//
//	/**
//	 * 锁对应的 key
//	 */
//	String key();
//
//	/**
//	 * 阻塞等待时间为 waitTime 默认为 10s
//	 */
//	long waitTime() default 10; // 等待时间，默认为 10 秒
//
//	/**
//	 * 过期释放时间为 leaseTime 默认为 30s
//	 */
//	long leaseTime() default 30; // 存活时间，默认为 30 秒
//
//}