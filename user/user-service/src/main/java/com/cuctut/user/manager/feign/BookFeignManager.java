package com.cuctut.user.manager.feign;

import com.cuctut.book.dto.req.BookCommentReqDto;
import com.cuctut.book.feign.BookFeign;
import com.cuctut.common.auth.UserHolder;
import com.cuctut.common.resp.RestResp;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 小说微服务调用 Feign 客户端管理
 *
 * @author cuctut
 * @since 2024/10/05
 */
@Component
@AllArgsConstructor
public class BookFeignManager {

    private final BookFeign bookFeign;

    public RestResp<Void> publishComment(BookCommentReqDto dto) {
        dto.setUserId(UserHolder.getUserId());
        return bookFeign.publishComment(dto);
    }

    public RestResp<Void> updateComment(BookCommentReqDto dto) {
        dto.setUserId(UserHolder.getUserId());
        return bookFeign.updateComment(dto);
    }

    public RestResp<Void> deleteComment(BookCommentReqDto dto) {
        dto.setUserId(UserHolder.getUserId());
        return bookFeign.deleteComment(dto);
    }


}
