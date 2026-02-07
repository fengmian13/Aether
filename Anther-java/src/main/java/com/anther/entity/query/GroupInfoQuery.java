package com.anther.entity.query;



/**
 * 参数
 */
public class GroupInfoQuery extends BaseParam {


	/**
	 * 
	 */
	private String groupId;

	private String groupIdFuzzy;

	/**
	 * 群组名
	 */
	private String groupName;

	private String groupNameFuzzy;

	/**
	 * 群组描述
	 */
	private String groupIntroducte;

	private String groupIntroducteFuzzy;

	/**
	 * 加入方式，是否需要管理员同意
	 */
	private String joinType;

	private String joinTypeFuzzy;


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

	public void setGroupName(String groupName){
		this.groupName = groupName;
	}

	public String getGroupName(){
		return this.groupName;
	}

	public void setGroupNameFuzzy(String groupNameFuzzy){
		this.groupNameFuzzy = groupNameFuzzy;
	}

	public String getGroupNameFuzzy(){
		return this.groupNameFuzzy;
	}

	public void setGroupIntroducte(String groupIntroducte){
		this.groupIntroducte = groupIntroducte;
	}

	public String getGroupIntroducte(){
		return this.groupIntroducte;
	}

	public void setGroupIntroducteFuzzy(String groupIntroducteFuzzy){
		this.groupIntroducteFuzzy = groupIntroducteFuzzy;
	}

	public String getGroupIntroducteFuzzy(){
		return this.groupIntroducteFuzzy;
	}

	public void setJoinType(String joinType){
		this.joinType = joinType;
	}

	public String getJoinType(){
		return this.joinType;
	}

	public void setJoinTypeFuzzy(String joinTypeFuzzy){
		this.joinTypeFuzzy = joinTypeFuzzy;
	}

	public String getJoinTypeFuzzy(){
		return this.joinTypeFuzzy;
	}

}
