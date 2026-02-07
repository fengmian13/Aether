package com.anther.entity.query;



/**
 * 参数
 */
public class UserGroupQuery extends BaseParam {


	/**
	 * 
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 
	 */
	private String groupId;

	private String groupIdFuzzy;

	/**
	 * 
	 */
	private String roleId;

	private String roleIdFuzzy;


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

	public void setGroupId(String groupId){
		this.groupId = groupId;
	}

	public String getGroupId(){
		return this.groupId;
	}

	public void setGroupIdFuzzy(String groupIdFuzzy){
		this.groupIdFuzzy = groupIdFuzzy;
	}

	public String getGroupIdFuzzy(){
		return this.groupIdFuzzy;
	}

	public void setRoleId(String roleId){
		this.roleId = roleId;
	}

	public String getRoleId(){
		return this.roleId;
	}

	public void setRoleIdFuzzy(String roleIdFuzzy){
		this.roleIdFuzzy = roleIdFuzzy;
	}

	public String getRoleIdFuzzy(){
		return this.roleIdFuzzy;
	}

}
