package com.anther.service;

import java.util.List;

import com.anther.entity.query.GroupRoleInfoQuery;
import com.anther.entity.po.GroupRoleInfo;
import com.anther.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface GroupRoleInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<GroupRoleInfo> findListByParam(GroupRoleInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(GroupRoleInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<GroupRoleInfo> findListByPage(GroupRoleInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(GroupRoleInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<GroupRoleInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<GroupRoleInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(GroupRoleInfo bean,GroupRoleInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(GroupRoleInfoQuery param);

	/**
	 * 根据RoleId查询对象
	 */
	GroupRoleInfo getGroupRoleInfoByRoleId(String roleId);


	/**
	 * 根据RoleId修改
	 */
	Integer updateGroupRoleInfoByRoleId(GroupRoleInfo bean,String roleId);


	/**
	 * 根据RoleId删除
	 */
	Integer deleteGroupRoleInfoByRoleId(String roleId);

}