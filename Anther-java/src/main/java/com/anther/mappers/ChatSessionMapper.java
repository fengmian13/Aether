package com.anther.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 聊天会话表 数据库操作接口
 */
public interface ChatSessionMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据SessionId更新
	 */
	 Integer updateBySessionId(@Param("bean") T t,@Param("sessionId") Long sessionId);


	/**
	 * 根据SessionId删除
	 */
	 Integer deleteBySessionId(@Param("sessionId") Long sessionId);


	/**
	 * 根据SessionId获取对象
	 */
	 T selectBySessionId(@Param("sessionId") Long sessionId);


	/**
	 * 根据UserIdAndContactId更新
	 */
	 Integer updateByUserIdAndContactId(@Param("bean") T t,@Param("userId") String userId,@Param("contactId") String contactId);


	/**
	 * 根据UserIdAndContactId删除
	 */
	 Integer deleteByUserIdAndContactId(@Param("userId") String userId,@Param("contactId") String contactId);


	/**
	 * 根据UserIdAndContactId获取对象
	 */
	 T selectByUserIdAndContactId(@Param("userId") String userId,@Param("contactId") String contactId);


}
