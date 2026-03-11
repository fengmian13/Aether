package com.anther.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.anther.entity.enums.DateTimePatternEnum;
import com.anther.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 聊天会话表
 */
public class ChatSession implements Serializable {


	/**
	 * 会话ID，唯一标识一条会话记录
	 */
	private Long sessionId;

	/**
	 * 当前用户ID，表示该会话属于哪个用户
	 */
	private String userId;

	/**
	 * 联系人ID，可以是好友ID或群ID
	 */
	private String contactId;

	/**
	 * 会话类型：1=单聊 2=群聊
	 */
	private Integer sessionType;

	/**
	 * 最后一条消息内容，用于会话列表展示
	 */
	private String lastMessage;

	/**
	 * 最后接收消息时间，用于会话排序
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastReceiveTime;

	/**
	 * 未读消息数量
	 */
	private Integer unreadCount;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;


	public void setSessionId(Long sessionId){
		this.sessionId = sessionId;
	}

	public Long getSessionId(){
		return this.sessionId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setContactId(String contactId){
		this.contactId = contactId;
	}

	public String getContactId(){
		return this.contactId;
	}

	public void setSessionType(Integer sessionType){
		this.sessionType = sessionType;
	}

	public Integer getSessionType(){
		return this.sessionType;
	}

	public void setLastMessage(String lastMessage){
		this.lastMessage = lastMessage;
	}

	public String getLastMessage(){
		return this.lastMessage;
	}

	public void setLastReceiveTime(Date lastReceiveTime){
		this.lastReceiveTime = lastReceiveTime;
	}

	public Date getLastReceiveTime(){
		return this.lastReceiveTime;
	}

	public void setUnreadCount(Integer unreadCount){
		this.unreadCount = unreadCount;
	}

	public Integer getUnreadCount(){
		return this.unreadCount;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}

	public Date getUpdateTime(){
		return this.updateTime;
	}

	@Override
	public String toString (){
		return "会话ID，唯一标识一条会话记录:"+(sessionId == null ? "空" : sessionId)+"，当前用户ID，表示该会话属于哪个用户:"+(userId == null ? "空" : userId)+"，联系人ID，可以是好友ID或群ID:"+(contactId == null ? "空" : contactId)+"，会话类型：1=单聊 2=群聊:"+(sessionType == null ? "空" : sessionType)+"，最后一条消息内容，用于会话列表展示:"+(lastMessage == null ? "空" : lastMessage)+"，最后接收消息时间，用于会话排序:"+(lastReceiveTime == null ? "空" : DateUtil.format(lastReceiveTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，未读消息数量:"+(unreadCount == null ? "空" : unreadCount)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}
