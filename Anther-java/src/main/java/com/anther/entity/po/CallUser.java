package com.anther.entity.po;

import java.io.Serializable;

public class CallUser implements Serializable {
    private Long id;
    private String callId;
    private String userId;
    private String nickName;
    private Integer role;
    private Integer joinStatus;
    private Integer audioEnabled;
    private Integer videoEnabled;
    private String rtcStatus;
    private Long joinTime;
    private Long leaveTime;
    private Long createTime;
    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(Integer joinStatus) {
        this.joinStatus = joinStatus;
    }

    public Integer getAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(Integer audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public Integer getVideoEnabled() {
        return videoEnabled;
    }

    public void setVideoEnabled(Integer videoEnabled) {
        this.videoEnabled = videoEnabled;
    }

    public String getRtcStatus() {
        return rtcStatus;
    }

    public void setRtcStatus(String rtcStatus) {
        this.rtcStatus = rtcStatus;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public Long getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Long leaveTime) {
        this.leaveTime = leaveTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
