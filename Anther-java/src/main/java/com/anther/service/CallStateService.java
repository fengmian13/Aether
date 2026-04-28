package com.anther.service;

import com.anther.entity.dto.CallInfo;
import com.anther.entity.dto.CallUser;

import java.util.List;
import java.util.Map;

public interface CallStateService {
    boolean isUserBusy(String userId);

    String getCurrentCallId(String userId);

    void saveCallInfo(CallInfo callInfo);

    CallInfo getCallInfo(String callId);

    void saveCallUsers(String callId, List<CallUser> users);

    Map<Object, Object> getCallUsers(String callId);

    void saveUserCurrentCall(String userId, String callId);

    void updateCallUser(String callId, CallUser callUser);

    void clearCallState(String callId, List<String> userIds);
}
