package com.xlfc.concurrent.lock.redislock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author: xiaoqindong
 * @CreateDate: 2022-01-17
 * @Description:
 */
public class RedisLockTest {
    private static Logger logger = LoggerFactory.getLogger(RedisLockTest.class);

    private static int SIZE=10;

    private static int THREAD_NUM=10;

    protected static ExecutorService executorService =  new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 50, 0L, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>(1024)
            , new ThreadFactoryBuilder().setNameFormat("Thread-name-%d").setUncaughtExceptionHandler((thread, throwable)-> logger.error("ThreadPool {} got exception", thread,throwable)).build()
            , new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args){
        RedisLock redisLock=new RedisLock();

        for (int i = 0; i <THREAD_NUM ; i++) {
            executorService.execute(()->{
                String s = UUID.randomUUID().toString();
                try {
                    redisLock.lock(s);

                    System.out.println(Thread.currentThread().getName()+"=======加锁：" + s);

                    System.out.println("当前剩余："+--SIZE);
                }finally {
                    redisLock.unlock(s);
                    System.out.println(Thread.currentThread().getName()+"========解锁：" + s);
                }

            });
        }
        executorService.shutdown();
    }

}
