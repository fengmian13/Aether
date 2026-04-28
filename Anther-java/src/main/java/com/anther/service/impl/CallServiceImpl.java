package com.anther.service.impl;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.*;
import com.anther.entity.enums.*;
import com.anther.exception.BusinessException;
import com.anther.mappers.CallInfoMapper;
import com.anther.mappers.CallUserMapper;
import com.anther.mappers.UserInfoMapper;
import com.anther.service.CallService;
import com.anther.service.CallSignalService;
import com.anther.service.CallStateService;
import com.anther.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("callService")
public class CallServiceImpl implements CallService {

    @Resource
    private CallInfoMapper<com.anther.entity.po.CallInfo, com.anther.entity.query.CallInfoQuery> callInfoMapper;

    @Resource
    private CallUserMapper<com.anther.entity.po.CallUser, com.anther.entity.query.CallUserQuery> callUserMapper;

    @Resource
    private UserInfoMapper<com.anther.entity.po.UserInfo, com.anther.entity.query.UserInfoQuery> userInfoMapper;

    @Resource
    private CallStateService callStateService;

    @Resource
    private CallSignalService callSignalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CallInfo createCall(CallCreateDto dto, TokenUserInfoDto tokenUserInfoDto) {
        CallTypeEnum callTypeEnum = CallTypeEnum.getByType(dto.getCallType());
        if (callTypeEnum == null) {
            throw new BusinessException("通话类型错误");
        }
        if (tokenUserInfoDto.getUserId().equals(dto.getToUserId())) {
            throw new BusinessException("不能给自己发起通话");
        }
        com.anther.entity.po.UserInfo callee = userInfoMapper.selectByUserId(dto.getToUserId());
        if (callee == null) {
            throw new BusinessException("被叫用户不存在");
        }
        if (callStateService.isUserBusy(tokenUserInfoDto.getUserId()) || callStateService.isUserBusy(dto.getToUserId())) {
            throw new BusinessException("当前用户忙线中");
        }

        long now = System.currentTimeMillis();
        String callId = buildCallId();
        com.anther.entity.po.CallInfo callInfo = new com.anther.entity.po.CallInfo();
        callInfo.setCallId(callId);
        callInfo.setCallerUserId(tokenUserInfoDto.getUserId());
        callInfo.setCallerNickName(tokenUserInfoDto.getNickName());
        callInfo.setCalleeUserId(callee.getUserId());
        callInfo.setCalleeNickName(callee.getNickName());
        callInfo.setCallType(dto.getCallType());
        callInfo.setStatus(CallStatusEnum.INIT.getStatus());
        callInfo.setStartTime(now);
        callInfo.setCreateTime(now);
        callInfo.setUpdateTime(now);
        callInfoMapper.insert(callInfo);

        List<com.anther.entity.po.CallUser> callUsers = new ArrayList<>();
        callUsers.add(buildCallUser(callId, tokenUserInfoDto.getUserId(), tokenUserInfoDto.getNickName(),
                CallUserRoleEnum.CALLER.getRole(), CallJoinStatusEnum.NOT_JOINED.getStatus(),
                1, CallTypeEnum.AUDIO == callTypeEnum ? 0 : 1, now));
        callUsers.add(buildCallUser(callId, callee.getUserId(), callee.getNickName(),
                CallUserRoleEnum.CALLEE.getRole(), CallJoinStatusEnum.NOT_JOINED.getStatus(),
                1, CallTypeEnum.AUDIO == callTypeEnum ? 0 : 1, now));
        callUserMapper.insertBatch(callUsers);

        CallInfo result = buildCallInfo(callInfo, callUsers);
        cacheCurrentCall(result);

        CallSignalDto signalDto = baseSignal(result, SignalEventEnum.CALL_INVITE, tokenUserInfoDto.getUserId(), callee.getUserId());
        callSignalService.sendSignal(signalDto);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CallInfo acceptCall(CallAcceptDto dto, TokenUserInfoDto tokenUserInfoDto) {
        com.anther.entity.po.CallInfo callInfo = requireCall(dto.getCallId());
        if (!tokenUserInfoDto.getUserId().equals(callInfo.getCalleeUserId())) {
            throw new BusinessException("只有被叫可以接听");
        }
        ensureInit(callInfo);
        long now = System.currentTimeMillis();

        com.anther.entity.po.CallInfo updateInfo = new com.anther.entity.po.CallInfo();
        updateInfo.setStatus(CallStatusEnum.ACCEPTED.getStatus());
        updateInfo.setAnswerTime(now);
        updateInfo.setUpdateTime(now);
        callInfoMapper.updateByCallId(updateInfo, callInfo.getCallId());
        callInfo.setStatus(CallStatusEnum.ACCEPTED.getStatus());
        callInfo.setAnswerTime(now);
        callInfo.setUpdateTime(now);

        markAcceptedUsers(callInfo.getCallId(), now);
        CallInfo result = getFreshCallInfo(callInfo.getCallId());
        cacheCurrentCall(result);

        CallSignalDto signalDto = baseSignal(result, SignalEventEnum.CALL_ACCEPT, tokenUserInfoDto.getUserId(), callInfo.getCallerUserId());
        callSignalService.sendSignal(signalDto);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CallInfo rejectCall(CallRejectDto dto, TokenUserInfoDto tokenUserInfoDto) {
        com.anther.entity.po.CallInfo callInfo = requireCall(dto.getCallId());
        if (!tokenUserInfoDto.getUserId().equals(callInfo.getCalleeUserId())) {
            throw new BusinessException("只有被叫可以拒绝");
        }
        ensureInit(callInfo);
        finishCall(callInfo, CallStatusEnum.REJECTED, tokenUserInfoDto.getUserId(), "reject");
        CallInfo result = getFreshCallInfo(callInfo.getCallId());
        callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_REJECT, tokenUserInfoDto.getUserId(), callInfo.getCallerUserId()));
        clearCurrentCall(result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CallInfo cancelCall(CallCancelDto dto, TokenUserInfoDto tokenUserInfoDto) {
        com.anther.entity.po.CallInfo callInfo = requireCall(dto.getCallId());
        if (!tokenUserInfoDto.getUserId().equals(callInfo.getCallerUserId())) {
            throw new BusinessException("只有主叫可以取消");
        }
        ensureInit(callInfo);
        finishCall(callInfo, CallStatusEnum.CANCELED, tokenUserInfoDto.getUserId(), "cancel");
        CallInfo result = getFreshCallInfo(callInfo.getCallId());
        callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_CANCEL, tokenUserInfoDto.getUserId(), callInfo.getCalleeUserId()));
        clearCurrentCall(result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CallInfo hangupCall(CallHangupDto dto, TokenUserInfoDto tokenUserInfoDto) {
        com.anther.entity.po.CallInfo callInfo = requireCall(dto.getCallId());
        if (!isCallParticipant(callInfo, tokenUserInfoDto.getUserId())) {
            throw new BusinessException("当前用户不属于该通话");
        }
        if (!CallStatusEnum.ACCEPTED.getStatus().equals(callInfo.getStatus())) {
            throw new BusinessException("当前状态不允许挂断");
        }
        finishCall(callInfo, CallStatusEnum.HANGUP, tokenUserInfoDto.getUserId(), "hangup");
        CallInfo result = getFreshCallInfo(callInfo.getCallId());
        String peerUserId = getPeerUserId(callInfo, tokenUserInfoDto.getUserId());
        callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_HANGUP, tokenUserInfoDto.getUserId(), peerUserId));
        clearCurrentCall(result);
        return result;
    }

