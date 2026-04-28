package com.anther.service;

import com.anther.entity.dto.*;

public interface CallService {
    CallInfo createCall(CallCreateDto dto, TokenUserInfoDto tokenUserInfoDto);

    CallInfo acceptCall(CallAcceptDto dto, TokenUserInfoDto tokenUserInfoDto);

    CallInfo rejectCall(CallRejectDto dto, TokenUserInfoDto tokenUserInfoDto);

    CallInfo cancelCall(CallCancelDto dto, TokenUserInfoDto tokenUserInfoDto);

    CallInfo hangupCall(CallHangupDto dto, TokenUserInfoDto tokenUserInfoDto);

    CallInfo getCallInfo(String callId, String currentUserId);

    RtcServerConfigDto getRtcConfig();

    void handleTimeout(String callId);

    void handleOffline(String userId);
}
