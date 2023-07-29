package com.astrosea.richer.aspect;//package com.astrosea.richer.aspect;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.log.Log;
//import com.astrosea.richer.annotation.RedissonLock;
//import org.apache.ibatis.logging.LogFactory;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.redisson.api.RedissonClient;
//import org.redisson.client.RedisException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.redisson.api.RLock;
//
//import java.util.concurrent.TimeUnit;
//
//
///**
// * 基于 Redisson 分布式锁注解组件实现
// */
//@Aspect
//@Component
//public class RedissonLockAspect {
//
//	private static final Log log = (Log) LogFactory.getLog(RedissonLockAspect.class);
//
//	@Autowired
//	private RedissonClient redissonClient;
//
//	// 环绕通知注解，拦截使用了 @RedissonLock 注解的方法
//	@Around("@annotation(redissonLock)")
//	public Object around(ProceedingJoinPoint point, RedissonLock redissonLock) {
//		// 获取 @RedissonLock 注解中指定的锁定 key
//		String key = redissonLock.key();
//		if (StrUtil.isEmpty(key)) {
//			throw new RuntimeException("The key of a distributed lock cannot be empty！");
//		}
//
//		// 获取 @RedissonLock 注解中指定的等待时间，默认为 10 秒
//		long waitTime = redissonLock.waitTime();
//
//		// 获取 @RedissonLock 注解中指定的 key 存活时间，默认为 30 秒
//		long leaseTime = redissonLock.leaseTime();
//
//		// 获取 Redisson 分布式锁 RLock 的实例
//		RLock lock = redissonClient.getLock("lock_" + key);
//		try {
//			// 尝试获取锁，等待时间为 waitTime 秒，锁存活时间为 leaseTime 秒
//			boolean isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
//
//			if (isLocked) {
//				// 执行业务逻辑方法
//				return point.proceed();
//			} else {
//				log.info("当前资源 " + key + " 被其他线程占用！");
//			}
//
//		} catch (RedisException | InterruptedException e) {
//			log.error("资源" + key + "加锁失败", e);
//			throw new RuntimeException("资源" + key + "加锁失败", e);
//		} catch (Throwable t) {
//			log.error("执行带有分布式锁 " + key + " 的业务逻辑时发生错误。", t);
//			throw new RuntimeException("执行带有分布式锁 " + key + " 的业务逻辑时发生错误。", t);
//		} finally {
//			// 无需判断当前线程是否持有锁，Redisson 已经集成了此功能
//			lock.unlock();
//			log.info("释放锁 + key");
//		}
//		//暂时先返回 null，根据后续的需求扩展
//		return null;
//	}
//}