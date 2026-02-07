package com.anther.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.anther.entity.enums.PageSize;
import com.anther.entity.query.GroupRoleInfoQuery;
import com.anther.entity.po.GroupRoleInfo;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.GroupRoleInfoMapper;
import com.anther.service.GroupRoleInfoService;
import com.anther.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("groupRoleInfoService")
public class GroupRoleInfoServiceImpl implements GroupRoleInfoService {

	@Resource
	private GroupRoleInfoMapper<GroupRoleInfo, GroupRoleInfoQuery> groupRoleInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<GroupRoleInfo> findListByParam(GroupRoleInfoQuery param) {
		return this.groupRoleInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(GroupRoleInfoQuery param) {
		return this.groupRoleInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<GroupRoleInfo> findListByPage(GroupRoleInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<GroupRoleInfo> list = this.findListByParam(param);
		PaginationResultVO<GroupRoleInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(GroupRoleInfo bean) {
		return this.groupRoleInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<GroupRoleInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.groupRoleInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<GroupRoleInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.groupRoleInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(GroupRoleInfo bean, GroupRoleInfoQuery param) {
		StringTools.checkParam(param);
		return this.groupRoleInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(GroupRoleInfoQuery param) {
		StringTools.checkParam(param);
		return this.groupRoleInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据RoleId获取对象
	 */
	@Override
	public GroupRoleInfo getGroupRoleInfoByRoleId(String roleId) {
		return this.groupRoleInfoMapper.selectByRoleId(roleId);
	}

	/**
	 * 根据RoleId修改
	 */
	@Override
	public Integer updateGroupRoleInfoByRoleId(GroupRoleInfo bean, String roleId) {
		return this.groupRoleInfoMapper.updateByRoleId(bean, roleId);
	}

	/**
	 * 根据RoleId删除
	 */
	@Override
	public Integer deleteGroupRoleInfoByRoleId(String roleId) {
		return this.groupRoleInfoMapper.deleteByRoleId(roleId);
	}
}