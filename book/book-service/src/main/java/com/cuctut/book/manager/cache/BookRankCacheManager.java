package com.cuctut.book.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuctut.book.dao.entity.BookInfo;
import com.cuctut.book.dao.mapper.BookInfoMapper;
import com.cuctut.book.dto.resp.BookRankRespDto;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.common.constant.DatabaseConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小说排行榜 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/04
 */
@Component
@RequiredArgsConstructor
public class BookRankCacheManager {

    private final BookInfoMapper bookInfoMapper;

    /**
     * 查询小说点击榜列表，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.BOOK_VISIT_RANK_CACHE_NAME)
    public List<BookRankRespDto> listVisitRankBooks() {
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<>();
        query.orderByDesc(BookInfo::getVisitCount);
        return listRankBooks(query);
    }

    /**
     * 查询小说新书榜列表，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
        value = CacheConsts.BOOK_NEWEST_RANK_CACHE_NAME)
    public List<BookRankRespDto> listNewestRankBooks() {
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<>();
        query.orderByDesc(BookInfo::getCreateTime);
        return listRankBooks(query);
    }

    /**
     * 查询小说更新榜列表，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
        value = CacheConsts.BOOK_UPDATE_RANK_CACHE_NAME)
    public List<BookRankRespDto> listUpdateRankBooks() {
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<>();
        query.orderByDesc(BookInfo::getUpdateTime);
        return listRankBooks(query);
    }

    private List<BookRankRespDto> listRankBooks(LambdaQueryWrapper<BookInfo> query) {
        query.gt(BookInfo::getWordCount, 0).last(DatabaseConsts.SqlEnum.LIMIT_30.getSql());
        return bookInfoMapper.selectList(query).stream().map(v ->
            BookRankRespDto.builder()
                    .id(v.getId())
                    .categoryId(v.getCategoryId())
                    .lastChapterName(v.getCategoryName())
                    .bookName(v.getBookName())
                    .authorName(v.getAuthorName())
                    .picUrl(v.getPicUrl())
                    .bookDesc(v.getBookDesc())
                    .lastChapterName(v.getLastChapterName())
                    .lastChapterUpdateTime(v.getLastChapterUpdateTime())
                    .wordCount(v.getWordCount())
                    .build()
        ).toList();
    }

}
