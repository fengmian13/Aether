package com.anther.mappers;

import com.anther.entity.dto.UserGroupContactDto;
import com.anther.entity.query.UserGroupQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  数据库操作接口
 */
public interface UserGroupMapper<T,P> extends BaseMapper<T,P> {

    List<UserGroupContactDto> selectListByQuery(UserGroupQuery param);
}
