package cn.iecas.store.redis.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


import com.alibaba.fastjson.JSON;

import cn.iecas.general.utils.prop.ResourceLoad;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * redis操作封装类
 * 
 * @author hhm
 *
 */
public class RedisClient {

	private static JedisPool pool = null;

	static {
		getJedisPool();
	}

	/**
	 * 获取redis连接池
	 *
	 * @return JedisPool
	 */
	public static JedisPool getJedisPool() {
		if (pool == null) {
			initJedisPool();
		}
		return pool;
	}

	/**
	 * 初始化redis连接池参数
	 */
	private static synchronized void initJedisPool() {

		if (pool == null) {

			Properties prop = ResourceLoad.loadProperties("redis");

			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			// config.setMaxTotal(Integer.parseInt(prop.getProperty("MaxTotal")));
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(Integer.parseInt(prop.getProperty("MaxIdle")));
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(Integer.parseInt(prop.getProperty("MaxWaitMillis")));
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			// 逐出连接的最小空闲时间，默认为1800000（30分钟）
			// config.setMinEvictableIdleTimeMillis(Integer.parseInt(prop.getProperty("MinEvictableIdleTimeMillis")));
			pool = new JedisPool(config, prop.getProperty("ip_redis"), Integer.parseInt(prop.getProperty("port_redis")), 100000);
		}
	}

	public static Jedis getJedis() {
		return getJedisPool().getResource();
	}

	/**
	 * 获取数据（key-value）
	 *
	 * @param key
	 * @return If the key does not exist null is returned.
	 */
	public static String get(String key) {
		String value = null;

		Jedis jedis = getJedis();
		value = jedis.get(key);
		jedis.close();

		return value;
	}
	
	public static <T> T get(String key, Class<T> clazz) {
		String value = get(key);
		
		if(value==null){
			return null;
		}
		
		T t = JSON.parseObject(value, clazz);
		
		return t;
	}

	/**
	 * 模糊查询key
	 * 
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		Set<String> keys = null;

		Jedis jedis = getJedis();
		keys = jedis.keys(pattern);
		jedis.close();

		return keys;
	}

	/**
	 * 置入数据（key-value）
	 *
	 * @param key
	 * @param value
	 */
	public static synchronized void set(String key, String value) {
		Jedis jedis = getJedis();
		jedis.set(key, value);
		jedis.close();
	}
	public static synchronized void set(String key, Object obj) {
		String json = JSON.toJSONString(obj);
		set(key, json);
	}

	/**
	 * 设置key-value 并设置过期时间
	 * 
	 * @param key
	 * @param seconds
	 *            秒数
	 * @param value
	 */
	public static synchronized void set(String key, int seconds, String value) {
		Jedis jedis = getJedis();
		jedis.setex(key, seconds, value);
		jedis.close();
	}
	public static synchronized void set(String key, int seconds, Object value) {
		set(key, seconds, JSON.toJSONString(value));
	}

	public static synchronized void setList(String key, List<String> list) {
		Jedis jedis = getJedis();
		jedis.del(key);
		jedis.rpush(key, list.toArray(new String[list.size()]));
		jedis.close();
	}

	/**
	 * Add the string value to the head (LPUSH) of the list
	 * stored at key. If the key does not exist an empty list is created just
	 * before the append operation. If the key exists but is not a List an error
	 * is returned.
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void listAdd(String key, String value) {
		Jedis jedis = getJedis();
		jedis.rpush(key, value);
		jedis.close();
	}

	public static long listSize(String key) {
		Jedis jedis = getJedis();
		long rs = jedis.llen(key);
		jedis.close();
		return rs;
	}

	public static List<String> getList(String key) {
		Jedis jedis = getJedis();
		List<String> ls = jedis.lrange(key, 0, -1);
		jedis.close();
		return ls;
	}

	public static long queueSize(String key) {
		return listSize(key);
	}

	public static <T> List<T> getList(String key, Class<T> clazz){
		List<String> ls = getList(key);
		List<T> queue = new ArrayList<T>();
		for (String s : ls) {
			T t = JSON.parseObject(s, clazz);
			queue.add(t);
		}
		return queue;
	}
	
	public static synchronized <T> void setListObject(String key, List<T> queue){
		Jedis jedis = getJedis();
		String[] ss = new String[queue.size()];
		int i=0;
		for (T t : queue) {
			ss[i] = JSON.toJSONString(t);
			i++;
		}
		jedis.del(key);
		jedis.rpush(key, ss);
		jedis.close();
	}
	public static <T> Queue<T> getQueue(String key, Class<T> clazz){
		List<String> ls = getList(key);
		Queue<T> queue = new LinkedBlockingQueue<T>();
		for (String s : ls) {
			T t = JSON.parseObject(s, clazz);
			queue.add(t);
		}
		return queue;
	}
	
	public static synchronized <T> void setQueue(String key, Queue<T> queue){
		Jedis jedis = getJedis();
		String[] ss = new String[queue.size()];
		int i=0;
		for (T t : queue) {
			ss[i] = JSON.toJSONString(t);
			i++;
		}
		jedis.del(key);
		jedis.rpush(key, ss);
		jedis.close();
	}

	/**
	 * 设置key的过期时间
	 * 
	 * @param key
	 * @param seconds
	 */
	public static synchronized void setKeyExpire(String key, int seconds) {
		if(seconds<=0){
			return;
		}
		
		Jedis jedis = getJedis();
		jedis.expire(key, seconds);
		jedis.close();
	}

