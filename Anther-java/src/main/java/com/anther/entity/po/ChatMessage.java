package com.anther.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class ChatMessage implements Serializable {


	/**
	 * 
	 */
	private Long messageId;

	/**
	 * 会话ID
	 */
	private String sessionId;

	/**
	 * 1=私聊, 2=群聊, 3=会议聊天
	 */
	private Integer sessionType;

	/**
	 * 发送人ID
	 */
	private String sendUserId;

	/**
	 * 发送人昵称
	 */
	private String sendUserNickName;

	/**
	 * 消息类型：1=文本, 2=图片, 3=视频, 4=文件, 5=撤回指令, 6=系统通知
	 */
	private Integer messageType;

	/**
	 * 消息详情
	 */
	private String messageContent;

	/**
	 * 发送时间
	 */
	private Long sendTime;

	/**
	 * 接收人ID
	 */
	private String contactId;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件后缀
	 */
	private String fileSuffix;

	/**
	 * 状态 0:正在发送 1:已发送
	 */
	private Integer status;

	/**
	 * 联系人类型 0:单聊 1:群聊
	 */
	private Integer contactType;

	public Integer getContactType() {
		return contactType;
	}

	public void setContactType(Integer contactType) {
		this.contactType = contactType;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

	public String getSessionId(){
		return this.sessionId;
	}

	public void setSessionType(Integer sessionType){
		this.sessionType = sessionType;
	}

	public Integer getSessionType(){
		return this.sessionType;
	}

	public void setSendUserId(String sendUserId){
		this.sendUserId = sendUserId;
	}

	public String getSendUserId(){
		return this.sendUserId;
	}

	public void setSendUserNickName(String sendUserNickName) {
		this.sendUserNickName = sendUserNickName;
	}

	public String getSendUserNickName() {
		return this.sendUserNickName;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public void setSendTime(Long sendTime){
		this.sendTime = sendTime;
	}

	public Long getSendTime(){
		return this.sendTime;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public void setFileSize(Long fileSize){
		this.fileSize = fileSize;
	}

	public Long getFileSize(){
		return this.fileSize;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public String getFileName(){
		return this.fileName;
	}

	public void setFileSuffix(String fileSuffix){
		this.fileSuffix = fileSuffix;
	}

	public String getFileSuffix(){
		return this.fileSuffix;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	@Override
	public String toString (){
		return "id:"+(messageId == null ? "空" : messageId)+"，会话ID:"+(sessionId == null ? "空" : sessionId)+"，1=私聊, 2=群聊, 3=会议聊天:"+(sessionType == null ? "空" : sessionType)+"，发送人ID:"+(sendUserId == null ? "空" : sendUserId)+"，消息类型：1=文本, 2=图片, 3=视频, 4=文件, 5=撤回指令, 6=系统通知:"+(messageType == null ? "空" : messageType)+"，消息详情:"+(messageContent == null ? "空" : messageContent)+"，发送时间:"+(sendTime == null ? "空" : sendTime)+"，接收人ID:"+(contactId == null ? "空" : contactId)+"，文件大小:"+(fileSize == null ? "空" : fileSize)+"，文件名:"+(fileName == null ? "空" : fileName)+"，文件后缀:"+(fileSuffix == null ? "空" : fileSuffix)+"，状态 0:正在发送 1:已发送:"+(status == null ? "空" : status);
	}
}
