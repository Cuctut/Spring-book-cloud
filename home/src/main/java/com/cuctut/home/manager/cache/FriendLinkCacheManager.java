package com.cuctut.home.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.home.dao.entity.HomeFriendLink;
import com.cuctut.home.dao.mapper.HomeFriendLinkMapper;
import com.cuctut.home.dto.resp.HomeFriendLinkRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 友情链接 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@RequiredArgsConstructor
public class FriendLinkCacheManager {

    private final HomeFriendLinkMapper friendLinkMapper;

    /**
     * 友情链接列表查询，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.HOME_FRIEND_LINK_CACHE_NAME)
    public List<HomeFriendLinkRespDto> listFriendLinks() {
        LambdaQueryWrapper<HomeFriendLink> query = new LambdaQueryWrapper<HomeFriendLink>()
                .orderByAsc(HomeFriendLink::getLinkName);
        return friendLinkMapper.selectList(query).stream().map(
                v -> {
                    HomeFriendLinkRespDto respDto = new HomeFriendLinkRespDto();
                    respDto.setLinkName(v.getLinkName());
                    respDto.setLinkUrl(v.getLinkUrl());
                    return respDto;
                }
            ).toList();
    }

}
