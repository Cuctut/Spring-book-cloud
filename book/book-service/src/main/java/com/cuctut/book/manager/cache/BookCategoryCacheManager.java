package com.cuctut.book.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuctut.book.dao.entity.BookCategory;
import com.cuctut.book.dao.mapper.BookCategoryMapper;
import com.cuctut.book.dto.resp.BookCategoryRespDto;
import com.cuctut.common.constant.CacheConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookCategoryCacheManager {

    private final BookCategoryMapper bookCategoryMapper;

    /**
     * 根据作品方向查询分类并缓存
     * @param workDirection 作品方向;0-男频 1-女频
     * @return 分类列表
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER,
            value = CacheConsts.BOOK_CATEGORY_LIST_CACHE_NAME)
    public List<BookCategoryRespDto> listCategory(Integer workDirection) {
        log.debug("Method {} is called, No caching is used.", Thread.currentThread().getStackTrace()[1].getMethodName());
        LambdaQueryWrapper<BookCategory> query = new LambdaQueryWrapper<BookCategory>().eq(BookCategory::getWorkDirection, workDirection);
        List<BookCategory> bookCategoryList = bookCategoryMapper.selectList(query);
        return bookCategoryList.stream().map(
                v -> BookCategoryRespDto.builder().id(v.getId()).name(v.getName()).build()
        ).toList();
    }
}