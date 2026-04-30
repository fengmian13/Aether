package com.anther.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通话成员 数据库操作接口
 */
public interface CallUserMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据CallIdAndUserId更新
     */
    Integer updateByCallIdAndUserId(@Param("bean") T t, @Param("callId") String callId, @Param("userId") String userId);


    /**
     * 根据CallIdAndUserId删除
     */
    Integer deleteByCallIdAndUserId(@Param("callId") String callId, @Param("userId") String userId);


    /**
     * 根据CallIdAndUserId获取对象
     */
    T selectByCallIdAndUserId(@Param("callId") String callId, @Param("userId") String userId);


    /**
     * 根据CallId获取列表
     */
    List<T> selectByCallId(@Param("callId") String callId);
}
