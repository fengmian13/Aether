package com.anther.service.impl;

import com.anther.entity.dto.ContactApplyDto;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.*;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.po.UserGroup;
import com.anther.entity.po.UserInfo;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.entity.query.UserGroupQuery;
import com.anther.entity.query.UserInfoQuery;
import com.anther.exception.BusinessException;
import com.anther.mappers.UserContactApplyMapper;
import com.anther.mappers.UserContactMapper;
import com.anther.mappers.UserGroupMapper;
import com.anther.mappers.UserInfoMapper;
import com.anther.redis.RedisComponent;
import com.anther.service.UserContactApplyService;
import com.anther.service.UserContactService;
import com.anther.utils.StringTools;
import com.anther.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private UserGroupMapper<UserGroup, UserGroupQuery> userGroupMapper;

    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private RedisComponent redisComponent;

    @Resource
    private UserContactService userContactService;



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
//        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
        messageSendDto.setMessageType(MessageTypeEnum.USER_CONTACT_APPLY.getType());
        messageSendDto.setContactId(userContactApply.getReceiveUserId());
        messageHandler.sendMessage(messageSendDto);
        return UserContactApplyStatusEnum.INIT.getStatus();
    }

    /**
     * 处理申请
     * @param userId: 处理人id
     * @param applyUserId: 申请人id
     * @param status： 处理状态
     * @param nickName： 处理人昵称
     */
    @Override
    public void dealWithApply(String userId, String applyUserId, Integer status, String nickName, String receiveUserId){
        // 获取申请的处理状态
        UserContactApplyStatusEnum applyStatus = UserContactApplyStatusEnum.getByStatus(status);
        if(applyStatus == null || applyStatus == UserContactApplyStatusEnum.INIT){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 判断申请是否存在
        UserContactApply apply = userContactApplyMapper.selectByApplyUserIdAndReceiveUserId(applyUserId, receiveUserId);
        System.out.println("G".equals(StringTools.getPreviousChar(apply.getReceiveUserId())));
        if("G".equals(StringTools.getPreviousChar(apply.getReceiveUserId())) ){
            apply.setContactType(UserContactTypeEnum.GROUP.getType());
        }else {
            apply.setContactType(UserContactTypeEnum.USER.getType());
        }
        if (apply == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserContactApply updateApply = new UserContactApply();
        updateApply.setStatus(applyStatus.getStatus());
        updateApply.setLastApplyTime(System.currentTimeMillis());
        userContactApplyMapper.updateByApplyId(updateApply, apply.getApplyId());

        //给用户添加联系人// 发消息，改到useContactService中
        if (UserContactApplyStatusEnum.PASS.equals(applyStatus)){
            userContactService.addContact(apply.getApplyUserId(), apply.getReceiveUserId(), apply.getContactType(), apply.getApplyInfo());
            return;
        }

        //拉黑
        if (UserContactApplyStatusEnum.BLACKLIST == applyStatus) {
            //拉黑 将接收人添加到申请人的联系人中，标记申请人被拉黑
            Date curDate = new Date();
            UserContact userContact = new UserContact();
            userContact.setUserId(apply.getApplyUserId());
            userContact.setContactId(apply.getContactId());
            userContact.setContactType(apply.getContactType());
            userContact.setCreateTime(curDate);
            userContact.setStatus(UserContactStatusEnum.BLACKLIST_BE.getStatus());
            userContact.setLastUpdateTime(curDate);
            userContactMapper.insertOrUpdate(userContact);
            return;
        }
    }

    @Override
    public List<ContactApplyDto> findListByParam(UserContactApplyQuery param) {
        List<UserContactApply> applyList = userContactApplyMapper.selectList(param);
        List<ContactApplyDto> result = new ArrayList<>();
        for (UserContactApply apply : applyList) {
            ContactApplyDto applyDto = new ContactApplyDto();
            applyDto.setApplyUserId(apply.getApplyUserId());
            applyDto.setReceiveUserId(apply.getReceiveUserId());
            UserInfo userInfo = userInfoMapper.selectByUserId(apply.getApplyUserId());
            if (userInfo != null) {
                applyDto.setApplyUserNickName(userInfo.getNickName()); // 假设getNickName()是获取昵称的方法
            } else {
                applyDto.setApplyUserNickName("未知用户"); // 用户不存在时的默认值
            }
            applyDto.setStatus(apply.getStatus());
            applyDto.setApplyTime(apply.getLastApplyTime().toString());

            result.add(applyDto);
        }


        return result;
    }

    @Override
    public List<ContactApplyDto> findGroupList(String userId) {
        // 查询拥有的群组
        UserGroupQuery query = new UserGroupQuery();
        query.setUserId(userId);
        query.setRoleId(GroupRoleEnum.MASTER.getType());
        List<UserGroup> groupIdList = userGroupMapper.selectList(query);
        List<ContactApplyDto> applyDtoList = new ArrayList<>();
        //如果没有数据，则返回空
        if (groupIdList.isEmpty()) {
            return applyDtoList;
        }
        System.out.println("groupIdList:"+applyDtoList);
        List<String> groupId = groupIdList.stream().map(UserGroup::getGroupId).collect(Collectors.toList());
        // 根据群组Id查询申请
        List<UserContactApply> queryDtoList = userContactApplyMapper.selectListByGroupId(groupId);

        for (UserContactApply apply : queryDtoList) {
            ContactApplyDto applyDto = new ContactApplyDto();
            applyDto.setApplyUserId(apply.getApplyUserId());
            applyDto.setReceiveUserId(apply.getReceiveUserId());
            applyDto.setApplyTime(apply.getLastApplyTime().toString());
            applyDto.setStatus(apply.getStatus());
            UserInfo userInfo = userInfoMapper.selectByUserId(apply.getApplyUserId());
            if (userInfo != null) {
                applyDto.setApplyUserNickName(userInfo.getNickName()); // 假设getNickName()是获取昵称的方法
            } else {
                applyDto.setApplyUserNickName("未知用户"); // 用户不存在时的默认值
            }
            applyDtoList.add(applyDto);
        }
        return applyDtoList;
    }
}
