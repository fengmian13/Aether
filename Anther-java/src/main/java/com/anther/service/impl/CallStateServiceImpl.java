package com.anther.service.impl;

import com.anther.entity.constants.Constants;
import com.anther.entity.dto.CallInfo;
import com.anther.entity.dto.CallUser;
import com.anther.redis.RedisUtils;
import com.anther.service.CallStateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("callStateService")
public class CallStateServiceImpl implements CallStateService {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean isUserBusy(String userId) {
        return getCurrentCallId(userId) != null;
    }

    @Override
    public String getCurrentCallId(String userId) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CALL_USER_CURRENT + userId + ":current");
    }

    @Override
    public void saveCallInfo(CallInfo callInfo) {
        redisUtils.setex(Constants.REDIS_KEY_CALL_INFO + callInfo.getCallId(), callInfo, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    @Override
    public CallInfo getCallInfo(String callId) {
        return (CallInfo) redisUtils.get(Constants.REDIS_KEY_CALL_INFO + callId);
    }

    @Override
    public void saveCallUsers(String callId, List<CallUser> users) {
        String key = Constants.REDIS_KEY_CALL_USERS + callId;
        for (CallUser user : users) {
            redisUtils.hset(key, user.getUserId(), user);
        }
        redisUtils.expire(key, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    @Override
    public Map<Object, Object> getCallUsers(String callId) {
        return redisUtils.hgetall(Constants.REDIS_KEY_CALL_USERS + callId);
    }

    @Override
    public void saveUserCurrentCall(String userId, String callId) {
        redisUtils.setex(Constants.REDIS_KEY_CALL_USER_CURRENT + userId + ":current", callId, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    @Override
    public void updateCallUser(String callId, CallUser callUser) {
        String key = Constants.REDIS_KEY_CALL_USERS + callId;
        redisUtils.hset(key, callUser.getUserId(), callUser);
        redisUtils.expire(key, Constants.REDIS_KEY_TOKEN_EXPIRES);
    }

    @Override
    public void clearCallState(String callId, List<String> userIds) {
        redisUtils.delete(Constants.REDIS_KEY_CALL_INFO + callId, Constants.REDIS_KEY_CALL_USERS + callId);
        if (userIds != null) {
            for (String userId : userIds) {
                redisUtils.delete(Constants.REDIS_KEY_CALL_USER_CURRENT + userId + ":current");
            }
        }
    }
}
