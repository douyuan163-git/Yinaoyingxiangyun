package cn.tieling.yibao.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 封装常用 Redis 操作，用于 Token 缓存、会话管理等
 */
@Slf4j
@Component
public class RedisUtil {

    private static final String TOKEN_PREFIX = "sso:token:";
    private static final String USER_PREFIX = "sso:user:";
    private static final String BLACKLIST_PREFIX = "sso:blacklist:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储 Token（用于 SSO 会话管理）
     */
    public void setToken(String token, Object userInfo, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(TOKEN_PREFIX + token, userInfo, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis setToken 异常: {}", e.getMessage());
        }
    }

    /**
     * 获取 Token 对应的用户信息
     */
    public Object getToken(String token) {
        try {
            return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        } catch (Exception e) {
            log.error("Redis getToken 异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除 Token（登出）
     */
    public void deleteToken(String token) {
        try {
            redisTemplate.delete(TOKEN_PREFIX + token);
        } catch (Exception e) {
            log.error("Redis deleteToken 异常: {}", e.getMessage());
        }
    }

    /**
     * 将 Token 加入黑名单
     */
    public void addToBlacklist(String token, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "1", expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis addToBlacklist 异常: {}", e.getMessage());
        }
    }

    /**
     * 检查 Token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
        } catch (Exception e) {
            log.error("Redis isBlacklisted 异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 存储用户会话信息
     */
    public void setUserSession(Long userId, Object sessionInfo, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(USER_PREFIX + userId, sessionInfo, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis setUserSession 异常: {}", e.getMessage());
        }
    }

    /**
     * 获取用户会话信息
     */
    public Object getUserSession(Long userId) {
        try {
            return redisTemplate.opsForValue().get(USER_PREFIX + userId);
        } catch (Exception e) {
            log.error("Redis getUserSession 异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 通用 set 方法
     */
    public void set(String key, Object value, long expireSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis set 异常: {}", e.getMessage());
        }
    }

    /**
     * 通用 get 方法
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get 异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除 key
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete 异常: {}", e.getMessage());
        }
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis hasKey 异常: {}", e.getMessage());
            return false;
        }
    }
}
