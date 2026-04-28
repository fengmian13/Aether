package com.anther.entity.enums;

public enum CallStatusEnum {
    INIT(0, "init"),
    ACCEPTED(1, "accepted"),
    REJECTED(2, "rejected"),
    CANCELED(3, "canceled"),
    HANGUP(4, "hangup"),
    TIMEOUT(5, "timeout");

    private final Integer status;
    private final String desc;

    CallStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static CallStatusEnum getByStatus(Integer status) {
        for (CallStatusEnum item : values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
