package com.anther.Controller;

import java.util.List;

import com.anther.entity.query.GroupRoleInfoQuery;
import com.anther.entity.po.GroupRoleInfo;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.GroupRoleInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  Controller
 */
@RestController("groupRoleInfoController")
@RequestMapping("/groupRoleInfo")
public class GroupRoleInfoController extends ABaseController{

	@Resource
	private GroupRoleInfoService groupRoleInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(GroupRoleInfoQuery query){
		return getSuccessResponseVO(groupRoleInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(GroupRoleInfo bean) {
		groupRoleInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<GroupRoleInfo> listBean) {
		groupRoleInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<GroupRoleInfo> listBean) {
		groupRoleInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据RoleId查询对象
	 */
	@RequestMapping("/getGroupRoleInfoByRoleId")
	public ResponseVO getGroupRoleInfoByRoleId(String roleId) {
		return getSuccessResponseVO(groupRoleInfoService.getGroupRoleInfoByRoleId(roleId));
	}

	/**
	 * 根据RoleId修改对象
	 */
	@RequestMapping("/updateGroupRoleInfoByRoleId")
	public ResponseVO updateGroupRoleInfoByRoleId(GroupRoleInfo bean,String roleId) {
		groupRoleInfoService.updateGroupRoleInfoByRoleId(bean,roleId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据RoleId删除
	 */
	@RequestMapping("/deleteGroupRoleInfoByRoleId")
	public ResponseVO deleteGroupRoleInfoByRoleId(String roleId) {
		groupRoleInfoService.deleteGroupRoleInfoByRoleId(roleId);
		return getSuccessResponseVO(null);
	}
}