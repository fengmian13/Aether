package com.anther.websocket.netty;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.PeerConnectionDataDto;
import com.anther.entity.dto.PeerMessageDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.MessageSend2TypeEnum;
import com.anther.entity.enums.MessageTypeEnum;
import com.anther.redis.RedisComponent;
//import com.anther.service.MeetingInfoService;
import com.anther.service.ChatMessageService;
import com.anther.utils.JsonUtils;
import com.anther.websocket.message.MessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description ws 业务处理
 * @Author 程序员老罗
 * @Date 2023/12/17 10:10
 */

/**
 * 设置通道共享
 */
@ChannelHandler.Sharable
@Component("handlerWebSocket")
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(HandlerWebSocket.class);

//    @Resource
//    private MeetingInfoService meetingInfoService;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private RedisComponent redisComponet;

    @Resource
    private MessageHandler messageHandler;

    /**
     * 当通道就绪后会调用此方法，通常我们会在这里做一些初始化操作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Channel channel = ctx.channel();
        logger.info("有新的连接加入。。。");
    }

    /**
     * 当通道不再活跃时（连接关闭）会调用此方法，我们可以在这里做一些清理工作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("有连接已经断开。。。");
//        meetingInfoService.removeContext(ctx.channel());TODO:处理连接断开的逻辑
    }

    /**
     * 读就绪事件 当有消息可读时会调用此方法，我们可以在这里读取消息并处理。
     *
     * @param ctx
     * @param textWebSocketFrame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        logger.info("收到客户端发来的 WebSocket 消息: {}", text);

        //处理心跳解析报错刷屏
        if ("heart beat".equals(text)) {
            // 也可以给前端回一个心跳，防止前端超时断开
            // ctx.channel().writeAndFlush(new TextWebSocketFrame("heart beat ok"));
            return;
        }

        try {
            // 1. 将收到的 JSON 字符串转换为 MessageSendDto
            MessageSendDto messageSendDto = JsonUtils.convertJson2Obj(text, MessageSendDto.class);

            // 2. 从 Channel 属性中获取当前用户的 userId (这是你在 HandlerTokenValidation 中鉴权通过时存入的)
            Channel channel = ctx.channel();
            Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
            String sendUserId = attribute.get();

            if (sendUserId == null) {
                logger.warn("Channel 中未获取到合法的用户ID，忽略该消息");
                return;
            }

            // 3. 将发送人的 userId 补充到消息体中
            messageSendDto.setSendUserId(sendUserId);

            // 4. 判断如果是普通的纯文本聊天消息，则交由业务层处理落库和分发
            if (MessageTypeEnum.CHAT_TEXT_MESSAGE.getType().equals(messageSendDto.getMessageType())) {
                logger.info("WebSocket 消息: 转Dto{}", messageSendDto);
                chatMessageService.saveAndSendMessage(messageSendDto);
            }

        } catch (Exception e) {
            logger.error("处理 WebSocket 消息时发生异常", e);
        }
    }
}