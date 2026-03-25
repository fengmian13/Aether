package com.anther.websocket;

import com.alibaba.fastjson.JSON;
import com.anther.entity.dto.VideoMeetingExitDto;
import com.anther.entity.dto.MeetingMemberDto;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.MeetingMemberStatusEnum;
import com.anther.entity.enums.MessageSend2TypeEnum;
import com.anther.entity.enums.MessageTypeEnum;
import com.anther.entity.enums.UserContactTypeEnum;
import com.anther.entity.po.UserInfo;
import com.anther.entity.query.UserInfoQuery;
import com.anther.mappers.UserInfoMapper;
import com.anther.redis.RedisComponent;
import com.anther.utils.JsonUtils;
import com.anther.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component("channelContextUtils")
@Slf4j
public class ChannelContextUtils {

    @Resource
    private RedisComponent redisComponet;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    public static final ConcurrentMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap();

    public static final ConcurrentMap<String, ChannelGroup> MEETING_ROOM_CONTEXT_MAP = new ConcurrentHashMap();

    public static final ConcurrentMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap();


    private void sendMsg2Group(MessageSendDto messageSendDto) {
        if (messageSendDto.getContactId() == null) {
            return;
        }

        ChannelGroup group = GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (group == null) {
            return;
        }
        group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageSendDto)));

        //移除群聊
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(messageSendDto.getMessageType());
        if (MessageTypeEnum.LEAVE_GROUP == messageTypeEnum || MessageTypeEnum.REMOVE_GROUP == messageTypeEnum) {
            String userId = (String) messageSendDto.getExtendData();
            redisComponet.removeUserContact(userId, messageSendDto.getContactId());
            Channel channel = USER_CONTEXT_MAP.get(userId);
            if (channel == null) {
                return;
            }
            group.remove(channel);
        }

        if (MessageTypeEnum.DISSOLUTION_GROUP == messageTypeEnum) {
            GROUP_CONTEXT_MAP.remove(messageSendDto.getContactId());
            group.close();
        }
    }

    private void sendMsg2User(MessageSendDto messageSendDto) {
        if (messageSendDto.getContactId() == null) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (channel == null) {
            log.warn("用户不在线: {}", messageSendDto.getContactId());
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageSendDto)));
        /**
         * 强制下线特殊处理
         */
        if (MessageTypeEnum.FORCE_OFF_LINE.getType().equals(messageSendDto.getMessageType())) {
            closeContext(messageSendDto.getContactId());
        }
    }

    public void closeContext(String userId) {
        if (StringTools.isEmpty(userId)) {
            return;
        }
        redisComponet.cleanUserTokenByUserId(userId);
        Channel channel = USER_CONTEXT_MAP.get(userId);
        USER_CONTEXT_MAP.remove(userId);
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * 发送消息
     *
     * @param messageSendDto
     */
    public void sendMessage(MessageSendDto messageSendDto) {
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(messageSendDto.getContactId());
        switch (contactTypeEnum) {
            case USER:
                sendMsg2User(messageSendDto);
                break;
            case GROUP:
                sendMsg2Group(messageSendDto);
        }
    }


    //用户移除群
    public void removeContextFromGroup(String userId, String meetingId) {
        Channel context = USER_CONTEXT_MAP.get(userId);
        if (null == context) {
            return;
        }
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingId);
        if (group != null) {
            group.remove(context);
        }
    }

    public void removeContextGroup(String meetingId) {
        MEETING_ROOM_CONTEXT_MAP.remove(meetingId);
    }

    public void removeContextUser(String userId) {
        USER_CONTEXT_MAP.remove(userId);
    }


    public void addContext(String userId, Channel channel) {
        try {
            String channelId = channel.id().toString();
            AttributeKey attributeKey = null;
            if (!AttributeKey.exists(channelId)) {
                attributeKey = AttributeKey.newInstance(channel.id().toString());
            } else {
                attributeKey = AttributeKey.valueOf(channel.id().toString());
            }
            channel.attr(attributeKey).set(userId);
            USER_CONTEXT_MAP.put(userId, channel);

            //更新最后登录时间，针对重连后更新最后登录时间
            UserInfo userInfo = new UserInfo();
            userInfo.setLastLoginTime(System.currentTimeMillis());
            userInfoMapper.updateByUserId(userInfo, userId);

//            //如果在会议中，自动加入会议 TODO: 重新处理
//            TokenUserInfoDto tokenUserInfoDto = redisComponet.getTokenUserInfoDtoByUserId(userId);
//            if (tokenUserInfoDto.getCurrentMeetingId() == null) {
//                return;
//            }
//
//            //加入会议室
//            addMeetingRoom(tokenUserInfoDto.getCurrentMeetingId(), userId);
        } catch (Exception e) {
            log.error("初始化链接失败", e);
        }
    }

    /**
     * 加入会议室
     *
     * @param meetingId
     */
    public void addMeetingRoom(String meetingId, String userId) {
        Channel context = USER_CONTEXT_MAP.get(userId);
        if (null == context) {
            return;
        }
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            MEETING_ROOM_CONTEXT_MAP.put(meetingId, group);
        }
        Channel channel = group.find(context.id());
        if (channel == null) {
            group.add(context);
        }
    }

    private void add2Group(String groupId, Channel context) {
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        if (context == null) {
            return;
        }
        group.add(context);
    }

    public void addUser2Group(String userId, String groupId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        add2Group(groupId, channel);
    }
}