    @Override
    public CallInfo getCallInfo(String callId, String currentUserId) {
        com.anther.entity.po.CallInfo callInfo = requireCall(callId);
        if (!isCallParticipant(callInfo, currentUserId)) {
            throw new BusinessException("无权查看该通话");
        }
        return getFreshCallInfo(callId);
    }

    @Override
    public RtcServerConfigDto getRtcConfig() {
        RtcServerConfigDto dto = new RtcServerConfigDto();
        dto.setStunUrls(Arrays.asList("stun:stun.l.google.com:19302", "stun:stun1.l.google.com:19302"));
        dto.setTurnUrls(Collections.emptyList());
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeout(String callId) {
        com.anther.entity.po.CallInfo callInfo = callInfoMapper.selectByCallId(callId);
        if (callInfo == null || !CallStatusEnum.INIT.getStatus().equals(callInfo.getStatus())) {
            return;
        }
        if (System.currentTimeMillis() - callInfo.getStartTime() < Constants.CALL_RING_TIMEOUT_SECONDS * 1000L) {
            return;
        }
        finishCall(callInfo, CallStatusEnum.TIMEOUT, null, "timeout");
        CallInfo result = getFreshCallInfo(callId);
        callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_HANGUP, callInfo.getCalleeUserId(), callInfo.getCallerUserId(), "timeout"));
        clearCurrentCall(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOffline(String userId) {
        String callId = callStateService.getCurrentCallId(userId);
        if (StringTools.isEmpty(callId)) {
            return;
        }
        com.anther.entity.po.CallInfo callInfo = callInfoMapper.selectByCallId(callId);
        if (callInfo == null) {
            return;
        }
        if (CallStatusEnum.ACCEPTED.getStatus().equals(callInfo.getStatus())) {
            finishCall(callInfo, CallStatusEnum.HANGUP, userId, "offline");
            CallInfo result = getFreshCallInfo(callId);
            callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_HANGUP, userId, getPeerUserId(callInfo, userId), "offline"));
            clearCurrentCall(result);
            return;
        }
        if (CallStatusEnum.INIT.getStatus().equals(callInfo.getStatus())) {
            if (userId.equals(callInfo.getCallerUserId())) {
                finishCall(callInfo, CallStatusEnum.CANCELED, userId, "offline");
                CallInfo result = getFreshCallInfo(callId);
                callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_CANCEL, userId, callInfo.getCalleeUserId(), "offline"));
                clearCurrentCall(result);
            } else if (userId.equals(callInfo.getCalleeUserId())) {
                finishCall(callInfo, CallStatusEnum.REJECTED, userId, "offline");
                CallInfo result = getFreshCallInfo(callId);
                callSignalService.sendSignal(baseSignal(result, SignalEventEnum.CALL_REJECT, userId, callInfo.getCallerUserId(), "offline"));
                clearCurrentCall(result);
            }
        }
    }

    private void markAcceptedUsers(String callId, long now) {
        com.anther.entity.po.CallUser update = new com.anther.entity.po.CallUser();
        update.setJoinStatus(CallJoinStatusEnum.JOINED.getStatus());
        update.setJoinTime(now);
        update.setUpdateTime(now);
        com.anther.entity.query.CallUserQuery query = new com.anther.entity.query.CallUserQuery();
        query.setCallId(callId);
        callUserMapper.updateByParam(update, query);
    }

    private void finishCall(com.anther.entity.po.CallInfo callInfo, CallStatusEnum targetStatus, String operatorUserId, String reason) {
        long now = System.currentTimeMillis();
        com.anther.entity.po.CallInfo updateInfo = new com.anther.entity.po.CallInfo();
        updateInfo.setStatus(targetStatus.getStatus());
        updateInfo.setEndTime(now);
        updateInfo.setUpdateTime(now);
        callInfoMapper.updateByCallId(updateInfo, callInfo.getCallId());

        com.anther.entity.po.CallUser updateUser = new com.anther.entity.po.CallUser();
        updateUser.setJoinStatus(CallJoinStatusEnum.LEFT.getStatus());
        updateUser.setRtcStatus(reason);
        updateUser.setLeaveTime(now);
        updateUser.setUpdateTime(now);
        com.anther.entity.query.CallUserQuery query = new com.anther.entity.query.CallUserQuery();
        query.setCallId(callInfo.getCallId());
        callUserMapper.updateByParam(updateUser, query);

        if (operatorUserId != null) {
            com.anther.entity.po.CallUser self = new com.anther.entity.po.CallUser();
            self.setRtcStatus(reason);
            self.setLeaveTime(now);
            self.setUpdateTime(now);
            callUserMapper.updateByCallIdAndUserId(self, callInfo.getCallId(), operatorUserId);
        }
    }

    private CallSignalDto baseSignal(CallInfo callInfo, SignalEventEnum eventEnum, String fromUserId, String toUserId) {
        return baseSignal(callInfo, eventEnum, fromUserId, toUserId, null);
    }

    private CallSignalDto baseSignal(CallInfo callInfo, SignalEventEnum eventEnum, String fromUserId, String toUserId, String reason) {
        CallSignalDto signalDto = new CallSignalDto();
        signalDto.setEvent(eventEnum.getEvent());
        signalDto.setCallId(callInfo.getCallId());
        signalDto.setFromUserId(fromUserId);
        signalDto.setToUserId(toUserId);
        signalDto.setCallType(callInfo.getCallType());
        signalDto.setTimestamp(System.currentTimeMillis());
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", callInfo.getStatus());
        if (!StringTools.isEmpty(reason)) {
            payload.put("reason", reason);
        }
        signalDto.setPayload(payload);
        return signalDto;
    }

    private void cacheCurrentCall(CallInfo callInfo) {
        callStateService.saveCallInfo(callInfo);
        callStateService.saveCallUsers(callInfo.getCallId(), callInfo.getUsers());
        for (CallUser user : callInfo.getUsers()) {
            callStateService.saveUserCurrentCall(user.getUserId(), callInfo.getCallId());
        }
    }

    private void clearCurrentCall(CallInfo callInfo) {
        callStateService.clearCallState(callInfo.getCallId(), Arrays.asList(callInfo.getCallerUserId(), callInfo.getCalleeUserId()));
    }

    private com.anther.entity.po.CallInfo requireCall(String callId) {
        com.anther.entity.po.CallInfo callInfo = callInfoMapper.selectByCallId(callId);
        if (callInfo == null) {
            throw new BusinessException("通话不存在");
        }
        return callInfo;
    }

    private void ensureInit(com.anther.entity.po.CallInfo callInfo) {
        if (!CallStatusEnum.INIT.getStatus().equals(callInfo.getStatus())) {
            throw new BusinessException("当前通话状态不允许此操作");
        }
    }

    private boolean isCallParticipant(com.anther.entity.po.CallInfo callInfo, String userId) {
        return userId.equals(callInfo.getCallerUserId()) || userId.equals(callInfo.getCalleeUserId());
    }

    private String getPeerUserId(com.anther.entity.po.CallInfo callInfo, String userId) {
        return userId.equals(callInfo.getCallerUserId()) ? callInfo.getCalleeUserId() : callInfo.getCallerUserId();
    }

    private String buildCallId() {
        return "C" + System.currentTimeMillis() + StringTools.getRandomNumber(6);
    }

    private com.anther.entity.po.CallUser buildCallUser(String callId, String userId, String nickName,
                                                        Integer role, Integer joinStatus, Integer audioEnabled,
                                                        Integer videoEnabled, long now) {
        com.anther.entity.po.CallUser callUser = new com.anther.entity.po.CallUser();
        callUser.setCallId(callId);
        callUser.setUserId(userId);
        callUser.setNickName(nickName);
        callUser.setRole(role);
        callUser.setJoinStatus(joinStatus);
        callUser.setAudioEnabled(audioEnabled);
        callUser.setVideoEnabled(videoEnabled);
        callUser.setRtcStatus("init");
        callUser.setCreateTime(now);
        callUser.setUpdateTime(now);
        return callUser;
    }

    private CallInfo getFreshCallInfo(String callId) {
        com.anther.entity.po.CallInfo callInfo = requireCall(callId);
        List<com.anther.entity.po.CallUser> callUsers = callUserMapper.selectByCallId(callId);
        return buildCallInfo(callInfo, callUsers);
    }

    private CallInfo buildCallInfo(com.anther.entity.po.CallInfo callInfo, List<com.anther.entity.po.CallUser> callUsers) {
        CallInfo dto = new CallInfo();
        dto.setCallId(callInfo.getCallId());
        dto.setCallerUserId(callInfo.getCallerUserId());
        dto.setCallerNickName(callInfo.getCallerNickName());
        dto.setCalleeUserId(callInfo.getCalleeUserId());
        dto.setCalleeNickName(callInfo.getCalleeNickName());
        dto.setCallType(callInfo.getCallType());
        dto.setStatus(callInfo.getStatus());
        dto.setStartTime(callInfo.getStartTime());
        dto.setAnswerTime(callInfo.getAnswerTime());
        dto.setEndTime(callInfo.getEndTime());
        List<CallUser> users = new ArrayList<>();
        for (com.anther.entity.po.CallUser item : callUsers) {
            CallUser user = new CallUser();
            user.setCallId(item.getCallId());
            user.setUserId(item.getUserId());
            user.setNickName(item.getNickName());
            user.setRole(item.getRole());
            user.setJoinStatus(item.getJoinStatus());
            user.setAudioEnabled(item.getAudioEnabled());
            user.setVideoEnabled(item.getVideoEnabled());
            user.setRtcStatus(item.getRtcStatus());
            user.setJoinTime(item.getJoinTime());
            user.setLeaveTime(item.getLeaveTime());
            users.add(user);
        }
        dto.setUsers(users);
        return dto;
    }
}
