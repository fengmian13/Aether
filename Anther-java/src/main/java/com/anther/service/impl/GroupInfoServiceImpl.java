package com.anther.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.GroupInfoDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.dto.UserGroupContactDto;
import com.anther.entity.enums.*;
import com.anther.entity.po.UserGroup;
import com.anther.entity.query.UserGroupQuery;
import com.anther.exception.BusinessException;
import com.anther.mappers.UserGroupMapper;
import com.anther.redis.RedisComponent;
import com.anther.utils.IdTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.stereotype.Service;

import com.anther.entity.query.GroupInfoQuery;
import com.anther.entity.po.GroupInfo;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.GroupInfoMapper;
import com.anther.service.GroupInfoService;
import com.anther.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService {

	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;

	@Resource
	private UserGroupMapper<UserGroup, UserGroupQuery> userGroupMapper;
    @Autowired
    private ProjectInfoAutoConfiguration projectInfoAutoConfiguration;
    @Autowired
    private RedisComponent redisComponent;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<GroupInfo> findListByParam(GroupInfoQuery param) {
		return this.groupInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(GroupInfoQuery param) {
		return this.groupInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(param);
		PaginationResultVO<GroupInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(GroupInfo bean, String userId) {
		if (userId == null){
			throw new BusinessException("创建人不能为空");
		}

		String id = StringTools.getRandomNumber(Constants.LENGTH_12);
		id = IdTools.getGroupId(id);
		bean.setGroupId(id);
		String joinType = bean.getJoinType();
		if (GroupJoinTypeEnum.ANYONE_CAN_JOIN.getType().equals(joinType)){
			bean.setJoinType(GroupJoinTypeEnum.ANYONE_CAN_JOIN.getType());
		}else {
			bean.setJoinType(GroupJoinTypeEnum.USER_APPROVE.getType());
		}
		int insertCount = groupInfoMapper.insert(bean);
		if (insertCount != 1) {
			throw new RuntimeException("创建群失败");
		}
		//获取角色
		UserGroup userGroup = new UserGroup();
		userGroup.setUserId(userId);
		userGroup.setGroupId(id);
		userGroup.setRoleId(GroupRoleEnum.MASTER.getType());
		userGroupMapper.insert(userGroup);
		redisComponent.addUserContact(userId, id);
		return 1;
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(GroupInfo bean, GroupInfoQuery param) {
		StringTools.checkParam(param);
		return this.groupInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(GroupInfoQuery param) {
		StringTools.checkParam(param);
		return this.groupInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据GroupId获取对象
	 */
	@Override
	public GroupInfo getGroupInfoByGroupId(String groupId) {
		String role_id = GroupRoleEnum.MASTER.getType();
		GroupInfo bean = this.groupInfoMapper.selectByGroupIdAndRoleId(groupId,role_id);
		if (bean == null){
			return null;
		}
		if (GroupJoinTypeEnum.ANYONE_CAN_JOIN.getType().equals(bean.getJoinType())){
			bean.setJoinType(GroupJoinTypeEnum.ANYONE_CAN_JOIN.getType());
		}else {
			bean.setJoinType(GroupJoinTypeEnum.USER_APPROVE.getType());
		}


		return bean;
	}

	/**
	 * 根据GroupId修改
	 */
	@Override
	public Integer updateGroupInfoByGroupId(GroupInfo bean, String groupId) {
		return this.groupInfoMapper.updateByGroupId(bean, groupId);
	}

	/**
	 * 根据GroupId删除
	 */
	@Override
	public Integer deleteGroupInfoByGroupId(String groupId) {
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

	@Override
	public List<UserGroupContactDto> loadMyGroup(String userId) {
		return this.groupInfoMapper.loadMyGroup(userId,GroupRoleEnum.MASTER.getType());
	}

	@Override
	public Integer dissolutionGroup(String groupId, String userId) {
		UserGroupQuery userGroupQuery = new UserGroupQuery();
		userGroupQuery.setGroupId(groupId);
		userGroupQuery.setRoleId(GroupRoleEnum.MASTER.getType());
		UserGroup userGroup = this.userGroupMapper.selectMasterByGroupIdAndRoleId(userGroupQuery);
		System.out.println(userGroup);
		if (userGroup != null && userGroup.getUserId().equals(userId)){
			try {
				this.groupInfoMapper.dissolutionGroup(groupId);//TODO 后续改为假删除
				UserGroupQuery userGroupQuery1 = new UserGroupQuery();
				userGroupQuery1.setGroupId(groupId);
				this.userGroupMapper.deleteByParam(userGroupQuery1);
				return 1;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return 0;
	}

	@Override
	public Integer leaveGroup(String groupId, String userId) {
		UserGroupQuery userGroupQuery = new UserGroupQuery();
		userGroupQuery.setGroupId(groupId);
		userGroupQuery.setRoleId(GroupRoleEnum.MASTER.getType());
		UserGroup userGroup = this.userGroupMapper.selectMasterByGroupIdAndRoleId(userGroupQuery);
		if (userGroup != null && !userGroup.getUserId().equals(userId)){
			UserGroupQuery userGroupQuery1 = new UserGroupQuery();
			userGroupQuery1.setUserId(userId);
			userGroupQuery1.setGroupId(groupId);
			this.userGroupMapper.deleteByParam(userGroupQuery1);
			return 1;
		}
		return 0;
	}

	@Override
	public GroupInfoDto searchContact(String contactId) {
		GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
		if (groupInfo == null){
			throw new BusinessException("出现错误！");
		}
		GroupInfoDto groupInfoDto = new GroupInfoDto();
		groupInfoDto.setContactId(groupInfo.getGroupId());
		groupInfoDto.setContactType(ContactTypeEnum.GROUP.getType());
		groupInfoDto.setNickName(groupInfo.getGroupName());
		return groupInfoDto;
	}
}