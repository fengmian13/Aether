package com.anther.websocket.netty;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.CallSignalDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.exception.BusinessException;
import com.anther.redis.RedisComponent;
import com.anther.service.CallService;
import com.anther.service.CallSignalService;
import com.anther.utils.JsonUtils;
import com.anther.utils.StringTools;
import com.anther.websocket.ChannelContextUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@ChannelHandler.Sharable
@Component("handlerWebSocket")
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(HandlerWebSocket.class);

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Resource
    private RedisComponent redisComponet;

    @Resource
    private CallSignalService callSignalService;

    @Resource
    private CallService callService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("websocket channel active: {}", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String userId = channelContextUtils.getUserId(ctx.channel());
        if (!StringTools.isEmpty(userId)) {
            callService.handleOffline(userId);
        }
        channelContextUtils.removeContext(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        String text = textWebSocketFrame.text();
        if (isHeartBeat(text)) {
            String userId = channelContextUtils.getUserId(ctx.channel());
            if (!StringTools.isEmpty(userId)) {
                redisComponet.saveUserHeartBeat(userId);
            }
            return;
        }
        String userId = channelContextUtils.getUserId(ctx.channel());
        if (StringTools.isEmpty(userId)) {
            return;
        }
        TokenUserInfoDto tokenUserInfoDto = redisComponet.getTokenUserInfoDtoByUserId(userId);
        if (tokenUserInfoDto == null) {
            return;
        }
        try {
            CallSignalDto signalDto = JsonUtils.convertJson2Obj(text, CallSignalDto.class);
            callSignalService.handleSignal(tokenUserInfoDto, signalDto);
        } catch (BusinessException e) {
            logger.warn("ignore invalid websocket payload, userId:{}, text:{}", userId, text);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("websocket handler exception, channelId:{}", ctx.channel().id(), cause);
        ctx.close();
    }

    private boolean isHeartBeat(String text) {
        if (StringTools.isEmpty(text)) {
            return false;
        }
        return Constants.PING.equalsIgnoreCase(text.trim())
                || Constants.HEART_BEAT.equalsIgnoreCase(text.trim());
    }
}
