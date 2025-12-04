package com.anther.entity.vo;

/**
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/7/31 23:27
 */
public class UserInfoVO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 昵称
     */
    private String nickName;

    private Integer sex;

    private String token;

    private String meetingNo;

    private Boolean admin;

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getMeetingNo() {
        return meetingNo;
    }

    public void setMeetingNo(String meetingNo) {
        this.meetingNo = meetingNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
}
