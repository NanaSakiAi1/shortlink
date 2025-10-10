package com.nageoffer.shortlink.shortlinkporject.mq.consumer;


import com.nageoffer.shortlink.shortlinkporject.common.biz.user.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.shortlinkporject.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.shortlinkporject.mq.idempotent.MessageQueueIdempotentHandler;
import com.nageoffer.shortlink.shortlinkporject.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.nageoffer.shortlink.shortlinkporject.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;


/**
 * 延迟记录短链接统计组件
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DelayShortLinkStatsConsumer implements InitializingBean {

    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;
    public void onMessage() {
        Executors.newSingleThreadExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("delay_short-link_stats_consumer");
                            thread.setDaemon(Boolean.TRUE);
                            return thread;
                        })
                .execute(() -> {
                    RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
                    RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                    for (; ; ) {
                        try {

                            ShortLinkStatsRecordDTO statsRecord = delayedQueue.poll();
                            if (statsRecord != null) {
                                if(!messageQueueIdempotentHandler.isMessageProcessed(statsRecord.getKeys())){
                                    if(messageQueueIdempotentHandler.isAccomplished(statsRecord.getKeys())){
                                        return;
                                    }
                                    throw new ServiceException("消息未完成流程，需要消息队列重试");

                                }
                                try{
                                    shortLinkService.shortLinkStats(null, null, statsRecord);
                                }catch (Throwable ex){
                                    messageQueueIdempotentHandler.delMessageProcessed(statsRecord.getKeys());
                                    log.error("记录短连接监控消费异常", ex);
                                }
                                messageQueueIdempotentHandler.setAccomplished(statsRecord.getKeys());
                                continue;
                            }
                            LockSupport.parkUntil(500);
                        } catch (Throwable ignored) {
                        }
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        onMessage();
    }
}
