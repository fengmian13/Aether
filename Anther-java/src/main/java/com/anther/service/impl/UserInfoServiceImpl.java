package com.anther.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.anther.entity.config.AppConfig;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.UserStatusEnum;
import com.anther.entity.vo.UserInfoVO;
import com.anther.exception.BusinessException;
import com.anther.redis.RedisComponent;
import com.anther.utils.CopyTools;
import com.anther.utils.FFmpegUtils;
import org.springframework.stereotype.Service;

import com.anther.entity.enums.PageSize;
import com.anther.entity.query.UserInfoQuery;
import com.anther.entity.po.UserInfo;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.UserInfoMapper;
import com.anther.service.UserInfoService;
import com.anther.utils.StringTools;
import com.anther.entity.constants.Constants;
import org.springframework.web.multipart.MultipartFile;


/**
 * 用户信息 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private AppConfig appConfig;
	@Resource
	private RedisComponent redisComponent;

	@Resource
	private FFmpegUtils fFmpegUtils;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 注册
	 */
	@Override
	public void register(String email, String password, String nickName, Integer sex) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (userInfo != null) {
			throw new RuntimeException("邮箱已存在");
		}
		Date nowDate = new Date();
		String userId = StringTools.getRandomNumber(Constants.LENGTH_12);

		userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setCreateTime(nowDate);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		userInfo.setLastOffTime(nowDate.getTime());
		userInfo.setNickName(nickName);
		userInfo.setSex(sex);
		this.userInfoMapper.insert(userInfo);
	}

	/**
	 * 登录
	 */
	@Override
	public UserInfoVO login(String email, String password) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null == userInfo ||  !userInfo.getPassword().equals(password)){
			throw new BusinessException("用户名或密码错误");
		}
		if (userInfo.getStatus().equals(UserStatusEnum.DISABLE.getStatus())){
			throw new BusinessException("用户被禁用");
		}
		if (userInfo.getLastLoginTime() != null && userInfo.getLastOffTime() <= userInfo.getLastLoginTime()){
			throw new BusinessException("用户已在别处的登陆");
		}
		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
		String token = StringTools.encodeByMD5(tokenUserInfoDto.getUserId()+StringTools.getRandomString(Constants.LENGTH_20));
		tokenUserInfoDto.setToken(token);
		tokenUserInfoDto.setMyMeetingNo(userInfo.getMeetingNo());
		tokenUserInfoDto.setAdmin(appConfig.getAdminEmails().contains( email));

		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);

		UserInfoVO userInfoVO = CopyTools.copy(tokenUserInfoDto, UserInfoVO.class);
		userInfoVO.setToken(token);
		userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());

		return userInfoVO;
	}

	/**
	 * 登出
	 */
	@Override
	public void logout(String email) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		redisComponent.cleanUserTokenByUserId(userInfo.getUserId());
//		redisComponent.removeUserHeartBeat(userInfo.getUserId());
		userInfo.setLastOffTime(new Date().getTime());
		this.userInfoMapper.updateByEmail(userInfo, email);
	}

	@Override
	public void updateUserInfo(MultipartFile file, UserInfo userInfo) throws IOException {
		if (file != null) {
			String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_AVATAR_NAME;//  头像保存目录
			File folderFile = new File(folder); //  创建保存文件夹
			if (!folderFile.exists()) { //  判断文件夹是否存在
				folderFile.mkdirs();
			}
			//  保存文件
			String realFileName = userInfo.getUserId() + Constants.IMAGE_SUFFIX;// 文件名
			String filePath = folderFile + realFileName; // 文件保存路径
			File tempFile = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP + StringTools.getRandomString(Constants.LENGTH_30));// 临时文件保存路径
			file.transferTo(tempFile);// 保存文件
			fFmpegUtils.createImageThumbnail(tempFile, filePath);// 创建缩略图
		}
		this.userInfoMapper.updateByUserId(userInfo, userInfo.getUserId());
		TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDtoByUserId(userInfo.getUserId());
		tokenUserInfoDto.setNickName(userInfo.getNickName());
		tokenUserInfoDto.setSex(userInfo.getSex());
		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);
	}

	@Override
	public void updatePassword(String userId, String oldPassword, String newPassword){
		UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
		if (null == userInfo){
			throw new BusinessException("用户不存在");
		}
		if (!userInfo.getPassword().equals(StringTools.encodeByMD5(oldPassword))){
			throw new BusinessException("旧密码错误");
		}
		UserInfo updateInfo = new UserInfo();
		updateInfo.setPassword(StringTools.encodeByMD5(newPassword));
		this.userInfoMapper.updateByUserId(updateInfo, userId);
		redisComponent.cleanUserTokenByUserId(userId);// 修改密码以后登出，清除token
	}
}