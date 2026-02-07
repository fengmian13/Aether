package com.anther.entity.enums;

public enum GroupRoleEnum {
    ADMIN("01", "管理员"),
    MEMBER("02", "成员"),
    MASTER("03", "群主");

    private String type;
    private String desc;
    GroupRoleEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public String getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
}
