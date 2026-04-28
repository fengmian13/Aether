package com.anther.entity.query;

public class CallInfoQuery extends BaseParam {
    private String callId;
    private String callerUserId;
    private String calleeUserId;
    private Integer status;
    private Long startTime;
    private Long startTimeBefore;

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

    public String getCalleeUserId() {
        return calleeUserId;
    }

    public void setCalleeUserId(String calleeUserId) {
        this.calleeUserId = calleeUserId;
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

    public Long getStartTimeBefore() {
        return startTimeBefore;
    }

    public void setStartTimeBefore(Long startTimeBefore) {
        this.startTimeBefore = startTimeBefore;
    }
}
