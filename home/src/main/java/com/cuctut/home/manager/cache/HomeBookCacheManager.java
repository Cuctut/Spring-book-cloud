package com.cuctut.home.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.common.constant.CacheConsts;
import com.cuctut.home.dao.entity.HomeBook;
import com.cuctut.home.dao.mapper.HomeBookMapper;
import com.cuctut.home.dto.resp.HomeBookRespDto;
import com.cuctut.home.manager.feign.BookFeignManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 首页推荐小说 缓存管理类
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@RequiredArgsConstructor
public class HomeBookCacheManager {

    private final HomeBookMapper homeBookMapper;

    private final BookFeignManager bookFeignManager;

    /**
     * 查询首页小说推荐，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
        value = CacheConsts.HOME_BOOK_CACHE_NAME)
    public List<HomeBookRespDto> listHomeBooks() {
        // 从首页小说推荐表中查询出需要推荐的小说
        LambdaQueryWrapper<HomeBook> query = new LambdaQueryWrapper<HomeBook>().orderByAsc(HomeBook::getSort);
        List<HomeBook> homeBooks = homeBookMapper.selectList(query);
        if (CollectionUtils.isEmpty(homeBooks)) return Collections.emptyList();

        // 获取推荐小说数据
        List<Long> bookIds = homeBooks.stream().map(HomeBook::getBookId).toList();
        List<BookInfoRespDto> bookInfos = bookFeignManager.listBookInfoByIds(bookIds);
        if (CollectionUtils.isEmpty(bookInfos)) return Collections.emptyList();

        // 组装 HomeBookRespDto 列表数据并返回
        Map<Long, BookInfoRespDto> bookInfoMap = bookInfos.stream().collect(
                Collectors.toMap(BookInfoRespDto::getId, Function.identity())
        );
        return homeBooks.stream().map(
                v -> {
                    BookInfoRespDto bookInfo = bookInfoMap.get(v.getBookId());
                    HomeBookRespDto bookRespDto = new HomeBookRespDto();
                    bookRespDto.setType(v.getType());
                    bookRespDto.setBookId(v.getBookId());
                    bookRespDto.setBookName(bookInfo.getBookName());
                    bookRespDto.setPicUrl(bookInfo.getPicUrl());
                    bookRespDto.setAuthorName(bookInfo.getAuthorName());
                    bookRespDto.setBookDesc(bookInfo.getBookDesc());
                    return bookRespDto;
                }
            ).toList();
    }

    @CacheEvict(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
        value = CacheConsts.HOME_BOOK_CACHE_NAME)
    public void evictCache(){
        //
    }

}
