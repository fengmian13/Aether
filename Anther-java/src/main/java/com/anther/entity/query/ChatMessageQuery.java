package com.anther.entity.query;



/**
 * 参数
 */
public class ChatMessageQuery extends BaseParam {


	/**
	 * 
	 */
	private Long id;

	/**
	 * 会话ID
	 */
	private String sessionId;

	private String sessionIdFuzzy;

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
	private Integer msgType;

	/**
	 * 消息详情
	 */
	private String content;

	private String contentFuzzy;

	/**
	 * 发送时间
	 */
	private Long sendTime;

	/**
	 * 接收人ID
	 */
	private String receiveUserId;

	private String receiveUserIdFuzzy;

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


	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
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

	public void setMsgType(Integer msgType){
		this.msgType = msgType;
	}

	public Integer getMsgType(){
		return this.msgType;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return this.content;
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

	public void setReceiveUserId(String receiveUserId){
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveUserId(){
		return this.receiveUserId;
	}

	public void setReceiveUserIdFuzzy(String receiveUserIdFuzzy){
		this.receiveUserIdFuzzy = receiveUserIdFuzzy;
	}

	public String getReceiveUserIdFuzzy(){
		return this.receiveUserIdFuzzy;
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
