package com.nageoffer.shortlink.shortlinkporject.mq.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息队列幂等处理
 *
 */
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {
    private final StringRedisTemplate stringRedisTemplate;

    private static final String IDEMPOTENT_KEY_PREFIX = "short_link:idempotent";
    /**
     * 判断消息是否处理过
     *
     * @param messageId 消息ID
     * @return true:处理过，false:未处理
     */
    public Boolean isMessageProcessed(String messageId){
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "0",2, TimeUnit.MINUTES));
    }

    /**
     * 消息处理成功删除消息处理状态（幂等）
     *
     * @param messageId 短链接ID
     */
    public Boolean isAccomplish(String messageId){
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Objects.equals(stringRedisTemplate.opsForValue().get(key),1);
    }

    public void setAccomplish(String messageId){
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.opsForValue().setIfAbsent(key,"1",2, TimeUnit.MINUTES);
    }

    /**
     * 如果消息处理遇到异常情况删除消息处理状态（幂等）
     *
     * @param messageId 短链接ID
     */
    public void delMessageProcessed(String messageId){
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.delete(key);
    }
}
