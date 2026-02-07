package com.anther.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface GroupRoleInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据RoleId更新
	 */
	 Integer updateByRoleId(@Param("bean") T t,@Param("roleId") String roleId);


	/**
	 * 根据RoleId删除
	 */
	 Integer deleteByRoleId(@Param("roleId") String roleId);


	/**
	 * 根据RoleId获取对象
	 */
	 T selectByRoleId(@Param("roleId") String roleId);


}
