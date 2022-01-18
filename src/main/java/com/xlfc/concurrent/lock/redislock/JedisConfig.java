package com.xlfc.concurrent.lock.redislock;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * JedisPool
 *
 * @author xlfc
 * @date 2022/1/16
 */
public class JedisConfig {

	public static JedisPool redisPoolFactory() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(8);
		jedisPoolConfig.setMaxIdle(500);
		jedisPoolConfig.setMinIdle(0);
		return new JedisPool(jedisPoolConfig, "ip", 6379, 3000, "密码");
	}
}