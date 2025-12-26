package com.anther.service.impl;

import com.anther.entity.dto.UserContactControllerDto;
import com.anther.entity.enums.ResponseCodeEnum;
import com.anther.entity.enums.UserContactApplyStatusEnum;
import com.anther.entity.enums.UserContactStatusEnum;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.po.UserInfo;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.entity.query.UserInfoQuery;
import com.anther.exception.BusinessException;
import com.anther.mappers.UserContactApplyMapper;
import com.anther.mappers.UserContactMapper;
import com.anther.mappers.UserInfoMapper;
import com.anther.service.UserContactService;
import com.sun.org.apache.bcel.internal.generic.NEW;
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

    @Override
    public UserContactControllerDto searchContact(String userId, String contactId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
        if (userInfo == null) {
            return null;
        }
        UserContactControllerDto resultDto = new UserContactControllerDto();
        resultDto.setNickName(userInfo.getNickName());
        resultDto.setSex(userInfo.getSex());
        // 是自己
        if (userId.equals(contactId)) {
            resultDto.setStatus(-UserContactApplyStatusEnum.PASS.getStatus());
            return resultDto;
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
    public List<UserContact> findListByParam(UserContactQuery param) {
        return this.userContactMapper.selectList(param);
    }

    @Override
    public void delContact(String userId, String contactId, Integer status) {
        if (!ArrayUtils.contains(new Integer[]{UserContactStatusEnum.DEL.getStatus(), UserContactStatusEnum.BLACKLIST.getStatus()}, status)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserContact userContact = new UserContact();
        userContact.setLastUpdateTime(new Date());
        userContact.setStatus(status);
        this.userContactMapper.updateByUserIdAndContactId(userContact, userId, contactId);
    }
}
