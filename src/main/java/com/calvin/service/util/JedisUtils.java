package com.calvin.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.Resource;
import java.util.Set;

/**
 * redis
 * Created by Calvin.Ding on 2016/10/30.
 */
@Component
public class JedisUtils {

    /**
     * log组件
     */
    private static final Logger LOG = LoggerFactory.getLogger(JedisUtils.class);

    /**
     * redis线程池
     */
    @Resource
    protected JedisPool jedisPool;

    /**
     * 从jedis线程池中获取jedis实例
     * @throws JedisException Jedis实例化异常
     * @return jedis实例
     */
    public Jedis getJedis() throws JedisException {
        try {
            return jedisPool.getResource();
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * redis set
     * @param key KEY
     * @param value 值
     * @return boolean
     * @throws JedisException
     */
    public boolean set(String key, String value)  throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            boolean result = jedis.set(key, value).equals("OK");
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis get
     * @param key KEY
     * @return String
     * @throws JedisException
     */
    public String get(String key)  throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            String value = jedis.get(key);
            jedis.close();
            return value;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * 删除key对应值
     * @param key KEY
     * @throws JedisException jedis异常
     */
    public void delKey(String key) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            jedis.del(key);
            jedis.close();
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis hset
     * @param key KEY值
     * @param field 域
     * @param value 值
     * @param cacheSeconds 缓存过期时间(0表示不设过期时间)
     * @throws JedisException jedis异常
     */
    public void hset(String key, String field, String value, int cacheSeconds)
            throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            jedis.hset(key, field, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
            jedis.close();
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis hincrby
     * @param key KEY值
     * @param field 域
     * @param increase 增加值
     * @throws JedisException jedis异常
     */
    public void hincrby(String key, String field, int increase) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            jedis.hincrBy(key, field, increase);
            jedis.close();
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis sadd
     * @param key KEY值
     * @param member 成员
     * @throws JedisException
     */
    public void sadd(String key, String member) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            jedis.sadd(key, member);
            jedis.close();
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis sismember
     * @param key KEY值
     * @param member 成员
     * @return boolean
     * @throws JedisException
     */
    public boolean sismember(String key, String member) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            boolean result = jedis.sismember(key, member);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis zadd
     * @param key 键
     * @param score 分数
     * @param member 成员
     * @return 变化个数
     * @throws JedisException
     */
    public Long zadd(String key, double score, String member) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            Long result = jedis.zadd(key, score, member);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis zrange with scores
     * @param key 键
     * @param start 起始
     * @param end 结束
     * @return Set<Tuple>
     * @throws JedisException
     */
    public Set<Tuple> zrange(String key, long start, long end) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            Set<Tuple> result = jedis.zrangeWithScores(key, start, end);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    /**
     * redis zremrangeByScore
     * @param key 键
     * @param start 起始
     * @param end 结束
     * @return 删除个数
     * @throws JedisException
     */
    public long zremrangeByScore(String key, String start, String end) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            long result = jedis.zremrangeByScore(key, start, end);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    public long zcard(String key) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            long result = jedis.zcard(key);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

    public long zcount(String key, String start, String end) throws JedisException {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            long result = jedis.zcount(key, start, end);
            jedis.close();
            return result;
        } catch (JedisException e) {
            LOG.error(e.getMessage(), e);
            if (jedis != null) {
                jedis.close();
            }
            throw e;
        }
    }

}
