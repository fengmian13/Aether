package com.anther.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class GroupRoleInfo implements Serializable {


	/**
	 * 
	 */
	private String roleId;

	/**
	 * 
	 */
	private String role;


	public void setRoleId(String roleId){
		this.roleId = roleId;
	}

	public String getRoleId(){
		return this.roleId;
	}

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return this.role;
	}

	@Override
	public String toString (){
		return "roleId:"+(roleId == null ? "空" : roleId)+"，role:"+(role == null ? "空" : role);
	}
}
