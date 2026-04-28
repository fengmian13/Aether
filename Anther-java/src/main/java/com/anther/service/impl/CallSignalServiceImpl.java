package com.anther.service.impl;

import com.anther.entity.dto.CallSignalDto;
import com.anther.entity.dto.MessageSendDto;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.CallStatusEnum;
import com.anther.entity.enums.MessageTypeEnum;
import com.anther.entity.enums.SignalEventEnum;
import com.anther.exception.BusinessException;
import com.anther.mappers.CallInfoMapper;
import com.anther.mappers.CallUserMapper;
import com.anther.service.CallSignalService;
import com.anther.service.CallStateService;
import com.anther.utils.StringTools;
import com.anther.websocket.message.MessageHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("callSignalService")
public class CallSignalServiceImpl implements CallSignalService {

    @Resource
    private CallInfoMapper<com.anther.entity.po.CallInfo, com.anther.entity.query.CallInfoQuery> callInfoMapper;

    @Resource
    private CallUserMapper<com.anther.entity.po.CallUser, com.anther.entity.query.CallUserQuery> callUserMapper;

    @Resource
    private CallStateService callStateService;

    @Resource
    private MessageHandler messageHandler;

    @Override
    public void handleSignal(TokenUserInfoDto tokenUserInfoDto, CallSignalDto signalDto) {
        if (signalDto == null || StringTools.isEmpty(signalDto.getCallId()) || StringTools.isEmpty(signalDto.getEvent())) {
            throw new BusinessException("通话信令参数错误");
        }
        SignalEventEnum eventEnum = SignalEventEnum.getByEvent(signalDto.getEvent());
        if (eventEnum == null) {
            throw new BusinessException("不支持的通话信令事件");
        }
        if (!(SignalEventEnum.WEBRTC_OFFER == eventEnum
                || SignalEventEnum.WEBRTC_ANSWER == eventEnum
                || SignalEventEnum.WEBRTC_ICE_CANDIDATE == eventEnum
                || SignalEventEnum.MEDIA_AUDIO_TOGGLE == eventEnum
                || SignalEventEnum.MEDIA_VIDEO_TOGGLE == eventEnum)) {
            throw new BusinessException("该事件不允许通过 websocket 直接处理");
        }

        String currentUserId = tokenUserInfoDto.getUserId();
        com.anther.entity.po.CallInfo callInfo = callInfoMapper.selectByCallId(signalDto.getCallId());
        if (callInfo == null) {
            throw new BusinessException("通话不存在");
        }
        if (!CallStatusEnum.ACCEPTED.getStatus().equals(callInfo.getStatus())) {
            throw new BusinessException("当前通话状态不允许发送 WebRTC 信令");
        }
        com.anther.entity.po.CallUser fromUser = callUserMapper.selectByCallIdAndUserId(signalDto.getCallId(), currentUserId);
        if (fromUser == null) {
            throw new BusinessException("当前用户不属于该通话");
        }

        String peerUserId = callInfo.getCallerUserId().equals(currentUserId) ? callInfo.getCalleeUserId() : callInfo.getCallerUserId();
        if (!StringTools.isEmpty(signalDto.getToUserId()) && !peerUserId.equals(signalDto.getToUserId())) {
            throw new BusinessException("信令目标用户非法");
        }
        com.anther.entity.po.CallUser targetUser = callUserMapper.selectByCallIdAndUserId(signalDto.getCallId(), peerUserId);
        if (targetUser == null) {
            throw new BusinessException("通话对端不存在");
        }

        signalDto.setFromUserId(currentUserId);
        signalDto.setToUserId(peerUserId);
        signalDto.setCallType(callInfo.getCallType());
        signalDto.setTimestamp(System.currentTimeMillis());

        if (SignalEventEnum.MEDIA_AUDIO_TOGGLE == eventEnum || SignalEventEnum.MEDIA_VIDEO_TOGGLE == eventEnum) {
            updateMediaState(signalDto, fromUser, eventEnum);
        }
        sendSignal(signalDto);
    }

    private void updateMediaState(CallSignalDto signalDto, com.anther.entity.po.CallUser callUser, SignalEventEnum eventEnum) {
        Map<String, Object> payload = signalDto.getPayload() == null ? new HashMap<>() : signalDto.getPayload();
        Integer enabled = toEnabled(payload.get("enabled"));
        com.anther.entity.po.CallUser updateUser = new com.anther.entity.po.CallUser();
        updateUser.setUpdateTime(System.currentTimeMillis());
        com.anther.entity.dto.CallUser cacheUser = convertCallUser(callUser);
        if (SignalEventEnum.MEDIA_AUDIO_TOGGLE == eventEnum) {
            updateUser.setAudioEnabled(enabled);
            cacheUser.setAudioEnabled(enabled);
        } else {
            updateUser.setVideoEnabled(enabled);
            cacheUser.setVideoEnabled(enabled);
        }
        callUserMapper.updateByCallIdAndUserId(updateUser, callUser.getCallId(), callUser.getUserId());
        callStateService.updateCallUser(callUser.getCallId(), cacheUser);
        signalDto.setPayload(payload);
    }

    private Integer toEnabled(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() > 0 ? 1 : 0;
        }
        return "true".equalsIgnoreCase(String.valueOf(value)) || "1".equals(String.valueOf(value)) ? 1 : 0;
    }

    @Override
    public void sendSignal(CallSignalDto signalDto) {
        if (signalDto == null || StringTools.isEmpty(signalDto.getToUserId())) {
            return;
        }
        MessageSendDto<CallSignalDto> messageSendDto = new MessageSendDto<>();
        messageSendDto.setMessageType(MessageTypeEnum.CALL_SIGNAL.getType());
        messageSendDto.setContactId(signalDto.getToUserId());
        messageSendDto.setSendUserId(signalDto.getFromUserId());
        messageSendDto.setSendTime(signalDto.getTimestamp() == null ? System.currentTimeMillis() : signalDto.getTimestamp());
        messageSendDto.setExtendData(signalDto);
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    public void handleUserOffline(String userId) {
        // offline lifecycle is finalized by CallService
    }

    private com.anther.entity.dto.CallUser convertCallUser(com.anther.entity.po.CallUser callUser) {
        com.anther.entity.dto.CallUser dto = new com.anther.entity.dto.CallUser();
        dto.setCallId(callUser.getCallId());
        dto.setUserId(callUser.getUserId());
        dto.setNickName(callUser.getNickName());
        dto.setRole(callUser.getRole());
        dto.setJoinStatus(callUser.getJoinStatus());
        dto.setAudioEnabled(callUser.getAudioEnabled());
        dto.setVideoEnabled(callUser.getVideoEnabled());
        dto.setRtcStatus(callUser.getRtcStatus());
        dto.setJoinTime(callUser.getJoinTime());
        dto.setLeaveTime(callUser.getLeaveTime());
        return dto;
    }
}
