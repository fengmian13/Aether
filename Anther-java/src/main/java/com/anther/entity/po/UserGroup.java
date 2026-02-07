package com.anther.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class UserGroup implements Serializable {


	/**
	 * 
	 */
	private String userId;

	/**
	 * 
	 */
	private String groupId;

	/**
	 * 
	 */
	private String roleId;


	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setGroupId(String groupId){
		this.groupId = groupId;
	}

	public String getGroupId(){
		return this.groupId;
	}

	public void setRoleId(String roleId){
		this.roleId = roleId;
	}

	public String getRoleId(){
		return this.roleId;
	}

	@Override
	public String toString (){
		return "userId:"+(userId == null ? "空" : userId)+"，groupId:"+(groupId == null ? "空" : groupId)+"，roleId:"+(roleId == null ? "空" : roleId);
	}
}
