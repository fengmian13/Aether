package com.anther.service;


import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.po.UserInfo;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;

import java.util.List;

public interface UserContactApplyService {

    Integer saveContactApply(UserContactApply userContactApply);

    void dealWithApply(String userId,String applyUserId,Integer status, String nickName);

    List<UserContactApply> findListByParam(UserContactApplyQuery param);

}
