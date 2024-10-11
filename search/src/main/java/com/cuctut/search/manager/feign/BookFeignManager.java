package com.cuctut.search.manager.feign;

import com.cuctut.book.dto.resp.BookEsRespDto;
import com.cuctut.book.feign.BookFeign;
import com.cuctut.common.constant.ErrorCodeEnum;
import com.cuctut.common.resp.RestResp;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 小说微服务调用 Feign 客户端管理
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Component
@AllArgsConstructor
public class BookFeignManager {

    private final BookFeign bookFeign;

    public List<BookEsRespDto> listEsBooks(Long maxBookId){
        RestResp<List<BookEsRespDto>> listRestResp = bookFeign.listNextEsBooks(maxBookId);
        return listRestResp.isOk()? listRestResp.getData() : Collections.emptyList();
    }

}
