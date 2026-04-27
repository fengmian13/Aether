package com.anther.service;


import com.anther.entity.dto.ContactApplyDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.po.UserInfo;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.entity.vo.PaginationResultVO;

import java.util.List;

public interface UserContactApplyService {

//    Integer saveContactApply(UserContactApply userContactApply);
    Integer saveContactApply(TokenUserInfoDto tokenUserInfoDto, String contactId, String contactType, String applyInfo);

    void dealWithApply(String userId,String applyUserId,Integer status, String nickName, String receiveUserId);

    List<UserContactApply> findListByParam(UserContactApplyQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(UserContactApplyQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<UserContactApply> findListByPage(UserContactApplyQuery param);

    List<ContactApplyDto> findGroupList(String userId);

}
