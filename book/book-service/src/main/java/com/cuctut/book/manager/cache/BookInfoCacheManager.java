package com.cuctut.book.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuctut.book.dao.entity.BookChapter;
import com.cuctut.book.dao.entity.BookInfo;
import com.cuctut.book.dao.mapper.BookChapterMapper;
import com.cuctut.book.dao.mapper.BookInfoMapper;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.common.constant.DatabaseConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookInfoCacheManager {

    private final BookInfoMapper bookInfoMapper;
    private final BookChapterMapper bookChapterMapper;

    /**
     * 根据 bookId 查询小说信息并缓存
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
            value = CacheConsts.BOOK_INFO_CACHE_NAME)
    public BookInfoRespDto getBookInfoById(Long bookId) {
        log.debug("Method {} is called, No caching is used.", Thread.currentThread().getStackTrace()[1].getMethodName());
        BookInfo bookInfo = bookInfoMapper.selectById(bookId);
        LambdaQueryWrapper<BookChapter> query = new LambdaQueryWrapper<BookChapter>()
                .eq(BookChapter::getBookId, bookId)
                .orderByAsc(BookChapter::getChapterNum)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        BookChapter firstChapter = bookChapterMapper.selectOne(query);
        return BookInfoRespDto.builder()
                .id(bookInfo.getId())
                .bookName(bookInfo.getBookName())
                .bookDesc(bookInfo.getBookDesc())
                .bookStatus(bookInfo.getBookStatus())
                .authorId(bookInfo.getAuthorId())
                .authorName(bookInfo.getAuthorName())
                .categoryId(bookInfo.getCategoryId())
                .categoryName(bookInfo.getCategoryName())
                .commentCount(bookInfo.getCommentCount())
                .firstChapterId(firstChapter.getId())
                .lastChapterId(bookInfo.getLastChapterId())
                .picUrl(bookInfo.getPicUrl())
                .visitCount(bookInfo.getVisitCount())
                .wordCount(bookInfo.getWordCount())
                .build();
    }

    /**
     * 清除小说信息的缓存
     * @param bookId 清除 Id 为 bookId 的小说缓存
     */
    @CacheEvict(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
            value = CacheConsts.BOOK_INFO_CACHE_NAME)
    public void evictBookInfoCache(Long bookId) {
        // 调用此方法自动清除小说信息的缓存
    }

    /**
     * 查询这个类别下最新更新的 500 个小说ID列表，并放入缓存中 1 个小时
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
            value = CacheConsts.LAST_UPDATE_BOOK_ID_LIST_CACHE_NAME)
    public List<Long> getLastUpdateIdList(Long categoryId) {
        LambdaQueryWrapper<BookInfo> query = new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getCategoryId, categoryId)
                .gt(BookInfo::getWordCount, 0)
                .orderByDesc(BookInfo::getUpdateTime)
                .last(DatabaseConsts.SqlEnum.LIMIT_30.getSql());
        return bookInfoMapper.selectList(query).stream().map(BookInfo::getId).toList();
    }
}