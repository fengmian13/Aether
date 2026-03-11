package com.anther.service;

import java.util.List;

import com.anther.entity.query.ChatSessionQuery;
import com.anther.entity.po.ChatSession;
import com.anther.entity.vo.PaginationResultVO;


/**
 * 聊天会话表 业务接口
 */
public interface ChatSessionService {

	/**
	 * 根据条件查询列表
	 */
	List<ChatSession> findListByParam(ChatSessionQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ChatSessionQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatSession> findListByPage(ChatSessionQuery param);

	/**
	 * 新增
	 */
	Integer add(ChatSession bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatSession> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatSession> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(ChatSession bean,ChatSessionQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ChatSessionQuery param);

	/**
	 * 根据SessionId查询对象
	 */
	ChatSession getChatSessionBySessionId(Long sessionId);


	/**
	 * 根据SessionId修改
	 */
	Integer updateChatSessionBySessionId(ChatSession bean,Long sessionId);


	/**
	 * 根据SessionId删除
	 */
	Integer deleteChatSessionBySessionId(Long sessionId);


	/**
	 * 根据UserIdAndContactId查询对象
	 */
	ChatSession getChatSessionByUserIdAndContactId(String userId,String contactId);


	/**
	 * 根据UserIdAndContactId修改
	 */
	Integer updateChatSessionByUserIdAndContactId(ChatSession bean,String userId,String contactId);


	/**
	 * 根据UserIdAndContactId删除
	 */
	Integer deleteChatSessionByUserIdAndContactId(String userId,String contactId);

}