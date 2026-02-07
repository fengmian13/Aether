package com.anther.Controller;

import java.util.List;

import com.anther.entity.query.UserGroupQuery;
import com.anther.entity.po.UserGroup;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.UserGroupService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  Controller
 */
@RestController("userGroupController")
@RequestMapping("/userGroup")
public class UserGroupController extends ABaseController{

	@Resource
	private UserGroupService userGroupService;
//	/**
//	 * 根据条件分页查询
//	 */
//	@RequestMapping("/loadDataList")
//	public ResponseVO loadDataList(UserGroupQuery query){
//		return getSuccessResponseVO(userGroupService.findListByPage(query));
//	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserGroup bean) {
		userGroupService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserGroup> listBean) {
		userGroupService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserGroup> listBean) {
		userGroupService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}
}