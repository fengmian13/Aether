package com.anther.entity.dto;

import javax.validation.constraints.NotBlank;

public class CallAcceptDto {
    @NotBlank
    private String callId;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
