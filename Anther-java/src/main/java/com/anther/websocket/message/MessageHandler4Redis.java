package com.anther.websocket.message;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.MessageSendDto;
import com.anther.utils.JsonUtils;
import com.anther.websocket.ChannelContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@ConditionalOnProperty(name = Constants.MESSAGEING_HANDLE_CHANNEL_KEY, havingValue = Constants.MESSAGEING_HANDLE_CHANNEL_REDIS)
@Slf4j
public class MessageHandler4Redis implements MessageHandler {

    private static final String MESSAGE_TOPIC = "message.topic";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Override
    public void listenMessage() {
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.addListener(MessageSendDto.class, (MessageSendDto, sendDto) -> {
            log.info("redis收到消息:{}", JsonUtils.convertObj2Json(sendDto));
            channelContextUtils.sendMessage(sendDto);
        });
    }

    @Override
    public void sendMessage(MessageSendDto sendDto) {
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        rTopic.publish(sendDto);
    }

    @PreDestroy
    public void destroy() {
        redissonClient.shutdown();
    }
}
