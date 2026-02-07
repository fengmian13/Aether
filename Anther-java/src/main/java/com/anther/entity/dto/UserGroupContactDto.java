package com.anther.entity.dto;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2026/2/5 15:50
 */
public class UserGroupContactDto {
    private String groupId;
    private String groupName;
    private String groupRole;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(String groupRole) {
        this.groupRole = groupRole;
    }

    @Override
    public String toString() {
        return "UserGroupContactDto{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupRole='" + groupRole + '\'' +
                '}';
    }
}
