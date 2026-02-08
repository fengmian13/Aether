package com.anther.mappers;

import com.anther.entity.dto.UserGroupContactDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  数据库操作接口
 */
public interface GroupInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据GroupId更新
	 */
	 Integer updateByGroupId(@Param("bean") T t,@Param("groupId") String groupId);


	/**
	 * 根据GroupId删除
	 */
	 Integer deleteByGroupId(@Param("groupId") String groupId);


	/**
	 * 根据GroupId获取对象
	 */
	 T selectByGroupId(@Param("groupId") String groupId);


	 List<UserGroupContactDto> loadMyGroup(String userId, String roleId);

	 /**
	 * 加载群组
	 */
	 T selectByGroupIdAndRoleId(@Param("groupId") String groupId, @Param("roleId") String roleId);

	 /**
	 * 解散群组
	 */
	 Integer dissolutionGroup(@Param("groupId") String groupId);

}
