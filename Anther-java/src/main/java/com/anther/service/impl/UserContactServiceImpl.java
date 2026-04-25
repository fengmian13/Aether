package com.anther.service.impl;

import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.SysSettingDto;
import com.anther.entity.dto.UserContactControllerDto;
import com.anther.entity.enums.*;
import com.anther.entity.po.*;
import com.anther.entity.query.*;
import com.anther.exception.BusinessException;
import com.anther.mappers.*;
import com.anther.redis.RedisComponent;
import com.anther.service.UserContactService;
import com.anther.service.UserGroupService;
import com.anther.utils.CopyTools;
import com.anther.utils.StringTools;
import com.anther.websocket.ChannelContextUtils;
import com.anther.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: 联系人相关逻辑
 * @date 2025/12/26 11:33
 */
@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

    @Resource
    private UserGroupService userGroupService;

    @Override
    public UserContactControllerDto searchContact(String userId, String contactId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
        if (userInfo == null) {
            return null;
        }
        UserContactControllerDto resultDto = new UserContactControllerDto();
        resultDto.setNickName(userInfo.getNickName());
        resultDto.setSex(userInfo.getSex());
        resultDto.setContactId(userInfo.getUserId());
        // 是自己
        if (userId.equals(contactId)) {
            resultDto.setStatus(-UserContactApplyStatusEnum.PASS.getStatus());
            return resultDto;
        }

        // 判断是用户还是群组
        System.out.println("userInfo.getContactType"+userInfo.getContactType());
        if (ContactTypeEnum.USER.getType().equals(userInfo.getContactType())){
            resultDto.setContactType(ContactTypeEnum.USER.getType());
        } else if (ContactTypeEnum.GROUP.getType().equals(userInfo.getContactType())) {
            resultDto.setContactType(ContactTypeEnum.GROUP.getType());
        }else {
            resultDto.setContactType("NULL");
        }
        // 查询申请
        UserContactApply userContactApply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(userId, contactId);

        // 查询状态，是否是好友、拉黑、待处理
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(contactId, userId);
        // 存在拉黑
        if (userContactApply != null && UserContactApplyStatusEnum.BLACKLIST.getStatus().equals(userContactApply.getStatus())||
        userContact != null && UserContactApplyStatusEnum.BLACKLIST.getStatus().equals(userContact.getStatus())){
            resultDto.setStatus(UserContactApplyStatusEnum.BLACKLIST.getStatus());
            return resultDto;
        }
        // 待处理
        if (userContactApply != null && UserContactApplyStatusEnum.INIT.getStatus().equals(userContactApply.getStatus())){
            resultDto.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
            return resultDto;
        }
        // 已是好友
        UserContact myUserContact = userContactMapper.selectByUserIdAndContactId(userId, contactId);

        if (userContact != null && UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus()) &&
        myUserContact != null && UserContactStatusEnum.FRIEND.getStatus().equals(myUserContact.getStatus())){
            resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            return resultDto;
        }
        return resultDto;
    }

    @Override
    public List<UserContactControllerDto> findListByParam(UserContactQuery param) {
        List<UserContactControllerDto> resultList = new ArrayList<>();

        List<UserContact> userContactList = this.userContactMapper.selectList(param);
        for (UserContact userContact : userContactList) {
            //判断contact_id的首字母是否为U
            if (userContact.getContactId().startsWith("U")) {
                UserContactControllerDto resultDto = new UserContactControllerDto();
                resultDto.setContactId(userContact.getContactId());
                resultDto.setStatus(userContact.getStatus());

                UserInfo userInfo = userInfoMapper.selectByUserId(userContact.getContactId());
                if (userInfo != null) {
                    resultDto.setNickName(userInfo.getNickName());
                    resultDto.setSex(userInfo.getSex());
                }
//            else {
//                resultDto.setNickName("");
//                resultDto.setSex(null);
//            }

                resultList.add(resultDto);
            }
        }
        return resultList;
    }

    @Autowired
    private RedisComponent redisComponent;

    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;

    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private ChannelContextUtils channelContextUtils;

    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;

    @Resource
    private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;

    // 待完善，redis
    @Override
    public void delContact(String userId, String contactId, Integer status) {
        if (!ArrayUtils.contains(new Integer[]{UserContactStatusEnum.DEL.getStatus(), UserContactStatusEnum.BLACKLIST.getStatus()}, status)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserContact userContact = new UserContact();
        userContact.setLastUpdateTime(new Date());
        userContact.setStatus(status);
        this.userContactMapper.updateByUserIdAndContactId(userContact, userId, contactId);

        //TODO 待完善，redis
    }

    /**
     * 添加联系人
     * @param applyUserId 申请人
     * @param receiveUserId 接收人
     * @param contactType 联系人类型
     * @param applyInfo 申请信息
     */
    @Override
    public void addContact(String applyUserId, String receiveUserId, Integer contactType, String applyInfo) {
        String contactId = receiveUserId;
        //群人上限判断
        if (UserContactTypeEnum.GROUP.getType().equals(contactType)) {
            UserContactQuery contactQuery = new UserContactQuery();
            contactQuery.setContactId(contactId);
            contactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer count = userContactMapper.selectCount(contactQuery);
            SysSettingDto sysSettingDto = redisComponent.getSysSetting();
            if (count >= sysSettingDto.getMaxGroupMemberCount()) {
                throw new BusinessException("成员已满，无法加入");
            }
        }
        Date curDate = new Date();
        //同意 双方添加为好友
        List<UserContact> contactList = new ArrayList<>();
        //申请人添加对方
        UserContact userContact = new UserContact();
        userContact.setUserId(applyUserId);
        userContact.setContactId(contactId);
        userContact.setContactType(contactType);
        userContact.setCreateTime(curDate);
        userContact.setLastUpdateTime(curDate);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
        contactList.add(userContact);
        //如果是申请好友 接收人添加申请人  群组不用添加对方为好友
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            userContact = new UserContact();
            userContact.setUserId(receiveUserId);
            userContact.setContactId(applyUserId);
            userContact.setContactType(contactType);
            userContact.setCreateTime(curDate);
            userContact.setLastUpdateTime(curDate);
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            contactList.add(userContact);
        }
        //批量加入
        userContactMapper.insertOrUpdateBatch(contactList);

        //如果是好友申请,接收人也添加申请人为联系人
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            redisComponent.addUserContact(receiveUserId, applyUserId);
        }
        //审核通过，将申请人的联系人添加上 我 或 群组
        redisComponent.addUserContact(applyUserId, contactId);


        //创建会话信息
        String sessionId = null;
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            sessionId = StringTools.getChatSessionId4User(new String[]{applyUserId, contactId});
        } else {
            sessionId = StringTools.getChatSessionId4Group(contactId);
        }

        //会话参与人
        List<ChatSessionUser> chatSessionUserList = new ArrayList<>();
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            //创建会话
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastReceiveTime(curDate);
            chatSession.setLastMessage(applyInfo);
            this.chatSessionMapper.insertOrUpdate(chatSession);

            //申请人session
            ChatSessionUser applySessionUser = new ChatSessionUser();
            applySessionUser.setUserId(applyUserId);
            applySessionUser.setContactId(contactId);
            applySessionUser.setSessionId(sessionId);
            applySessionUser.setLastReceiveTime(curDate);
            applySessionUser.setLastMessage(applyInfo);
            //查询接收人信息
            UserInfo contactUser = this.userInfoMapper.selectByUserId(contactId);
            applySessionUser.setContactName(contactUser.getNickName());
            chatSessionUserList.add(applySessionUser);

            //接受人session
            ChatSessionUser contactSessionUser = new ChatSessionUser();
            contactSessionUser.setUserId(contactId);
            contactSessionUser.setContactId(applyUserId);
            contactSessionUser.setSessionId(sessionId);
            contactSessionUser.setLastReceiveTime(curDate);
            contactSessionUser.setLastMessage(applyInfo);
            //查询申请人信息
            UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(applyUserId);
            contactSessionUser.setContactName(applyUserInfo.getNickName());
            chatSessionUserList.add(contactSessionUser);
            this.chatSessionUserMapper.insertOrUpdateBatch(chatSessionUserList);

            //记录消息消息表
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
            chatMessage.setMessageContent(applyInfo);
            chatMessage.setSendUserId(applyUserId);
            chatMessage.setSendUserNickName(applyUserInfo.getNickName());
            chatMessage.setSendTime(curDate.getTime());
            chatMessage.setSessionType(UserContactTypeEnum.USER.getType());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
            chatMessageMapper.insert(chatMessage);

            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            /**
             * 发送给接受好友申请的人
             */
            messageHandler.sendMessage(messageSendDto);

            /**
             * 发送给申请人 发送人就是接收人，联系人就是申请人
             */
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND_SELF.getType());
            messageSendDto.setSendUserId(applyUserId);
            messageSendDto.setExtendData(contactUser);
            messageHandler.sendMessage(messageSendDto);

        } else {
            //加入群组
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(applyUserId);
            chatSessionUser.setContactId(contactId);
            GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
            chatSessionUser.setContactName(groupInfo.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            this.chatSessionUserMapper.insertOrUpdate(chatSessionUser);

            //将群组加入到用户的联系人列表
            redisComponent.addUserContact(applyUserId, groupInfo.getGroupId());

            channelContextUtils.addUser2Group(applyUserId, groupInfo.getGroupId());


            UserInfo applyUserInfo = this.userInfoMapper.selectByUserId(applyUserId);

            String sendMessage = String.format(MessageTypeEnum.ADD_GROUP.getInitMessage(), applyUserInfo.getNickName());

            //增加session信息
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastReceiveTime(curDate);
            chatSession.setLastMessage(sendMessage);
            this.chatSessionMapper.insertOrUpdate(chatSession);

            //增加聊天消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.ADD_GROUP.getType());
            chatMessage.setMessageContent(sendMessage);
            chatMessage.setSendUserId(null);
            chatMessage.setSendUserNickName(null);
            chatMessage.setSendTime(curDate.getTime());
            chatMessage.setContactId(contactId);
            chatMessage.setSessionType(UserContactTypeEnum.GROUP.getType());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
            chatMessageMapper.insert(chatMessage);

            //发送群消息
            MessageSendDto messageSend = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSend.setContactId(groupInfo.getGroupId());
            //获取群人数量
            UserContactQuery userContactQuery = new UserContactQuery();
            userContactQuery.setContactId(contactId);
            userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            Integer memberCount = this.userContactMapper.selectCount(userContactQuery);
            messageSend.setMemberCount(memberCount);
            messageSend.setSendUserNickName(groupInfo.getGroupName());
            messageHandler.sendMessage(messageSend);

            //添加user_group群成员
            //判断是否拉黑
            UserGroup userGroup = new UserGroup();
            userGroup.setUserId(applyUserId);
            userGroup.setGroupId(contactId);
            userGroup.setRoleId(GroupRoleEnum.MASTER.getType());
            userGroupService.add(userGroup);
        }
    }

    @Override
    public UserContact getUserContactByUserIdAndContactId(String userId, String contactId) {
        return this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
    }
}
