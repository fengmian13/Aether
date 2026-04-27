package com.anther.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class GroupInfo implements Serializable {


	/**
	 * 
	 */
	private String groupId;

	/**
	 * 群组名
	 */
	private String groupName;

	/**
	 * 群组描述
	 */
	private String groupIntroducte;

	/**
	 * 加入方式，是否需要管理员同意
	 */
	private Integer joinType;

	private String groupOwnerId;

	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getGroupOwnerId() {
		return groupOwnerId;
	}

	public void setGroupOwnerId(String groupOwnerId) {
		this.groupOwnerId = groupOwnerId;
	}

	public void setGroupId(String groupId){
		this.groupId = groupId;
	}

	public String getGroupId(){
		return this.groupId;
	}

	public void setGroupName(String groupName){
		this.groupName = groupName;
	}

	public String getGroupName(){
		return this.groupName;
	}

	public void setGroupIntroducte(String groupIntroducte){
		this.groupIntroducte = groupIntroducte;
	}

	public String getGroupIntroducte(){
		return this.groupIntroducte;
	}

	public void setJoinType(Integer joinType){
		this.joinType = joinType;
	}

	public Integer getJoinType(){
		return this.joinType;
	}

	@Override
	public String toString (){
		return "groupId:"+(groupId == null ? "空" : groupId)+"，群组名:"+(groupName == null ? "空" : groupName)+"，群组描述:"+(groupIntroducte == null ? "空" : groupIntroducte)+"，加入方式，是否需要管理员同意:"+(joinType == null ? "空" : joinType);
	}
}
