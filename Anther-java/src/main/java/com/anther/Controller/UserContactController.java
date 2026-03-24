package com.anther.Controller;

import com.anther.entity.dto.*;
import com.anther.entity.enums.ContactTypeEnum;
import com.anther.entity.enums.GroupRoleEnum;
import com.anther.entity.enums.IdEnum;
import com.anther.entity.enums.UserContactStatusEnum;
import com.anther.entity.po.UserContact;
import com.anther.entity.po.UserContactApply;
import com.anther.entity.po.UserInfo;
import com.anther.entity.vo.UserInfoVO;
import com.anther.entity.query.UserContactApplyQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.entity.query.UserGroupQuery;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.*;
import com.anther.utils.CopyTools;
import com.anther.utils.IdTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: 联系人相关接口
 * @date 2025/12/25 22:40
 */
@RestController
@RequestMapping("/userContact")
public class UserContactController extends ABaseController{

    @Resource
    private UserContactService userContactService;

    @Resource
    private UserContactApplyService userContactApplyService;

    @Resource
    private UserGroupService userGroupService;

    @Resource
    private GroupInfoService groupInfoService;


    @Resource
    private UserInfoService userInfoService;

    /**
     * @param contactId
     * @return com.anther.entity.vo.ResponseVO
     * @description: 搜索联系人
     * @author 吴磊
     * @date 2025/12/26 15：03
     */
    @RequestMapping("/search")
    public ResponseVO search(@NotEmpty String contactId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // TODO: 修改查询
        //  1、userID和GroupID，分别加上前缀U和G
        //  2、通过获取contactId中的第一个字母判断是搜索用户还是群组
        //  3、分开调用不同的方式
        //  用户增加地区，以及经纬度
        if (IdEnum.USER_ID.equals(IdTools.getUserIdOrGroupIdById(contactId))){
            UserContactControllerDto resultDto = userContactService.searchContact(tokenUserInfoDto.getUserId(), contactId);
            return getSuccessResponseVO(resultDto);
        }
        if (IdEnum.GROUP_ID.equals(IdTools.getUserIdOrGroupIdById(contactId))){
            GroupInfoDto resultDto = groupInfoService.searchContact(contactId);
            return getSuccessResponseVO(resultDto);
        }
        // TODO: 待补充群组搜索,
        //  群组群组全部改为使用user_info表
        //  修改群组搜索，确定群组申请的同意方案
        return getSuccessResponseVO("无效的ID");
    }

    /**
     * @param receiveUserId
     * @return com.anther.entity.vo.ResponseVO
     * @description: 添加联系人
     * @author 吴磊
     * @date 2025/12/26 15：03
     */
    @RequestMapping("/contactApply")
    public ResponseVO contactApply(@NotEmpty String receiveUserId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        UserContactApply userContactApply = new UserContactApply();
        userContactApply.setApplyUserId(tokenUserInfoDto.getUserId());
        userContactApply.setReceiveUserId(receiveUserId);
        Integer status = userContactApplyService.saveContactApply(userContactApply);
        return getSuccessResponseVO(status);
        // TODO:需要增加申请理由，给apply增加一个字段，来描述
    }

    /**
     * @param
     * @return com.anther.entity.vo.ResponseVO
     * @description: 处理申请
     * @author 吴磊
     * @date 2025/12/26 15：03
     */
    @RequestMapping("/dealWithApply")
    public ResponseVO dealWithApply(@NotEmpty String applyUserId,@NotEmpty String receiveUserId , @NotEmpty Integer status){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        userContactApplyService.dealWithApply(tokenUserInfoDto.getUserId(), applyUserId, status, tokenUserInfoDto.getNickName(), receiveUserId);
        return getSuccessResponseVO(null);
    }

    /**
     * @param
     * @return com.anther.entity.vo.ResponseVO
     * @description: 加载联系人用户
     * @author 吴磊
     * @date 2025/12/26 15：03
     */
    @RequestMapping("/loadContact")
    public ResponseVO loadContact(@NotEmpty String contactType){
        if (contactType == null){
            return getSuccessResponseVO("无效的参数");
        }
        try {
            ContactTypeEnum type = ContactTypeEnum.valueOf(contactType.toUpperCase());
            if (ContactTypeEnum.GROUP.equals(type)){
                //查找用户群组
                TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
                UserGroupQuery userGroup = new UserGroupQuery();
                userGroup.setUserId(tokenUserInfoDto.getUserId());
//                userGroup.setRoleId(GroupRoleEnum.MASTER.getType());//改为前端判断03（群主）
                List<UserGroupContactDto> userGroupList = userGroupService.findListByQuery(userGroup);

                // 查找群组

                return getSuccessResponseVO(userGroupList);
            } else if (ContactTypeEnum.USER.equals(type)) {
                TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
                UserContactQuery contactQuery = new UserContactQuery();
                contactQuery.setUserId(tokenUserInfoDto.getUserId());
                contactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
                contactQuery.setOrderBy("last_update_time desc");
                contactQuery.setQueryUserInfo(true);
                List<UserContactControllerDto> userContactList = userContactService.findListByParam(contactQuery);
                return getSuccessResponseVO(userContactList);
            }
        } catch (IllegalArgumentException e) {
            return getSuccessResponseVO("无效的参数");
        }
        return getSuccessResponseVO("无效的参数");
    }

    /**
     * @param
     * @return com.anther.entity.vo.ResponseVO
     * @description: 加载联系人申请
     * @author 吴磊
     * @date 2025/12/26 15：03
     */

    @RequestMapping("/loadContactApply")
    public ResponseVO loadContactApply() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        System.out.println(tokenUserInfoDto.getUserId());
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setReceiveUserId(tokenUserInfoDto.getUserId());
        applyQuery.setQueryUserInfo(true);
        applyQuery.setOrderBy("last_apply_time desc");
        List<ContactApplyDto> applyList = this.userContactApplyService.findListByParam(applyQuery);
        // 补充群组申请
        List<ContactApplyDto> applyGroupList =this.userContactApplyService.findGroupList(tokenUserInfoDto.getUserId());
        applyList.addAll(applyGroupList);
        return getSuccessResponseVO(applyList);
    }


    /**
     * @param
     * @return com.anther.entity.vo.ResponseVO
     * @description: 删除联系人
     * @author 吴磊
     * @date 2025/12/26 15：03
     */
    @RequestMapping("/delContact")
    public ResponseVO delContact(@NotEmpty String contactId, @NotNull Integer status) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        userContactService.delContact(tokenUserInfoDto.getUserId(), contactId, status);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 拉黑
     * @author 吴磊
     * @date 2026/2/5 21:18
     * @version 1.0
     */
    @RequestMapping("/addContact2BlackList")
    public ResponseVO contactBlack(@NotEmpty String contactId, @NotNull Integer status) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        userContactService.delContact(tokenUserInfoDto.getUserId(), contactId, status);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getContactInfo")
    public ResponseVO getContactInfo(@NotEmpty String contactId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        // 查找联系人信息
        UserInfo userInfo = userInfoService.getUserInfoByUserId(contactId);
        UserInfoVO userInfoVo = CopyTools.copy(userInfo, UserInfoVO.class);
        userInfoVo.setContactStatus(UserContactStatusEnum.NOT_FRIEND.getStatus());
        //判断是否是联系人
        UserContact userContact = userContactService.getUserContactByUserIdAndContactId(tokenUserInfoDto.getUserId(), contactId);
        if (userContact != null) {
            userInfoVo.setContactStatus(userContact.getStatus());
        }
        return getSuccessResponseVO(userInfoVo);
    }
}
