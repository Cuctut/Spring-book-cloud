package com.cuctut.book.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cuctut.book.dao.entity.BookContent;
import com.cuctut.book.dao.mapper.BookContentMapper;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.common.constant.DatabaseConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 小说内容 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/04
 */
@Component
@RequiredArgsConstructor
public class BookContentCacheManager {

    private final BookContentMapper bookContentMapper;

    /**
     * 查询小说内容，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER,
        value = CacheConsts.BOOK_CONTENT_CACHE_NAME)
    public String getBookContent(Long chapterId) {
        LambdaQueryWrapper<BookContent> query = new LambdaQueryWrapper<BookContent>()
                .eq(BookContent::getChapterId, chapterId)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        return bookContentMapper.selectOne(query).getContent();
    }


}
