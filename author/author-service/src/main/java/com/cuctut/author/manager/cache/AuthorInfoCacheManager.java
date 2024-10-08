package com.cuctut.author.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuctut.author.dao.entity.AuthorInfo;
import com.cuctut.author.dao.mapper.AuthorInfoMapper;
import com.cuctut.author.dto.AuthorInfoDto;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.common.constant.DatabaseConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 作家信息 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/06
 */
@Component
@RequiredArgsConstructor
public class AuthorInfoCacheManager {

    private final AuthorInfoMapper authorInfoMapper;

    /**
     * 查询作家信息，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.AUTHOR_INFO_CACHE_NAME, unless = "#result == null")
    public AuthorInfoDto getAuthor(Long userId) {
        LambdaQueryWrapper<AuthorInfo> query = new LambdaQueryWrapper<AuthorInfo>()
                .eq(AuthorInfo::getUserId, userId)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        AuthorInfo authorInfo = authorInfoMapper.selectOne(query);
        if (Objects.isNull(authorInfo)) return null;

        return AuthorInfoDto.builder()
                .id(authorInfo.getId())
                .penName(authorInfo.getPenName())
                .status(authorInfo.getStatus())
                .build();
    }

    @CacheEvict(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.AUTHOR_INFO_CACHE_NAME)
    public void evictAuthorCache() {
        // 调用此方法自动清除作家信息的缓存
    }

}
