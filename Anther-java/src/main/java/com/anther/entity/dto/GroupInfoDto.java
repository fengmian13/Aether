package com.anther.entity.dto;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2026/2/8 18:01
 */
public class GroupInfoDto {

    private String nickName;
    private String contactId;
    private String contactType;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }
}
