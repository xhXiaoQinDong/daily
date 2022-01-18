package com.xlfc.concurrent.lock.redisson;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.handler.ConnectionWatchdog;
import org.redisson.config.Config;


/**
 * @Author: xiaoqindong
 * @CreateDate: 2022-01-16
 * @Description:
 */
public class RedissonTest {
    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://ip:6379");
        config.useSingleServer().setPassword("密码");
        final RedissonClient client = Redisson.create(config);

        RLock lock = client.getLock("lock");

        try{
            lock.lock();

            System.out.println("线程一加锁，开始执行逻辑.......");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程一逻辑执行完毕.......");
        }finally{
            lock.unlock();
            System.out.println("线程一释放锁");
        }
        System.out.println("线程一执行结束");


    }
}
