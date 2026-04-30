package com.anther.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 通话信息 数据库操作接口
 */
public interface CallInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据CallId更新
     */
    Integer updateByCallId(@Param("bean") T t, @Param("callId") String callId);


    /**
     * 根据CallId删除
     */
    Integer deleteByCallId(@Param("callId") String callId);


    /**
     * 根据CallId获取对象
     */
    T selectByCallId(@Param("callId") String callId);
}
