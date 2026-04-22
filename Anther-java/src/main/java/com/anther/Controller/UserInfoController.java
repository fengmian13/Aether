package com.anther.Controller;

import java.io.IOException;
import java.util.List;

import com.anther.annotation.GlobalInterceptor;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.query.UserInfoQuery;
import com.anther.entity.po.UserInfo;
import com.anther.entity.vo.ResponseVO;
import com.anther.redis.RedisComponent;
import com.anther.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息 Controller
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController{

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private UserInfoService userInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query){
		return getSuccessResponseVO(userInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserInfo bean) {
		userInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId查询对象
	 */
	@RequestMapping("/getUserInfoByUserId")
	public ResponseVO getUserInfoByUserId(String userId) {
		return getSuccessResponseVO(userInfoService.getUserInfoByUserId(userId));
	}

	 /**
	  * @description: 获取用户信息
	  * @author 吴磊
	  * @date 2026/2/9 11:09
	  * @version 1.0
	  */
	 @RequestMapping("/getUserInfo")
	 public ResponseVO getUserInfo() {
		 TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
		 return getSuccessResponseVO(userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId()));
	 }

	/**
	 * 根据UserId修改对象
	 */
	@RequestMapping("/updateUserInfoByUserId")
	public ResponseVO updateUserInfoByUserId(UserInfo bean,String userId) {
		userInfoService.updateUserInfoByUserId(bean,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId删除
	 */
	@RequestMapping("/deleteUserInfoByUserId")
	public ResponseVO deleteUserInfoByUserId(String userId) {
		userInfoService.deleteUserInfoByUserId(userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email查询对象
	 */
	@RequestMapping("/getUserInfoByEmail")
	public ResponseVO getUserInfoByEmail(String email) {
		return getSuccessResponseVO(userInfoService.getUserInfoByEmail(email));
	}

	/**
	 * 根据Email修改对象
	 */
	@RequestMapping("/updateUserInfoByEmail")
	public ResponseVO updateUserInfoByEmail(UserInfo bean,String email) {
		userInfoService.updateUserInfoByEmail(bean,email);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据Email删除
	 */
	@RequestMapping("/deleteUserInfoByEmail")
	public ResponseVO deleteUserInfoByEmail(String email) {
		userInfoService.deleteUserInfoByEmail(email);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/saveUserInfo")
	@GlobalInterceptor
	public ResponseVO saveUserInfo( UserInfo userInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
		userInfo.setUserId(tokenUserInfoDto.getUserId());
		userInfo.setPassword(null);
		userInfo.setStatus(null);
		userInfo.setCreateTime(null);
		userInfo.setLastLoginTime(null);
		this.userInfoService.updateUserInfo(avatarFile, userInfo, avatarCover);
		if (!tokenUserInfoDto.getNickName().equals(userInfo.getNickName())) {
			tokenUserInfoDto.setNickName(userInfo.getNickName());
			redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
		}
		return getSuccessResponseVO(null);
	}
}