package com.anther.entity.enums;

public enum IdEnum {
    USER_ID("U"),
    GROUP_ID("G");

    private final String prefix;

    IdEnum(String prefix) {
        this.prefix = prefix;
    }

    // 静态方法：根据前缀字符串查找对应的枚举值
    public static IdEnum fromPrefix(String prefix) {
        for (IdEnum idEnum : IdEnum.values()) {
            if (idEnum.prefix.equals(prefix)) {
                return idEnum;
            }
        }
        throw new IllegalArgumentException("Unknown prefix: " + prefix);
    }
}
