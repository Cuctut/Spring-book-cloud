package com.cuctut.search.service;


import com.cuctut.book.dto.req.BookSearchReqDto;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;

/**
 * 搜索 服务类
 *
 * @author cuctut
 * @since 2024/10/07
 */
public interface SearchService {

    /**
     * 小说搜索
     *
     * @param condition 搜索条件
     * @return 搜索结果
     */
    RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition);

}
