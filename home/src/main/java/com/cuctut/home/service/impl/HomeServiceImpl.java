package com.cuctut.home.service.impl;

import com.cuctut.common.resp.RestResp;
import com.cuctut.home.dto.resp.HomeBookRespDto;
import com.cuctut.home.dto.resp.HomeFriendLinkRespDto;
import com.cuctut.home.manager.cache.FriendLinkCacheManager;
import com.cuctut.home.manager.cache.HomeBookCacheManager;
import com.cuctut.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 首页模块 服务实现类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeBookCacheManager homeBookCacheManager;

    private final FriendLinkCacheManager friendLinkCacheManager;

    @Override
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        List<HomeBookRespDto> list = homeBookCacheManager.listHomeBooks();
        if(CollectionUtils.isEmpty(list)) homeBookCacheManager.evictCache();
        return RestResp.ok(list);
    }

    @Override
    public RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks() {
        return RestResp.ok(friendLinkCacheManager.listFriendLinks());
    }
}
