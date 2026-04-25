package com.anther.entity.query;


import java.util.List;

/**
 * 参数
 */
public class ChatMessageQuery extends BaseParam {


	/**
	 * 
	 */
	private Long messageId;

	/**
	 * 会话ID
	 */
	private String sessionId;

	private String sessionIdFuzzy;

	/**
	 * 发送人昵称
	 */
	private String sendUserNickName;

	private String sendUserNickNameFuzzy;

	/**
	 * 1=私聊, 2=群聊, 3=会议聊天
	 */
	private Integer sessionType;

	/**
	 * 发送人ID
	 */
	private String sendUserId;

	private String sendUserIdFuzzy;

	/**
	 * 消息类型：1=文本, 2=图片, 3=视频, 4=文件, 5=撤回指令, 6=系统通知
	 */
	private Integer messageType;

	/**
	 * 消息详情
	 */
	private String messageContent;

	private String contentFuzzy;

	/**
	 * 发送时间
	 */
	private Long sendTime;

	/**
	 * 接收人ID
	 */
	private String contactId;

	private String contactIdFuzzy;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名
	 */
	private String fileName;

	private String fileNameFuzzy;

	/**
	 * 文件后缀
	 */
	private String fileSuffix;

	private String fileSuffixFuzzy;

	/**
	 * 状态 0:正在发送 1:已发送
	 */
	private Integer status;

	private List<String> contactIdList;

	private Long lastReceiveTime;

	private String messageContentFuzzy;

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

	public List<String> getContactIdList() {
		return contactIdList;
	}

	public void setContactIdList(List<String> contactIdList) {
		this.contactIdList = contactIdList;
	}

	public Long getLastReceiveTime() {
		return lastReceiveTime;
	}

	public void setLastReceiveTime(Long lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}

	public String getSendUserNickName() {
		return sendUserNickName;
	}

	public void setSendUserNickName(String sendUserNickName) {
		this.sendUserNickName = sendUserNickName;
	}

	public String getSendUserNickNameFuzzy() {
		return sendUserNickNameFuzzy;
	}

	public void setSendUserNickNameFuzzy(String sendUserNickNameFuzzy) {
		this.sendUserNickNameFuzzy = sendUserNickNameFuzzy;
	}

	public String getMessageContentFuzzy() {
		return messageContentFuzzy;
	}

	public void setMessageContentFuzzy(String messageContentFuzzy) {
		this.messageContentFuzzy = messageContentFuzzy;
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

	public void setSessionIdFuzzy(String sessionIdFuzzy){
		this.sessionIdFuzzy = sessionIdFuzzy;
	}

	public String getSessionIdFuzzy(){
		return this.sessionIdFuzzy;
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

	public void setSendUserIdFuzzy(String sendUserIdFuzzy){
		this.sendUserIdFuzzy = sendUserIdFuzzy;
	}

	public String getSendUserIdFuzzy(){
		return this.sendUserIdFuzzy;
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

	public void setContentFuzzy(String contentFuzzy){
		this.contentFuzzy = contentFuzzy;
	}

	public String getContentFuzzy(){
		return this.contentFuzzy;
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

	public String getContactIdFuzzy() {
		return contactIdFuzzy;
	}

	public void setContactIdFuzzy(String contactIdFuzzy) {
		this.contactIdFuzzy = contactIdFuzzy;
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

	public void setFileNameFuzzy(String fileNameFuzzy){
		this.fileNameFuzzy = fileNameFuzzy;
	}

	public String getFileNameFuzzy(){
		return this.fileNameFuzzy;
	}

	public void setFileSuffix(String fileSuffix){
		this.fileSuffix = fileSuffix;
	}

	public String getFileSuffix(){
		return this.fileSuffix;
	}

	public void setFileSuffixFuzzy(String fileSuffixFuzzy){
		this.fileSuffixFuzzy = fileSuffixFuzzy;
	}

	public String getFileSuffixFuzzy(){
		return this.fileSuffixFuzzy;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

}
