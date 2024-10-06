package com.cuctut.author.controller;

import com.cuctut.author.dto.req.AuthorRegisterReqDto;
import com.cuctut.author.manager.feign.BookFeignManager;
import com.cuctut.author.service.AuthorService;
import com.cuctut.book.dto.req.BookAddReqDto;
import com.cuctut.book.dto.req.BookPageReqDto;
import com.cuctut.book.dto.req.ChapterAddReqDto;
import com.cuctut.book.dto.req.ChapterPageReqDto;
import com.cuctut.book.dto.resp.BookChapterRespDto;
import com.cuctut.book.dto.resp.BookInfoRespDto;
import com.cuctut.common.auth.UserHolder;
import com.cuctut.common.constant.ApiRouterConsts;
import com.cuctut.common.constant.SystemConfigConsts;
import com.cuctut.common.req.PageReqDto;
import com.cuctut.common.resp.PageRespDto;
import com.cuctut.common.resp.RestResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * 作家后台-作家模块 API 控制器
 *
 * @author cuctut
 * @since 2024/10/06
 */
@Tag(name = "AuthorController", description = "作家后台-作者模块")
@SecurityRequirement(name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME)
@RestController
@RequestMapping(ApiRouterConsts.API_AUTHOR_URL_PREFIX)
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    private final BookFeignManager bookFeignManager;

    /**
     * 作家注册接口
     */
    @Operation(summary = "作家注册接口")
    @PostMapping("register")
    public RestResp<Void> register(
            @Valid @RequestBody AuthorRegisterReqDto dto
    ) {
        dto.setUserId(UserHolder.getUserId());
        return authorService.register(dto);
    }

    /**
     * 查询作家状态接口
     */
    @Operation(summary = "作家状态查询接口")
    @GetMapping("status")
    public RestResp<Integer> getStatus() {
        return authorService.getStatus(UserHolder.getUserId());
    }

    /**
     * 小说发布接口
     */
    @Operation(summary = "小说发布接口")
    @PostMapping("book")
    public RestResp<Void> publishBook(@Valid @RequestBody BookAddReqDto dto) {
        return bookFeignManager.publishBook(dto);
    }

    /**
     * 小说发布列表查询接口
     */
    @Operation(summary = "小说发布列表查询接口")
    @GetMapping("books")
    public RestResp<PageRespDto<BookInfoRespDto>> listBooks(@ParameterObject BookPageReqDto dto) {
        dto.setAuthorId(UserHolder.getAuthorId());
        return bookFeignManager.listPublishBooks(dto);
    }

    /**
     * 小说章节发布接口
     */
    @Operation(summary = "小说章节发布接口")
    @PostMapping("book/chapter/{bookId}")
    public RestResp<Void> publishBookChapter(
        @Parameter(description = "小说ID") @PathVariable("bookId") Long bookId,
        @Valid @RequestBody ChapterAddReqDto dto) {
        dto.setAuthorId(UserHolder.getAuthorId());
        dto.setBookId(bookId);
        return bookFeignManager.publishBookChapter(dto);
    }

    /**
     * 小说章节发布列表查询接口
     */
    @Operation(summary = "小说章节发布列表查询接口")
    @GetMapping("book/chapters/{bookId}")
    public RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(
        @Parameter(description = "小说ID") @PathVariable("bookId") Long bookId,
        @ParameterObject PageReqDto dto) {
        ChapterPageReqDto chapterPageReqReqDto = new ChapterPageReqDto();
        chapterPageReqReqDto.setBookId(bookId);
        chapterPageReqReqDto.setPageNum(dto.getPageNum());
        chapterPageReqReqDto.setPageSize(dto.getPageSize());
        return bookFeignManager.listPublishBookChapters(chapterPageReqReqDto);
    }

}
