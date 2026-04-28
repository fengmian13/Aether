package com.anther.entity.enums;

public enum SignalEventEnum {
    CALL_INVITE("CALL_INVITE"),
    CALL_ACCEPT("CALL_ACCEPT"),
    CALL_REJECT("CALL_REJECT"),
    CALL_CANCEL("CALL_CANCEL"),
    CALL_HANGUP("CALL_HANGUP"),
    WEBRTC_OFFER("WEBRTC_OFFER"),
    WEBRTC_ANSWER("WEBRTC_ANSWER"),
    WEBRTC_ICE_CANDIDATE("WEBRTC_ICE_CANDIDATE"),
    MEDIA_AUDIO_TOGGLE("MEDIA_AUDIO_TOGGLE"),
    MEDIA_VIDEO_TOGGLE("MEDIA_VIDEO_TOGGLE");

    private final String event;

    SignalEventEnum(String event) {
        this.event = event;
    }

    public static SignalEventEnum getByEvent(String event) {
        for (SignalEventEnum item : values()) {
            if (item.getEvent().equals(event)) {
                return item;
            }
        }
        return null;
    }

    public String getEvent() {
        return event;
    }
}
