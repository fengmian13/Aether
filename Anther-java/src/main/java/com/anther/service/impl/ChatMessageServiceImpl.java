package com.anther.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.anther.entity.enums.PageSize;
import com.anther.entity.query.ChatMessageQuery;
import com.anther.entity.po.ChatMessage;
import com.anther.entity.vo.PaginationResultVO;
import com.anther.entity.query.SimplePage;
import com.anther.mappers.ChatMessageMapper;
import com.anther.service.ChatMessageService;
import com.anther.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {

	@Resource
	private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;

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
}