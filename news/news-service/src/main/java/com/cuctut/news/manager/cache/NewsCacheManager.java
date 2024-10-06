package com.cuctut.news.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.common.constant.DatabaseConsts;
import com.cuctut.news.dao.entity.NewsInfo;
import com.cuctut.news.dao.mapper.NewsInfoMapper;
import com.cuctut.news.dto.resp.NewsInfoRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 新闻 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@RequiredArgsConstructor
public class NewsCacheManager {

    private final NewsInfoMapper newsInfoMapper;

    /**
     * 最新新闻列表查询，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
        value = CacheConsts.LATEST_NEWS_CACHE_NAME)
    public List<NewsInfoRespDto> listLatestNews() {
        // 从新闻信息表中查询出最新发布的两条新闻
        LambdaQueryWrapper<NewsInfo> query = new LambdaQueryWrapper<NewsInfo>()
                .orderByDesc(NewsInfo::getCreateTime)
                .last(DatabaseConsts.SqlEnum.LIMIT_2.getSql());
        return newsInfoMapper.selectList(query).stream().map(
                v -> NewsInfoRespDto.builder()
                        .id(v.getId())
                        .categoryId(v.getCategoryId())
                        .categoryName(v.getCategoryName())
                        .title(v.getTitle())
                        .sourceName(v.getSourceName())
                        .updateTime(v.getUpdateTime())
                        .build()
        ).toList();
    }

}
