package com.anther.websocket.message;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.enums.MessageSend2TypeEnum;
import com.anther.utils.JsonUtils;
import com.anther.websocket.ChannelContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import static com.mysql.cj.conf.PropertyKey.logger;

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

    public void onMessage(MessageSendDto messageSendDto) {
        log.info("Redis 订阅收到消息，准备向本地 WebSocket 客户端分发: {}", messageSendDto);

        // 如果是单聊（私聊）
        if (MessageSend2TypeEnum.USER.getType().equals(messageSendDto.getMessageSend2Type())) {

            // 尝试将消息推给接收者。
            // 内部逻辑：检查接收者的 Channel 是否在本机 USER_CONTEXT_MAP 中。如果在，就用 WebSocket 刷出去。
            channelContextUtils.sendMessage(messageSendDto);

            // 【关键点】如果要做多端同步（比如我发了一条消息，我的手机端和电脑端都要能看到这句话）
            // 需要把这条消息也推送给 "发送者" 自己！
            channelContextUtils.sendMessage(messageSendDto);
        }
    }
}
