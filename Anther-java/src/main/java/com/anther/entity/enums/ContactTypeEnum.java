package com.anther.entity.enums;

public enum ContactTypeEnum {
    USER("USER", "用户"),
    GROUP("GROUP", "群组");


    private String type;
    private String desc;

    private ContactTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    // 建议添加getter方法以便外部访问
    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
