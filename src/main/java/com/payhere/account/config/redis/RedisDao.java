package com.payhere.account.config.redis;


import io.jsonwebtoken.Jwts;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisDao(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**만료 기한을 가지지 않은 데이터를 생성또는 설정하는 메소드**/
    public void setValues(String key, String value) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value);
    }

    public void setValues(String key, String value, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, duration);
    }

    public void setValues(String key, String value, long timeout, TimeUnit unit) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, timeout, unit);
    }

    /**레디스에 저장되어있는 데이터를 가져오는 메소드**/
    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }
    /**로그아웃시에 리프레쉬 토큰을 삭제하기 위한 메소드**/
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    /**이미 생성되어있거나 생성되어있지 않은 데이터를 만료기한까지 설정해 저장하는 메소드**/
    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key,value,expireDuration);
    }




}
