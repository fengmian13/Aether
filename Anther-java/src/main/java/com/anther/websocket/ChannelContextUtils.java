package com.anther.websocket;

import com.alibaba.fastjson.JSON;
import com.anther.entity.constants.Constants;
import com.anther.entity.dto.*;
import com.anther.entity.enums.*;
import com.anther.entity.po.*;
import com.anther.entity.query.*;
import com.anther.mappers.*;
import com.anther.redis.RedisComponent;
import com.anther.utils.JsonUtils;
import com.anther.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
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

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;

    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;


    private void sendMsg2Group(MessageSendDto messageSendDto) {
        if (messageSendDto.getContactId() == null) {
            return;
        }

        String groupId = messageSendDto.getContactId();
        ChannelGroup group = GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (group == null) {
            log.error("尝试发送群聊失败！当前内存 GROUP_CONTEXT_MAP 中不存在群组: {}", groupId);
            log.info("当前内存中所有的群组有: {}", GROUP_CONTEXT_MAP.keySet());
            return;
        }
        log.info("群聊广播准备: 群 {} 当前在线通道数量为: {}", messageSendDto.getContactId(), group.size());
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

        UserInfo userInfo = new UserInfo();
        userInfo.setLastOffTime(System.currentTimeMillis());
        userInfoMapper.updateByUserId(userInfo, userId);
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
                System.out.println("sendMsg2User");
                sendMsg2User(messageSendDto);
                break;
            case GROUP:
                System.out.println("sendMsg2Group");
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

    public void removeContext(Channel channel) {
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if (!StringTools.isEmpty(userId)) {
            USER_CONTEXT_MAP.remove(userId);
        }
        redisComponet.removeUserHeartBeat(userId);

        //更新用户最后断线时间
        UserInfo userInfo = new UserInfo();
        userInfo.setLastOffTime(System.currentTimeMillis());
        userInfoMapper.updateByUserId(userInfo, userId);
    }


    public void addContext(String userId, Channel channel) {
        log.info("用户连接注册-addContext: {}", userId);
        try {

            String channelId = channel.id().toString();
            AttributeKey attributeKey = null;
            if (!AttributeKey.exists(channelId)) {
                attributeKey = AttributeKey.newInstance(channel.id().toString());
            } else {
                attributeKey = AttributeKey.valueOf(channel.id().toString());
            }
            channel.attr(attributeKey).set(userId);

            List<String> contactList = redisComponet.getUserContactList(userId);
            for (String groupId : contactList) {
                if (groupId.startsWith(UserContactTypeEnum.GROUP.getPrefix())) {
                    add2Group(groupId, channel);
                }
            }

            USER_CONTEXT_MAP.put(userId, channel);
            redisComponet.saveUserHeartBeat(userId);




            //更新最后登录时间，针对重连后更新最后登录时间
            UserInfo updateInfo = new UserInfo();
            updateInfo.setLastLoginTime(System.currentTimeMillis());
            userInfoMapper.updateByUserId(updateInfo, userId);

            //给用户发送一些消息
            //获取用户最后离线时间
            UserInfo userInfo = userInfoMapper.selectByUserId(userId);
            Long sourceLastOffTime = userInfo.getLastOffTime();
            //这里避免毫秒时间差，所以减去1秒的时间
            //如果时间太久，只取最近三天的消息数
            Long lastOffTime = sourceLastOffTime;
            if (sourceLastOffTime != null && System.currentTimeMillis() - Constants.MILLISECOND_3DAYS_AGO > sourceLastOffTime) {
                lastOffTime = System.currentTimeMillis() - Constants.MILLISECOND_3DAYS_AGO;
            }

            /**
             * 1、查询会话信息 查询用户所有会话，避免换设备会话不同步
             */
            ChatSessionUserQuery sessionUserQuery = new ChatSessionUserQuery();
            sessionUserQuery.setUserId(userId);
            sessionUserQuery.setOrderBy("last_receive_time desc");
            List<ChatSessionUser> chatSessionList = chatSessionUserMapper.selectList(sessionUserQuery);
            WsInitData wsInitData = new WsInitData();
            wsInitData.setChatSessionList(chatSessionList);

            /**
             * 2、查询聊天消息
             */
            //查询用户的联系人
            UserContactQuery contactQuery = new UserContactQuery();
            contactQuery.setContactType(UserContactTypeEnum.GROUP.getType());
            contactQuery.setUserId(userId);
            List<UserContact> groupContactList = userContactMapper.selectList(contactQuery);
            log.info("用户群组列表：{}", groupContactList);
            List<String> groupIdList = groupContactList.stream().map(item -> item.getContactId()).collect(Collectors.toList());
            //将自己也加进去
            groupIdList.add(userId);

            ChatMessageQuery messageQuery = new ChatMessageQuery();
            messageQuery.setContactIdList(groupIdList);
            messageQuery.setLastReceiveTime(lastOffTime);
            List<ChatMessage> chatMessageList = chatMessageMapper.selectList(messageQuery);
            wsInitData.setChatMessageList(chatMessageList);

            //输出chatMessageList的内容
            log.info("用户消息列表：{}", JsonUtils.convertObj2Json(chatMessageList));
            /**
             * 3、查询好友申请
             */
            UserContactApplyQuery applyQuery = new UserContactApplyQuery();
            applyQuery.setReceiveUserId(userId);
            applyQuery.setLastApplyTimestamp(sourceLastOffTime);
            applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
            Integer applyCount = userContactApplyMapper.selectCount(applyQuery);
            wsInitData.setApplyCount(applyCount);

            //发送消息
            MessageSendDto messageSendDto = new MessageSendDto();
            messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
            messageSendDto.setContactId(userId);
            messageSendDto.setExtendData(wsInitData);

            System.out.println("发送消息给用户：" + userId+ "内容为："+ messageSendDto);

            sendMsg(messageSendDto, userId);
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

    private static void sendMsg(MessageSendDto messageSendDto, String reciveId) {
        if (reciveId == null) {
            log.error("发送消息失败，用户不存在");
            return;
        }
        Channel sendChannel = USER_CONTEXT_MAP.get(reciveId);
        if (sendChannel == null) {
            log.error("发送消息失败，用户不存在, sendChannel");
            return;
        }
//        //相当于客户而言，联系人就是发送人，所以这里转换一下再发送,好友打招呼信息发送给自己需要特殊处理
//        if (MessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())) {
//            UserInfo userInfo = (UserInfo) messageSendDto.getExtendData();
//            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
//            messageSendDto.setContactId(userInfo.getUserId());
//            messageSendDto.setContactName(userInfo.getNickName());
//            messageSendDto.setExtendData(null);
//        } else {
//            messageSendDto.setContactId(messageSendDto.getSendUserId());
//            messageSendDto.setContactName(messageSendDto.getSendUserNickName());
//        }
//        log.info("发送消息给用户：" + reciveId+ "内容为："+ messageSendDto+ "走到最后一步");
//        sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
        // 逻辑修复：INIT 类型的消息不需要转换发送人
        if (MessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())) {
            UserInfo userInfo = (UserInfo) messageSendDto.getExtendData();
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
            messageSendDto.setContactId(userInfo.getUserId());
            messageSendDto.setContactName(userInfo.getNickName());
            messageSendDto.setExtendData(null);
        } else if (!MessageTypeEnum.INIT.getType().equals(messageSendDto.getMessageType())) {
            // 【关键修复】只有不是 INIT 消息，才做这个发送人的转换
            messageSendDto.setContactId(messageSendDto.getSendUserId());
            messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        }

        String jsonStr = JsonUtils.convertObj2Json(messageSendDto);
        log.info("准备发送消息给用户：{}，消息长度：{} byte", reciveId, jsonStr.getBytes().length);
        log.info("发送内容={}", jsonStr);

        // 【关键排查手段】添加 Listener，捕捉 Netty 底层发送异常！
        sendChannel.writeAndFlush(new TextWebSocketFrame(jsonStr)).addListener(future -> {
            if (!future.isSuccess()) {
                // 如果超长或者连接已断开，这里一定会打印出红色的报错！
                log.error("👿 消息发送到客户端底层失败！用户: {}, 原因:", reciveId, future.cause());
            } else {
                log.info("✅ 消息已成功推送到TCP缓冲区！用户: {}", reciveId);
            }
        });
    }
}
