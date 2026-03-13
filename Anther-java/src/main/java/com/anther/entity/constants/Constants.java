package com.anther.entity.constants;
import com.anther.entity.enums.UserContactTypeEnum;


public class Constants {
    public static final String ZERO_STR = "0";

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final String SESSION_KEY = "session_key";

    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_12 = 12;
    public static final Integer LENGTH_20 = 20;

    public static final Integer LENGTH_30 = 30;

    public static final String FILE_FOLDER_FILE = "file/";

    public static final String FILE_FOLDER_TEMP = "/temp/";

    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";

    public static final String VIDEO_SUFFIX = ".mp4";

    public static final String DEFAULT_AVATAR = "/user.png";

    public static final String PING = "ping";

    public static final String IMAGE_SUFFIX = ".png";

    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    public static final String[] IMAGE_SUFFIX_LIST = new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"};

    public static final String[] VIDEO_SUFFIX_LIST = new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"};

    public static final Long FILE_SIZE_MB = 1024 * 1024L;


    //用户联系人列表
    public static final String REDIS_KEY_USER_CONTACT = "anther:ws:user:contact:";

    //用户参与的会话列表
    public static final String REDIS_KEY_USER_SESSION = "anther:ws:user:session:";
    /**
     * redis key 相关
     */

    /**
     * 过期时间 1分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;


    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6;

    /**
     * 过期时间 1天
     */
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;

    public static final Integer REDIS_KEY_TOKEN_EXPIRES = REDIS_KEY_EXPIRES_DAY * 2;

    private static final String REDIS_KEY_PREFIX = "anther:";

    public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkcode:";

    public static final String REDIS_KEY_WS_TOKEN = REDIS_KEY_PREFIX + "ws:token:";

    public static final String REDIS_KEY_WS_TOKEN_USERID = REDIS_KEY_PREFIX + "ws:token:userid";

    public static final String REDIS_KEY_WS_USER_HEART_BEAT = REDIS_KEY_PREFIX + "ws:user:heartbeat";

    public static final String REDIS_KEY_MEETING_ROOM = REDIS_KEY_PREFIX + "anther:room:";

    public static final String REDIS_KEY_INVITE_MEMBER = REDIS_KEY_PREFIX + "anther:invite:member:";

    public static final String REDIS_KEY_SYS_SETTING = REDIS_KEY_PREFIX + "sysSetting:";

    public static final String MEETING_NO_PRIFIX = "M";

    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail";

    public static final String VIDEO_CODE_HEVC = "hevc";

    public static final String APP_UPDATE_FOLDER = "/app/";

    public static final String APP_NAME = "AntherSetup.";
    public static final String APP_EXE_SUFFIX = ".exe";

    public static final String MESSAGEING_HANDLE_CHANNEL_KEY = "messaging.handle.channel";

    public static final String MESSAGEING_HANDLE_CHANNEL_REDIS = "redis";

    public static final String MESSAGEING_HANDLE_CHANNEL_RABBITMQ = "rabbitmq";

    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";

    //正则
    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";

    //申请信息模板
    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    //自己退群
    public static final String out_group_TEMPLATE_self = "%s退出了群聊";

    //被管理员踢群
    public static final String out_group_TEMPLATE = "%s被管理员移出了群聊";


}
