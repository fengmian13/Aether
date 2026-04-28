package com.anther.entity.enums;

public enum CallUserRoleEnum {
    CALLER(0, "caller"),
    CALLEE(1, "callee");

    private final Integer role;
    private final String desc;

    CallUserRoleEnum(Integer role, String desc) {
        this.role = role;
        this.desc = desc;
    }

    public Integer getRole() {
        return role;
    }

    public String getDesc() {
        return desc;
    }
}
