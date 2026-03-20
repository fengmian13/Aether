package com.anther.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2026/1/29 14:16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactApplyDto implements Serializable {
    private String applyUserId;
    private String applyUserNickName;
    private Integer status;
    private String applyTime;
    private String ReceiveUserId;

    public String getReceiveUserId() {
        return ReceiveUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        ReceiveUserId = receiveUserId;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserNickName() {
        return applyUserNickName;
    }

    public void setApplyUserNickName(String applyUserNickName) {
        this.applyUserNickName = applyUserNickName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }
}
