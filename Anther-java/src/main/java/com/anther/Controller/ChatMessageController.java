package com.anther.Controller;

import java.util.List;

import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.ResponseVO;
import com.anther.service.ChatMessageService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  Controller
 */
@RestController("chatMessageController")
@RequestMapping("/chatMessage")
public class ChatMessageController extends ABaseController{

	@Resource
	private ChatMessageService chatMessageService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(ChatMessageQuery query){
		return getSuccessResponseVO(chatMessageService.findListByPage(query));
	}

}