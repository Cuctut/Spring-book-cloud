package com.cuctut.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuctut.common.constant.DatabaseConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.news.dao.entity.NewsContent;
import com.cuctut.news.dao.entity.NewsInfo;
import com.cuctut.news.dao.mapper.NewsContentMapper;
import com.cuctut.news.dao.mapper.NewsInfoMapper;
import com.cuctut.news.dto.resp.NewsInfoRespDto;
import com.cuctut.news.manager.cache.NewsCacheManager;
import com.cuctut.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新闻模块 服务实现类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsCacheManager newsCacheManager;

    private final NewsInfoMapper newsInfoMapper;

    private final NewsContentMapper newsContentMapper;

    @Override
    public RestResp<List<NewsInfoRespDto>> listLatestNews() {
        return RestResp.ok(newsCacheManager.listLatestNews());
    }

    @Override
    public RestResp<NewsInfoRespDto> getNews(Long id) {
        NewsInfo newsInfo = newsInfoMapper.selectById(id);
        LambdaQueryWrapper<NewsContent> query = new LambdaQueryWrapper<NewsContent>()
                .eq(NewsContent::getNewsId, id)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        NewsContent newsContent = newsContentMapper.selectOne(query);
        return RestResp.ok(
                NewsInfoRespDto.builder()
                        .title(newsInfo.getTitle())
                        .sourceName(newsInfo.getSourceName())
                        .updateTime(newsInfo.getUpdateTime())
                        .content(newsContent.getContent())
                        .build()
        );
    }
}
