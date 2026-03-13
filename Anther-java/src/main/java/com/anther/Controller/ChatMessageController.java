package com.anther.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.anther.annotation.GlobalInterceptor;
import com.anther.entity.config.AppConfig;
import com.anther.entity.constants.Constants;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.MessageTypeEnum;
import com.anther.entity.enums.ResponseCodeEnum;
import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.ResponseVO;
import com.anther.exception.BusinessException;
import com.anther.service.ChatMessageService;
import com.anther.service.impl.ChatMessageServiceImpl;
import com.anther.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Controller
 */
@RestController("chatMessageController")
@RequestMapping("/chat")
public class ChatMessageController extends ABaseController{
	private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

	@Resource
	private ChatMessageService chatMessageService;

	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatMessageQuery query){
		return getSuccessResponseVO(chatMessageService.findListByPage(query));
	}

	@RequestMapping("/sendMessage")
	@GlobalInterceptor
	public ResponseVO sendMessage(@NotEmpty String contactId,
								  @NotEmpty @Size(max = 500) String messageContent,
								  @NotNull Integer messageType,
								  Long fileSize,
								  String fileName,
								  String Suffix) {
		MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(messageType);
		if (null == messageTypeEnum || !ArrayUtils.contains(new Integer[]{MessageTypeEnum.CHAT.getType(), MessageTypeEnum.MEDIA_CHAT.getType()}, messageType)) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setReceiveUserId(contactId);
		chatMessage.setContent(messageContent);
		chatMessage.setFileSize(fileSize);
		chatMessage.setFileName(fileName);
		chatMessage.setFileSuffix(Suffix);
		chatMessage.setMsgType(messageType);
		MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, tokenUserInfoDto);
		return getSuccessResponseVO(messageSendDto);
	}

	@RequestMapping("downloadFile")
	@GlobalInterceptor
	public void downloadFile(HttpServletResponse response,
							 @NotEmpty String fileId,
							 @NotNull Boolean showCover) throws Exception {
		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		OutputStream out = null;
		FileInputStream in = null;
		try {
			File file = null;
			if (!StringTools.isEmpty(fileId)) {
				String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
				String avatarPath = appConfig.getProjectFolder() + avatarFolderName + fileId + Constants.IMAGE_SUFFIX;
				if (showCover) {
					avatarPath = avatarPath + Constants.COVER_IMAGE_SUFFIX;
				}
				file = new File(avatarPath);
				if (!file.exists()) {
					throw new BusinessException(ResponseCodeEnum.CODE_602);
				}
			} else {
				file = chatMessageService.downloadFile(userInfoDto, Long.parseLong(fileId), showCover);
			}
			response.setContentType("application/x-msdownload; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;");
			response.setContentLengthLong(file.length());
			in = new FileInputStream(file);
			byte[] byteData = new byte[1024];
			out = response.getOutputStream();
			int len = 0;
			while ((len = in.read(byteData)) != -1) {
				out.write(byteData, 0, len);
			}
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("IO异常", e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("IO异常", e);
				}
			}
		}
	}

}