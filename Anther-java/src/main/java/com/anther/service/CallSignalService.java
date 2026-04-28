package com.anther.service;

import com.anther.entity.dto.CallSignalDto;
import com.anther.entity.dto.TokenUserInfoDto;

public interface CallSignalService {
    void handleSignal(TokenUserInfoDto tokenUserInfoDto, CallSignalDto signalDto);

    void sendSignal(CallSignalDto signalDto);

    void handleUserOffline(String userId);
}
