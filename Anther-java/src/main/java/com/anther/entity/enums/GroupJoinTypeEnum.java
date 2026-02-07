package com.anther.entity.enums;

public enum GroupJoinTypeEnum {
    USER_APPROVE("0", "off"),
    ANYONE_CAN_JOIN("1", "on");


    private String type;
    private String desc;

    private GroupJoinTypeEnum(String type, String desc) {
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
