package com.astrosea.richer.annotation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootConfiguration
public class ThreadPoolConfig {

    @Bean(value = "jobCallbackAfterHandleThreadPool")
    public ExecutorService jobCallbackAfterHandleThreadPool() {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("job-callback-after-handle-pool-%d").build();

        ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20480), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        return pool;
    }

    @Bean(value = "bizThreadPool")
    public ExecutorService bizThreadPool() {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("biz-thread-pool-%d").build();

        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20480), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        return pool;
    }

    @Bean("queryProdPool")
    public ExecutorService queryProdPool(){
        return new ThreadPoolExecutor(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            final AtomicInteger atomicInteger = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                String name = "prod-task-"+atomicInteger.getAndIncrement();
                return new Thread(null,r,name,0);
            }
        });
    }
}