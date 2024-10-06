package com.cuctut.user.manager.cache;

import com.cuctut.common.constant.CacheConsts;
import com.cuctut.user.dao.entity.UserInfo;
import com.cuctut.user.dao.mapper.UserInfoMapper;
import com.cuctut.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户信息 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/05
 */
@Component
@RequiredArgsConstructor
public class UserInfoCacheManager {

    private final UserInfoMapper userInfoMapper;

    /**
     * 查询用户信息，并放入redis缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (Objects.isNull(userInfo)) return null;
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .status(userInfo.getStatus())
                .build();
    }


}
