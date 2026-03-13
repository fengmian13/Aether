package com.anther.Controller;

import java.util.List;

import com.anther.entity.query.ChatSessionQuery;
import com.anther.entity.po.ChatSession;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.ChatSessionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 聊天会话表 Controller
 */
@RestController("chatSessionController")
@RequestMapping("/chatSession")
public class ChatSessionController extends ABaseController{

	@Resource
	private ChatSessionService chatSessionService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatSessionQuery query){
		return getSuccessResponseVO(chatSessionService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(ChatSession bean) {
		chatSessionService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<ChatSession> listBean) {
		chatSessionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<ChatSession> listBean) {
		chatSessionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SessionId查询对象
	 */
	@RequestMapping("/getChatSessionBySessionId")
	public ResponseVO getChatSessionBySessionId(String sessionId) {
		return getSuccessResponseVO(chatSessionService.getChatSessionBySessionId(sessionId));
	}

	/**
	 * 根据SessionId修改对象
	 */
	@RequestMapping("/updateChatSessionBySessionId")
	public ResponseVO updateChatSessionBySessionId(ChatSession bean,String sessionId) {
		chatSessionService.updateChatSessionBySessionId(bean,sessionId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SessionId删除
	 */
	@RequestMapping("/deleteChatSessionBySessionId")
	public ResponseVO deleteChatSessionBySessionId(String sessionId) {
		chatSessionService.deleteChatSessionBySessionId(sessionId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId查询对象
	 */
	@RequestMapping("/getChatSessionByUserIdAndContactId")
	public ResponseVO getChatSessionByUserIdAndContactId(String userId,String contactId) {
		return getSuccessResponseVO(chatSessionService.getChatSessionByUserIdAndContactId(userId,contactId));
	}

	/**
	 * 根据UserIdAndContactId修改对象
	 */
	@RequestMapping("/updateChatSessionByUserIdAndContactId")
	public ResponseVO updateChatSessionByUserIdAndContactId(ChatSession bean,String userId,String contactId) {
		chatSessionService.updateChatSessionByUserIdAndContactId(bean,userId,contactId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserIdAndContactId删除
	 */
	@RequestMapping("/deleteChatSessionByUserIdAndContactId")
	public ResponseVO deleteChatSessionByUserIdAndContactId(String userId,String contactId) {
		chatSessionService.deleteChatSessionByUserIdAndContactId(userId,contactId);
		return getSuccessResponseVO(null);
	}
}