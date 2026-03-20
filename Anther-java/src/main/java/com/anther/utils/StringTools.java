package com.anther.utils;
import com.anther.entity.constants.Constants;
import com.anther.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;


public class StringTools {

    public static void checkParam(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof String && !StringTools.isEmpty(object.toString())
                        || object != null && !(object instanceof String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，删除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }

    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        //如果第二个字母是大写，第一个字母不大写
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    //将两人的 userId 排序后拼接：U0001 和 U0005 私聊，session_id 是 U0001_U0005。
    public static String generatePrivateSessionId(String UserId, String receiveUserId) {
        if(UserId == null || receiveUserId == null){
            throw new BusinessException("UserId 不能为空");
        }
        if(UserId.compareTo(receiveUserId) < 0){
            return UserId + "_" + receiveUserId;
        } else {
            return receiveUserId + "_" + UserId;
        }
    }

    // 获取字符串的第一个字符
    public static String getPreviousChar(String str) {
        return str.substring(0, 1);
    }


//    TODO: 下面的这些方法的实现是怎么样的

    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    public static String encodeByMD5(String originString) {
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    public static String getFileSuffix(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return suffix;
    }

    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        return true;
    }

    public static String getImageThumbnail(String fileName) {
        return fileName + Constants.IMAGE_THUMBNAIL_SUFFIX + Constants.IMAGE_SUFFIX;
    }

    public static final String getMeetingNoOrMeetingId() {
        return StringTools.getRandomNumber(Constants.LENGTH_10);
    }

    public static String cleanHtmlTag(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;");
        content = content.replace("\r\n", "<br>");
        content = content.replace("\n", "<br>");
        return content;
    }
    public static String resetMessageContent(String content) {
        content = cleanHtmlTag(content);
        return content;
    }

    public static final String getChatSessionId4User(String[] userIds) {
        Arrays.sort(userIds);
        return encodeByMD5(StringUtils.join(userIds, ""));
    }

    public static final String getChatSessionId4Group(String groupId) {
        return encodeByMD5(groupId);
    }

    public static Date getLocalDateTimeFromLong(Long timeMillis) {
        if (timeMillis == null) {
            return null;
        }
        return new Date(timeMillis);
    }

}
