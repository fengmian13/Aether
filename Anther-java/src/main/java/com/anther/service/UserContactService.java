package com.anther.service;


import com.anther.entity.dto.UserContactControllerDto;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.query.UserContactQuery;

import java.util.List;

public interface UserContactService {

    UserContactControllerDto searchContact(String userId, String contactId);

    List<UserContactControllerDto> findListByParam(UserContactQuery query);

    void delContact(String userId, String contactId, Integer status);

    /**
     * 添加联系人
     *
     * @param applyUserId
     * @param receiveUserId
     * @param contactType
     * @param applyInfo
     */
    void addContact(String applyUserId, String receiveUserId, Integer contactType, String applyInfo);

}
