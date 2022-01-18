package com.xlfc.concurrent.lock.redislock;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

/**
 * @Author: xiaoqindong
 * @CreateDate: 2022-01-17
 * @Description:
 */
public class RedisLock {
    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private static final long TIME = 10000;

    private static final SetParams PARAMS = SetParams.setParams().nx().px(TIME);

    private static final Long INTERNAL_LOCK_LEAST_TIME=1000l;

    private static final String prefix="prefix-";

    public boolean lock(String key){
        Jedis jedis= JedisConfig.redisPoolFactory().getResource();
        try {
            long begin = System.currentTimeMillis();

            while (true){
                String result = jedis.set(prefix, key, PARAMS);
                //如果返回值为Ok，那么就说明加锁成功
                if(StringUtils.equals("OK",result)){
                    logger.info("分布式锁获取成功，realKey："+prefix+key);
                    return true;
                }

                //自旋时间超过internalLockLeaseTime，那么就加锁失败
                if ((System.currentTimeMillis() - begin)>INTERNAL_LOCK_LEAST_TIME){
                    logger.info("分布式锁获取失败，realKey："+prefix+key);
                    return false;
                }
            }
        }finally {
            jedis.close();
        }
    }


    /**
     * 解锁
     * */
    public boolean unlock(String key){
        Jedis jedis=JedisConfig.redisPoolFactory().getResource();

        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                "   return redis.call('del',KEYS[1]) " +
                "else" +
                "   return 0 " +
                "end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(prefix),
                    Collections.singletonList(key));
            if (StringUtils.equals("1",result.toString())){
                logger.info("分布式锁删除成功，realKey："+prefix+key);
                return true;
            }
            logger.info("分布式锁删除失败，realKey："+prefix+key);
            return false;
        }finally {
            jedis.close();
        }
    }

}
