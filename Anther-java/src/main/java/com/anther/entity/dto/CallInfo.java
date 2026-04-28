package com.anther.entity.dto;

import java.io.Serializable;
import java.util.List;

public class CallInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String callId;
    private String callerUserId;
    private String callerNickName;
    private String calleeUserId;
    private String calleeNickName;
    private Integer callType;
    private Integer status;
    private Long startTime;
    private Long answerTime;
    private Long endTime;
    private List<CallUser> users;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(String callerUserId) {
        this.callerUserId = callerUserId;
    }

    public String getCallerNickName() {
        return callerNickName;
    }

    public void setCallerNickName(String callerNickName) {
        this.callerNickName = callerNickName;
    }

    public String getCalleeUserId() {
        return calleeUserId;
    }

    public void setCalleeUserId(String calleeUserId) {
        this.calleeUserId = calleeUserId;
    }

    public String getCalleeNickName() {
        return calleeNickName;
    }

    public void setCalleeNickName(String calleeNickName) {
        this.calleeNickName = calleeNickName;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Long answerTime) {
        this.answerTime = answerTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<CallUser> getUsers() {
        return users;
    }

    public void setUsers(List<CallUser> users) {
        this.users = users;
    }
}
