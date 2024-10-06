package com.cuctut.home.manager.feign;

import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.book.feign.BookFeign;
import com.cuctut.common.resp.RestResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 小说微服务调用 Feign 客户端管理
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@RequiredArgsConstructor
public class BookFeignManager {

    private final BookFeign bookFeign;

    public List<BookInfoRespDto> listBookInfoByIds(List<Long> bookIds){
        RestResp<List<BookInfoRespDto>> resp = bookFeign.listBookInfoByIds(bookIds);
        return resp.isOk() ? resp.getData() : Collections.emptyList();
    }

}
