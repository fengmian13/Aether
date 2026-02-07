package com.anther.service;

import java.util.List;

import com.anther.entity.dto.UserGroupContactDto;
import com.anther.entity.query.UserGroupQuery;
import com.anther.entity.po.UserGroup;
import com.anther.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface UserGroupService {

	/**
	 * 根据条件查询列表
	 */
	List<UserGroup> findListByParam(UserGroupQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserGroupQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserGroup> findListByPage(UserGroupQuery param);

	/**
	 * 新增
	 */
	Integer add(UserGroup bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserGroup> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserGroup> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserGroup bean,UserGroupQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserGroupQuery param);

	List<UserGroupContactDto> findListByQuery(UserGroupQuery query);
}