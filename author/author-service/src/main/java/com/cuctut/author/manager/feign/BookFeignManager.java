package com.cuctut.author.manager.feign;

import com.cuctut.author.dto.AuthorInfoDto;
import com.cuctut.author.manager.cache.AuthorInfoCacheManager;
import com.cuctut.book.dto.req.BookAddReqDto;
import com.cuctut.book.dto.req.BookPageReqDto;
import com.cuctut.book.dto.req.ChapterAddReqDto;
import com.cuctut.book.dto.req.ChapterPageReqDto;
import com.cuctut.book.dto.resp.BookChapterRespDto;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.book.feign.BookFeign;
import com.cuctut.common.auth.UserHolder;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 小说微服务调用 Feign 客户端管理
 *
 * @author cuctut
 * @since 2024/10/06
 */
@Component
@RequiredArgsConstructor
public class BookFeignManager {

    private final BookFeign bookFeign;

    private final AuthorInfoCacheManager authorInfoCacheManager;

    public RestResp<Void> publishBook(BookAddReqDto dto) {
        AuthorInfoDto author = authorInfoCacheManager.getAuthor(UserHolder.getUserId());
        dto.setAuthorId(author.getId());
        dto.setPenName(author.getPenName());
        return bookFeign.publishBook(dto);
    }

    public RestResp<PageRespDto<BookInfoRespDto>> listPublishBooks(BookPageReqDto dto) {
        authorInfoCacheManager.getAuthor(UserHolder.getUserId());
        return bookFeign.listPublishBooks(dto);
    }

    public RestResp<Void> publishBookChapter(ChapterAddReqDto dto) {
        return bookFeign.publishBookChapter(dto);
    }

    public RestResp<PageRespDto<BookChapterRespDto>> listPublishBookChapters(ChapterPageReqDto dto) {
        return bookFeign.listPublishBookChapters(dto);
    }


}
