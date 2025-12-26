package com.anther.service.impl;

import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.*;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.exception.BusinessException;
import com.anther.mappers.UserContactApplyMapper;
import com.anther.mappers.UserContactMapper;
import com.anther.service.UserContactApplyService;
import com.anther.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/26 15:28
 */
@Service("userContactApplyService")
public class UserContactApplyServiceImpl implements UserContactApplyService {

    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

    @Autowired
    private MessageHandler messageHandler;


    @Override
    public Integer saveContactApply(UserContactApply userContactApply) {
        // 判断是否被拉黑
        UserContact userContact = userContactMapper.selectByUserIdAndContactId(userContactApply.getReceiveUserId(), userContactApply.getApplyUserId());
        if (userContact != null && UserContactStatusEnum.BLACKLIST.getStatus().equals(userContact.getStatus())){
            throw new BusinessException("对方已将你拉黑");
        }
        // 已经是好友
        if (userContact != null && UserContactStatusEnum.FRIEND.getStatus().equals(userContact.getStatus())){
            // 更新自己的好友信息
            UserContact myUserContact = new UserContact();
            myUserContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            myUserContact.setLastUpdateTime(new Date());
            userContactMapper.updateByUserIdAndContactId(myUserContact, userContactApply.getApplyUserId(), userContactApply.getReceiveUserId());
            return UserContactStatusEnum.FRIEND.getStatus();
        }
        // 判断是否已存在申请,没有新建，有跟新时间
        UserContactApply apply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(userContactApply.getApplyUserId(), userContactApply.getReceiveUserId());
        if (apply == null){
            userContactApply.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
            userContactApply.setLastApplyTime(System.currentTimeMillis());
            userContactApplyMapper.insert(userContactApply);
        } else {
            UserContactApply updateInfo = new UserContactApply();
            updateInfo.setStatus(UserContactApplyStatusEnum.INIT.getStatus());
            updateInfo.setLastApplyTime(System.currentTimeMillis());
            this.userContactApplyMapper.updateByApplyId(updateInfo, apply.getApplyId());
        }

        // 发送消息
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
        messageSendDto.setMessageType(MessageTypeEnum.USER_CONTACT_APPLY.getType());
        messageSendDto.setReceiveUserId(userContactApply.getReceiveUserId());
        messageHandler.sendMessage(messageSendDto);
        return UserContactApplyStatusEnum.INIT.getStatus();
    }

    @Override
    public void dealWithApply(String userId, String applyUserId, Integer status, String nickName){
        // 获取申请的处理状态
        UserContactApplyStatusEnum applyStatus = UserContactApplyStatusEnum.getByStatus(status);
        if(applyStatus == null || applyStatus == UserContactApplyStatusEnum.INIT){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 判断申请是否存在
        UserContactApply apply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(applyUserId, userId);
        if (apply == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (UserContactApplyStatusEnum.PASS.equals(applyStatus)){
            Date now = new Date();
            UserContact userContact = new UserContact();
            userContact.setUserId(applyUserId);
            userContact.setContactId(userId);
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            userContact.setLastUpdateTime(now);
            userContactMapper.insertOrUpdate(userContact);
            userContact.setUserId(userId);
            userContact.setContactId(applyUserId);
            userContactMapper.insertOrUpdate(userContact);
        }

        UserContactApply updateApply = new UserContactApply();
        updateApply.setStatus(applyStatus.getStatus());
        userContactApplyMapper.updateByApplyId(updateApply, apply.getApplyId());

        // 发消息
        MessageSendDto sendDto = new MessageSendDto();
        sendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
        sendDto.setMessageType(MessageTypeEnum.USER_CONTACT_DEAL_WITH.getType());
        sendDto.setReceiveUserId(applyUserId);
        sendDto.setSendUserId(userId);
        sendDto.setSendUserNickName(nickName);
        sendDto.setMessageContent(status);
        messageHandler.sendMessage(sendDto);
    }

    @Override
    public List<UserContactApply> findListByParam(UserContactApplyQuery param) {
        return userContactApplyMapper.selectList(param);
    }


}
