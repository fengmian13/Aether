package com.anther.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.anther.entity.config.AppConfig;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.SysSettingDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.*;
import com.anther.entity.po.ChatSession;
import com.anther.entity.po.UserContact;
import com.anther.entity.query.ChatSessionQuery;
import com.anther.entity.query.UserContactQuery;
import com.anther.exception.BusinessException;
import com.anther.mappers.ChatSessionMapper;
import com.anther.mappers.UserContactMapper;
import com.anther.redis.RedisComponent;
import com.anther.utils.CopyTools;
import com.anther.utils.DateUtil;
import com.anther.websocket.message.MessageHandler;
import jodd.util.ArraysUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.ChatMessageMapper;
import com.anther.service.ChatMessageService;
import com.anther.utils.StringTools;

import static com.anther.utils.StringTools.generatePrivateSessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anther.entity.constants.Constants;

/**
 *  业务接口实现
 */
@Slf4j
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {
	private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

	@Resource
	private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;

	@Resource
	private MessageHandler messageHandler;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;

	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

	@Resource
	private AppConfig appConfig;

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


	@Override
	public MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto) {
		//不是机器人回复，判断好友状态
		if (!Constants.ROBOT_UID.equals(tokenUserInfoDto.getUserId())) {
			List<String> contactList = redisComponent.getUserContactList(tokenUserInfoDto.getUserId());
			if (!contactList.contains(chatMessage.getReceiveUserId())) {
				UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatMessage.getReceiveUserId());
				if (UserContactTypeEnum.USER == userContactTypeEnum) {
					throw new BusinessException(ResponseCodeEnum.CODE_904);
				} else {
					throw new BusinessException(ResponseCodeEnum.CODE_903);
				}
			}
		}
		String sessionId = null;
		String sendUserId = tokenUserInfoDto.getUserId();
		String contactId = chatMessage.getReceiveUserId();
		//生成date的 时间
		Long curTime = System.currentTimeMillis();
		Date curDate = StringTools.getLocalDateTimeFromLong(curTime);

		UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
		MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(chatMessage.getMsgType());
		String lastMessage = chatMessage.getContent();
		String messageContent = StringTools.resetMessageContent(lastMessage);
		chatMessage.setContent(messageContent);
		Integer status = MessageTypeEnum.MEDIA_CHAT == messageTypeEnum ? MessageStatusEnum.SENDING.getStatus() : MessageStatusEnum.SENDED.getStatus();
		if (ArraysUtil.contains(new Integer[]{
				MessageTypeEnum.CHAT.getType(),
				MessageTypeEnum.GROUP_CREATE.getType(),
				MessageTypeEnum.ADD_FRIEND.getType(),
				MessageTypeEnum.MEDIA_CHAT.getType()
		}, messageTypeEnum.getType())) {
			if (UserContactTypeEnum.USER == contactTypeEnum) {
				//生成会话ID
				sessionId = StringTools.getChatSessionId4User(new String[]{sendUserId, contactId});
			} else {
				sessionId = StringTools.getChatSessionId4Group(contactId);
			}
			//更新会话消息
			ChatSession chatSession = new ChatSession();
			chatSession.setLastMessage(messageContent);
			if (UserContactTypeEnum.GROUP == contactTypeEnum && !MessageTypeEnum.GROUP_CREATE.getType().equals(messageTypeEnum.getType())) {
				chatSession.setLastMessage(tokenUserInfoDto.getNickName() + "：" + messageContent);
			}
			lastMessage = chatSession.getLastMessage();
			//如果是媒体文件
			chatSession.setLastReceiveTime(curDate);
			chatSessionMapper.updateBySessionId(chatSession, sessionId);
			//记录消息消息表
			chatMessage.setSessionId(sessionId);
			chatMessage.setSendUserId(sendUserId);
			chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
			chatMessage.setSendTime(curTime);
			chatMessage.setMsgType(contactTypeEnum.getType());
			chatMessage.setStatus(status);
			chatMessageMapper.insert(chatMessage);
		}
		MessageSendDto messageSend = CopyTools.copy(chatMessage, MessageSendDto.class);
		if (Constants.ROBOT_UID.equals(contactId)) {
			SysSettingDto sysSettingDto = redisComponent.getSysSetting();
			TokenUserInfoDto robot = new TokenUserInfoDto();
			robot.setUserId(sysSettingDto.getRobotUid());
			robot.setNickName(sysSettingDto.getRobotNickName());
			ChatMessage robotChatMessage = new ChatMessage();
			robotChatMessage.setReceiveUserId(sendUserId);
			//这里可以对接Ai 根据输入的信息做出回答
			robotChatMessage.setContent("我只是一个机器人无法识别你的消息");
			robotChatMessage.setMsgType(MessageTypeEnum.CHAT.getType());
			saveMessage(robotChatMessage, robot);
		} else {
			messageHandler.sendMessage(messageSend);
		}
		return messageSend;
	}

	public File downloadFile(TokenUserInfoDto userInfoDto, Long messageId, Boolean cover) {
		ChatMessage message = chatMessageMapper.selectById(messageId);
		String contactId = message.getReceiveUserId();
		UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
		if (UserContactTypeEnum.USER.getType().equals(contactTypeEnum) && !userInfoDto.getUserId().equals(message.getReceiveUserId())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (UserContactTypeEnum.GROUP.getType().equals(contactTypeEnum)) {
			UserContactQuery userContactQuery = new UserContactQuery();
			userContactQuery.setUserId(userInfoDto.getUserId());
			userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType());
			userContactQuery.setContactId(contactId);
			userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			Integer contactCount = userContactMapper.selectCount(userContactQuery);
			if (contactCount == 0) {
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
		}
		String month = DateUtil.format(new Date(message.getSendTime()), DateTimePatternEnum.YYYYMM.getPattern());
		File folder = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		String fileName = message.getFileName();
		String fileExtName = StringTools.getFileSuffix(fileName);
		String fileRealName = messageId + fileExtName;

		if (cover != null && cover) {
			fileRealName = fileRealName + Constants.COVER_IMAGE_SUFFIX;
		}
		File file = new File(folder.getPath() + "/" + fileRealName);
		if (!file.exists()) {
			logger.info("文件不存在");
			throw new BusinessException(ResponseCodeEnum.CODE_602);
		}
		return file;
	}
}