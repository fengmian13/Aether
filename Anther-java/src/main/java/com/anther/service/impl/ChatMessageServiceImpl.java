package com.anther.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.enums.MessageSend2TypeEnum;
import com.anther.websocket.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anther.entity.enums.PageSize;
import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.ChatMessageMapper;
import com.anther.service.ChatMessageService;
import com.anther.utils.StringTools;

import static com.anther.utils.StringTools.generatePrivateSessionId;


/**
 *  业务接口实现
 */
@Slf4j
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {

	@Resource
	private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;

	@Resource
	private MessageHandler messageHandler;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ChatMessage> findListByParam(ChatMessageQuery param) {
		return this.chatMessageMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(ChatMessageQuery param) {
		return this.chatMessageMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<ChatMessage> findListByPage(ChatMessageQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<ChatMessage> list = this.findListByParam(param);
		PaginationResultVO<ChatMessage> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(ChatMessage bean) {
		return this.chatMessageMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(ChatMessage bean, ChatMessageQuery param) {
		StringTools.checkParam(param);
		return this.chatMessageMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(ChatMessageQuery param) {
		StringTools.checkParam(param);
		return this.chatMessageMapper.deleteByParam(param);
	}

	/**
	 * 根据Id获取对象
	 */
	@Override
	public ChatMessage getChatMessageById(Long id) {
		return this.chatMessageMapper.selectById(id);
	}

	/**
	 * 根据Id修改
	 */
	@Override
	public Integer updateChatMessageById(ChatMessage bean, Long id) {
		return this.chatMessageMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	@Override
	public Integer deleteChatMessageById(Long id) {
		return this.chatMessageMapper.deleteById(id);
	}

	public void saveAndSendMessage(MessageSendDto messageDto) {
		String sendUserId = messageDto.getSendUserId();
		String receiveUserId = messageDto.getReceiveUserId();
		long currentTime = System.currentTimeMillis();

		// 1. 构建数据库实体对象 ChatMessage
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setSendUserId(sendUserId);
		chatMessage.setReceiveUserId(receiveUserId);

		// 【修正点 1】修改为调用实体类现有的 setContent 和 setMsgType
		chatMessage.setContent((String) messageDto.getMessageContent());
		chatMessage.setMsgType(messageDto.getMessageType());

		chatMessage.setSendTime(currentTime);
		chatMessage.setStatus(1); // 1: 已发送状态
		log.info("WebSocket 消息: chatMessage1:{}", chatMessage);

		// 判断是私聊还是群聊，生成对应的 SessionId
		if (MessageSend2TypeEnum.USER.getType().equals(messageDto.getMessageSend2Type())) {
			chatMessage.setSessionType(1); // 1: 代表私聊
			// 生成私聊专属 sessionId，保证两人的会话ID永远唯一且一致
			String sessionId = generatePrivateSessionId(sendUserId, receiveUserId);
			chatMessage.setSessionId(sessionId);
		} else if (MessageSend2TypeEnum.GROUP.getType().equals(messageDto.getMessageSend2Type())) {
			chatMessage.setSessionType(2); // 2: 代表群聊
			chatMessage.setSessionId(receiveUserId); // 群聊 sessionId 直接使用 groupId
		}

		// 将消息入库保存 (持久化)
		log.info("WebSocket 消息: chatMessage2持久化:{}", chatMessage);
		chatMessageMapper.insert(chatMessage);

		// 将补全后的数据回填到 DTO，准备用于跨节点广播
		messageDto.setSessionId(chatMessage.getSessionId());
		messageDto.setSendTime(currentTime);
		//把数据库自动生成的自增主键 id 也带给前端，未来做“消息撤回”或“已读回执”用得上
		messageDto.setMessageId(chatMessage.getId());

		// 将消息发往消息中心 (Redis/RabbitMQ)，交由监听器消费后推送给真正的 WebSocket 客户端
		messageHandler.sendMessage(messageDto);
	}
}