package com.anther.entity.enums;


public enum MessageTypeEnum {
    INIT(0, "", "连接WS获取信息"),
    ADD_MEETING_ROOM(1, "", "加入房间"),
    PEER(2, "", "发送peer"),
    EXIT_MEETING_ROOM(3, "", "退出房间"),
    FINIS_MEETING(4, "", "结束会议"),
    CHAT_TEXT_MESSAGE(5, "", "文本消息"),
    CHAT_MEDIA_MESSAGE(6, "", "媒体消息"),
    CHAT_MEDIA_MESSAGE_UPDATE(7, "", "媒体消息更新"),
    USER_CONTACT_APPLY(8, "", "好友申请消息"),
    INVITE_MEMBER_MEETING(9, "", "邀请入会"),
    FORCE_OFF_LINE(10, "", "强制下线"),
    MEETING_USER_VIDEO_CHANGE(11, "", "用户视频改变"),
    USER_CONTACT_DEAL_WITH(12, "", "处理好友申请"),
    CHAT(13, "", "普通聊天消息"),
    ADD_FRIEND(14, "", "添加好友打招呼消息"),
    GROUP_CREATE(15, "群组已经创建好，可以和好友一起畅聊了", "群创建成功"),
    MEDIA_CHAT(16, "", "媒体文件"),

    ;

    private Integer type;
    private String initMessage;
    private String desc;

    MessageTypeEnum(Integer type, String initMessage, String desc) {
        this.type = type;
        this.initMessage = initMessage;
        this.desc = desc;
    }


    public static MessageTypeEnum getByType(Integer type) {
        for (MessageTypeEnum item : MessageTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getInitMessage() {
        return initMessage;
    }

    public String getDesc() {
        return desc;
    }
}
