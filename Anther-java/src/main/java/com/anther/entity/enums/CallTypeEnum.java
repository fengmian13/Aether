package com.anther.entity.enums;

public enum CallTypeEnum {
    AUDIO(0, "audio"),
    VIDEO(1, "video");

    private final Integer type;
    private final String desc;

    CallTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CallTypeEnum getByType(Integer type) {
        for (CallTypeEnum item : values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
