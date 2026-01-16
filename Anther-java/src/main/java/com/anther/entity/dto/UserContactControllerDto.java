package com.anther.entity.dto;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description:
 * @date 2025/12/25 22:58
 */
public class UserContactControllerDto {

    private String nickName;
    private Integer sex;
    private Integer status;
    private String contactId;
    private String contactType;
    // 用户头像后期上传头像使用userId命名


    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
}
