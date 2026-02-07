package com.anther.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.anther.entity.dto.UserGroupContactDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.anther.entity.enums.PageSize;
import com.anther.entity.query.UserGroupQuery;
import com.anther.entity.po.UserGroup;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.UserGroupMapper;
import com.anther.service.UserGroupService;
import com.anther.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService {

	@Resource
	private UserGroupMapper<UserGroup, UserGroupQuery> userGroupMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserGroup> findListByParam(UserGroupQuery param) {
		return this.userGroupMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserGroupQuery param) {
		return this.userGroupMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserGroup> findListByPage(UserGroupQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserGroup> list = this.findListByParam(param);
		PaginationResultVO<UserGroup> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserGroup bean) {
		return this.userGroupMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserGroup> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userGroupMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserGroup> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userGroupMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserGroup bean, UserGroupQuery param) {
		StringTools.checkParam(param);
		return this.userGroupMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserGroupQuery param) {
		StringTools.checkParam(param);
		return this.userGroupMapper.deleteByParam(param);
	}

	@Override
	public List<UserGroupContactDto> findListByQuery(UserGroupQuery query) {
		return this.userGroupMapper.selectListByQuery( query);
	}
}