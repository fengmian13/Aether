package com.anther.entity.query;



/**
 * 参数
 */
public class GroupRoleInfoQuery extends BaseParam {


	/**
	 * 
	 */
	private String roleId;

	private String roleIdFuzzy;

	/**
	 * 
	 */
	private String role;

	private String roleFuzzy;


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

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return this.role;
	}

	public void setRoleFuzzy(String roleFuzzy){
		this.roleFuzzy = roleFuzzy;
	}

	public String getRoleFuzzy(){
		return this.roleFuzzy;
	}

}
