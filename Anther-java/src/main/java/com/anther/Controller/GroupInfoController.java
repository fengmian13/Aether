package com.anther.Controller;

import java.util.List;

import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.po.GroupInfo;
import com.anther.entity.query.GroupInfoQuery;
import com.anther.entity.vo.ResponseVO;
import com.anther.exception.BusinessException;
import com.anther.service.GroupInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  Controller
 */
@RestController("groupInfoController")
@RequestMapping("/group")
public class GroupInfoController extends ABaseController{

	@Resource
	private GroupInfoService groupInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(GroupInfoQuery query){
		return getSuccessResponseVO(groupInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/saveGroup")
	public ResponseVO add(GroupInfo bean) {
		if (bean == null){
			throw new BusinessException("群组对象不能为空");
		}
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
		if (bean.getGroupId() == null){
			groupInfoService.add(bean, tokenUserInfoDto.getUserId());
		}else{
			groupInfoService.updateGroupInfoByGroupId(bean, bean.getGroupId());
		}
		return getSuccessResponseVO(null);
	}


	/**
	 * 根据GroupId删除
	 */
	@RequestMapping("/loadMyGroup")
	public ResponseVO loadMyGroup() {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
		return getSuccessResponseVO(groupInfoService.loadMyGroup(tokenUserInfoDto.getUserId()));
	}
	/**
	 * 根据GroupId查询
	 */
	@RequestMapping("/getGroupInfo")
	public ResponseVO loadGroup(String groupId) {
		return getSuccessResponseVO(groupInfoService.getGroupInfoByGroupId(groupId));
	}
}