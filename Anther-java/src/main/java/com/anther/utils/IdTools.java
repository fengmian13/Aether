package com.anther.utils;

import com.anther.entity.enums.IdEnum;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: 用户id和群组ia处理文件
 * @date 2026/1/28 15:42
 */
public class IdTools {

    public static String getUserId(String userId) {
        return "U" + userId;
    }

    public static String getGroupId(String groupId) {
        return "G" + groupId;
    }

    public static IdEnum getUserIdOrGroupIdById(String id) {
        char first_char = id.charAt(0);
        return IdEnum.fromPrefix(Character.toString(first_char));
    }
}