	/**
	 * 删除key
	 * 
	 * @param key
	 */
	public static synchronized void del(final String... key) {
		Jedis jedis = getJedis();
		jedis.del(key);
		jedis.close();
	}

	/**
	 * 定时器
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized long incr(String key) {
		Jedis jedis = getJedis();
		long rs = jedis.incr(key);
		jedis.close();
		return rs;
	}

	/**
	 * 定时器
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized long incr(String key, long val) {
		Jedis jedis = getJedis();
		long rs = jedis.incrBy(key, val);
		jedis.close();
		return rs;
	}

	/**
	 * 定时器
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized long decr(String key) {
		Jedis jedis = getJedis();
		long rs = jedis.decr(key);
		jedis.close();
		return rs;
	}

	/**
	 * 定时器
	 * 
	 * @param key
	 * @return
	 */
	public static synchronized long decr(String key, long val) {
		Jedis jedis = getJedis();
		long rs = jedis.decrBy(key, val);
		jedis.close();
		return rs;
	}

	/**
	 * 判别key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		Jedis jedis = getJedis();
		boolean b = jedis.exists(key);
		jedis.close();

		return b;
	}

	public static synchronized void setMap(String key, Map<String, String> map) {
		Jedis jedis = getJedis();
		jedis.hmset(key, map);
		jedis.close();
	}

	public static synchronized void setMap(String key, String field, String value) {
		Jedis jedis = getJedis();
		jedis.hset(key, field, value);
		jedis.close();
	}
	public static synchronized void setMap(String key, String field, Object value) {
		setMap(key, field, JSON.toJSONString(value));
	}

	public static Map<String, String> getMap(String key) {
		Jedis jedis = getJedis();
		Map<String, String> value = jedis.hgetAll(key);
		jedis.close();
		return value;
	}

	public static String getMapValue(String key, String field) {
		Jedis jedis = getJedis();
		String value = jedis.hget(key, field);
		jedis.close();
		return value;
	}

	public static <T> T getMapValue(String key, String field, Class<T> clazz) {
		return JSON.parseObject(getMapValue(key, field), clazz);
	}

	public static List<String> getMapValueList(String key) {
		Jedis jedis = getJedis();
		List<String> value = jedis.hvals(key);
		jedis.close();
		return value;
	}

	public static Set<String> getMapKeySet(String key) {
		Jedis jedis = getJedis();
		Set<String> value = jedis.hkeys(key);
		jedis.close();
		return value;
	}

	public static boolean existsMapKey(String key, String field) {
		Jedis jedis = getJedis();
		boolean b = jedis.hexists(key, field);
		jedis.close();

		return b;
	}

	public static synchronized void delMapKey(String key, String... fields) {
		Jedis jedis = getJedis();
		jedis.hdel(key, fields);
		jedis.close();
	}
	
	public static synchronized void setAdd(String key, String... fields) {
		Jedis jedis = getJedis();
		jedis.sadd(key, fields);
		jedis.close();
		
	}
	
	public static synchronized Set<String> setGet(String key) {
		Jedis jedis = getJedis();
		Set<String> elems=jedis.smembers(key);
		jedis.close();
		return elems;
		
		
	}
	
	
	public static void main(String[] args) {
		RedisClient.get("name");
	}

}
