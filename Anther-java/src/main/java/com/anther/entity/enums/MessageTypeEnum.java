package com.anther.entity.enums;

public enum MessageTypeEnum {
    INIT(0, "", "ws init"),
    ADD_FRIEND(1, "", "add friend"),
    CHAT(2, "", "chat"),
    GROUP_CREATE(3, "group created", "group create"),
    CONTACT_APPLY(4, "", "contact apply"),
    MEDIA_CHAT(5, "", "media chat"),
    FILE_UPLOAD(6, "", "file upload"),
    FORCE_OFF_LINE(7, "", "force off line"),
    DISSOLUTION_GROUP(8, "group dissolved", "dissolution group"),
    ADD_GROUP(9, "%s joined group", "add group"),
    CONTACT_NAME_UPDATE(10, "", "contact name update"),
    LEAVE_GROUP(11, "%s left group", "leave group"),
    REMOVE_GROUP(12, "%s removed from group", "remove group"),
    ADD_FRIEND_SELF(13, "", "add friend self"),
    FINIS_MEETING(14, "", "finish meeting"),
    CHAT_TEXT_MESSAGE(15, "", "chat text"),
    CHAT_MEDIA_MESSAGE(16, "", "chat media"),
    CHAT_MEDIA_MESSAGE_UPDATE(17, "", "chat media update"),
    USER_CONTACT_APPLY(18, "", "user contact apply"),
    INVITE_MEMBER_MEETING(19, "", "invite member meeting"),
    MEETING_USER_VIDEO_CHANGE(20, "", "meeting user video change"),
    USER_CONTACT_DEAL_WITH(21, "", "user contact deal with"),
    EXIT_MEETING_ROOM(22, "", "exit meeting room"),
    CALL_SIGNAL(23, "", "call signal");

    private final Integer type;
    private final String initMessage;
    private final String desc;

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
