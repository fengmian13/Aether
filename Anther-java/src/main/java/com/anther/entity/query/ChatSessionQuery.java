package com.anther.entity.query;

import java.util.Date;


/**
 * 聊天会话表参数
 */
public class ChatSessionQuery extends BaseParam {


	/**
	 * 会话ID，唯一标识一条会话记录
	 */
	private String sessionId;

	/**
	 * 当前用户ID，表示该会话属于哪个用户
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 联系人ID，可以是好友ID或群ID
	 */
	private String contactId;

	private String contactIdFuzzy;

	/**
	 * 会话类型：1=单聊 2=群聊
	 */
	private Integer sessionType;

	/**
	 * 最后一条消息内容，用于会话列表展示
	 */
	private String lastMessage;

	private String lastMessageFuzzy;

	/**
	 * 最后接收消息时间，用于会话排序
	 */
	private String lastReceiveTime;

	private String lastReceiveTimeStart;

	private String lastReceiveTimeEnd;

	/**
	 * 未读消息数量
	 */
	private Integer unreadCount;

	/**
	 * 创建时间
	 */
	private String createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 更新时间
	 */
	private String updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;


	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getSessionId(){
		return this.sessionId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setContactId(String contactId){
		this.contactId = contactId;
	}

	public String getContactId(){
		return this.contactId;
	}

	public void setContactIdFuzzy(String contactIdFuzzy){
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy(){
		return this.contactIdFuzzy;
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

	public void setLastMessageFuzzy(String lastMessageFuzzy){
		this.lastMessageFuzzy = lastMessageFuzzy;
	}

	public String getLastMessageFuzzy(){
		return this.lastMessageFuzzy;
	}

	public void setLastReceiveTime(String lastReceiveTime){
		this.lastReceiveTime = lastReceiveTime;
	}

	public String getLastReceiveTime(){
		return this.lastReceiveTime;
	}

	public void setLastReceiveTimeStart(String lastReceiveTimeStart){
		this.lastReceiveTimeStart = lastReceiveTimeStart;
	}

	public String getLastReceiveTimeStart(){
		return this.lastReceiveTimeStart;
	}
	public void setLastReceiveTimeEnd(String lastReceiveTimeEnd){
		this.lastReceiveTimeEnd = lastReceiveTimeEnd;
	}

	public String getLastReceiveTimeEnd(){
		return this.lastReceiveTimeEnd;
	}

	public void setUnreadCount(Integer unreadCount){
		this.unreadCount = unreadCount;
	}

	public Integer getUnreadCount(){
		return this.unreadCount;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

	public void setCreateTimeStart(String createTimeStart){
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart(){
		return this.createTimeStart;
	}
	public void setCreateTimeEnd(String createTimeEnd){
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd(){
		return this.createTimeEnd;
	}

	public void setUpdateTime(String updateTime){
		this.updateTime = updateTime;
	}

	public String getUpdateTime(){
		return this.updateTime;
	}

	public void setUpdateTimeStart(String updateTimeStart){
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeStart(){
		return this.updateTimeStart;
	}
	public void setUpdateTimeEnd(String updateTimeEnd){
		this.updateTimeEnd = updateTimeEnd;
	}

	public String getUpdateTimeEnd(){
		return this.updateTimeEnd;
	}

}
