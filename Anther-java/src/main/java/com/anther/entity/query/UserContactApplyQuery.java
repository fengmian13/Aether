package com.anther.entity.query;



/**
 * 联系人申请参数
 */
public class UserContactApplyQuery extends BaseParam {


	/**
	 * 自增ID
	 */
	private Integer applyId;

	/**
	 * 申请人id
	 */
	private String applyUserId;

	private String applyUserIdFuzzy;

	/**
	 * 接收人ID
	 */
	private String receiveUserId;

	private String receiveUserIdFuzzy;

	/**
	 * 最后申请时间
	 */
	private Long lastApplyTime;

	/**
	 * 状态0:待处理 1:已同意  2:已拒绝 3:已拉黑
	 */
	private Integer status;

	private Long lastApplyTimestamp;

	/**
	 * 申请信息
	 */
	private String applyInfo;

	private String applyInfoFuzzy;

	private Boolean queryContactInfo;

	public String getApplyInfo() {
		return applyInfo;
	}

	public void setApplyInfo(String applyInfo) {
		this.applyInfo = applyInfo;
	}

	public String getApplyInfoFuzzy() {
		return applyInfoFuzzy;
	}

	public void setApplyInfoFuzzy(String applyInfoFuzzy) {
		this.applyInfoFuzzy = applyInfoFuzzy;
	}

	public Boolean getQueryContactInfo() {
		return queryContactInfo;
	}

	public void setQueryContactInfo(Boolean queryContactInfo) {
		this.queryContactInfo = queryContactInfo;
	}

	public Long getLastApplyTimestamp() {
		return lastApplyTimestamp;
	}

	public void setLastApplyTimestamp(Long lastApplyTimestamp) {
		this.lastApplyTimestamp = lastApplyTimestamp;
	}

	public void setApplyId(Integer applyId){
		this.applyId = applyId;
	}

	public Integer getApplyId(){
		return this.applyId;
	}

	public void setApplyUserId(String applyUserId){
		this.applyUserId = applyUserId;
	}

	public String getApplyUserId(){
		return this.applyUserId;
	}

	public void setApplyUserIdFuzzy(String applyUserIdFuzzy){
		this.applyUserIdFuzzy = applyUserIdFuzzy;
	}

	public String getApplyUserIdFuzzy(){
		return this.applyUserIdFuzzy;
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

	public void setLastApplyTime(Long lastApplyTime){
		this.lastApplyTime = lastApplyTime;
	}

	public Long getLastApplyTime(){
		return this.lastApplyTime;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

}
