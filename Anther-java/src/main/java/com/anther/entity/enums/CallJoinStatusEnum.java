package com.anther.entity.enums;

public enum CallJoinStatusEnum {
    NOT_JOINED(0, "not_joined"),
    JOINED(1, "joined"),
    LEFT(2, "left");

    private final Integer status;
    private final String desc;

    CallJoinStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
