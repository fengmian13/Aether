package com.anther.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;


/**
 *  业务接口
 */
public interface ChatMessageService {

	/**
	 * 根据条件查询列表
	 */
	List<ChatMessage> findListByParam(ChatMessageQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(ChatMessageQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery param);

	/**
	 * 新增
	 */
	Integer add(ChatMessage bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ChatMessage> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ChatMessage> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(ChatMessage bean,ChatMessageQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(ChatMessageQuery param);

	/**
	 * 根据Id查询对象
	 */
	ChatMessage getChatMessageById(Long id);


	/**
	 * 根据Id修改
	 */
	Integer updateChatMessageById(ChatMessage bean,Long id);


	/**
	 * 根据Id删除
	 */
	Integer deleteChatMessageById(Long id);

	MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto);

	File downloadFile(TokenUserInfoDto userInfoDto, Long messageId, Boolean cover);

	void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover);
}