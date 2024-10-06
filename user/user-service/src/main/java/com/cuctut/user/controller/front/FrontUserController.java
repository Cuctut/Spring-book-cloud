package com.cuctut.user.controller.front;

import com.cuctut.book.dto.req.BookCommentReqDto;
import com.cuctut.common.auth.UserHolder;
import com.cuctut.common.constant.ApiRouterConsts;
import com.cuctut.common.constant.SystemConfigConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.user.dto.req.UserInfoUptReqDto;
import com.cuctut.user.dto.req.UserLoginReqDto;
import com.cuctut.user.dto.req.UserRegisterReqDto;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import com.cuctut.user.dto.resp.UserLoginRespDto;
import com.cuctut.user.dto.resp.UserRegisterRespDto;
import com.cuctut.user.manager.feign.BookFeignManager;
import com.cuctut.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 前台门户-用户模块 API 控制器
 *
 * @author cuctut
 * @date 2024/10/05
 */
@Tag(name = "UserController", description = "前台门户-用户模块")
@SecurityRequirement(name = SystemConfigConsts.HTTP_AUTH_HEADER_NAME)
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_USER_URL_PREFIX)
@RequiredArgsConstructor
public class FrontUserController {

    private final UserService userService;

    private final BookFeignManager bookFeignManager;

    /**
     * 用户注册接口
     */
    @Operation(summary = "用户注册接口")
    @PostMapping("register")
    public RestResp<UserRegisterRespDto> register(
            @Valid @RequestBody UserRegisterReqDto dto
    ) {
        return userService.register(dto);
    }

    /**
     * 用户登录接口
     */
    @Operation(summary = "用户登录接口")
    @PostMapping("login")
    public RestResp<UserLoginRespDto> login(
            @Valid @RequestBody UserLoginReqDto dto
    ) {
        return userService.login(dto);
    }

    /**
     * 用户信息查询接口
     */
    @Operation(summary = "用户信息查询接口")
    @GetMapping
    public RestResp<UserInfoRespDto> getUserInfo() {
        return userService.getUserInfo(UserHolder.getUserId());
    }

    /**
     * 用户信息修改接口
     */
    @Operation(summary = "用户信息修改接口")
    @PutMapping
    public RestResp<Void> updateUserInfo(
            @Valid @RequestBody UserInfoUptReqDto dto
    ) {
        dto.setUserId(UserHolder.getUserId());
        return userService.updateUserInfo(dto);
    }

    /**
     * 用户反馈提交接口
     */
    @Operation(summary = "用户反馈提交接口")
    @PostMapping("feedback")
    public RestResp<Void> submitFeedback(
            @Parameter(description = "反馈内容", required = true) @RequestBody String content
    ) {
        return userService.saveFeedback(UserHolder.getUserId(), content);
    }

    /**
     * 用户反馈删除接口
     */
    @Operation(summary = "用户反馈删除接口")
    @DeleteMapping("feedback/{id}")
    public RestResp<Void> deleteFeedback(
            @Parameter(description = "反馈ID", required = true) @PathVariable Long id
    ) {
        return userService.deleteFeedback(UserHolder.getUserId(), id);
    }

    /**
     * 发表评论接口
     */
    @Operation(summary = "发表评论接口")
    @PostMapping("comment")
    public RestResp<Void> comment(
            @Valid @RequestBody BookCommentReqDto dto
    ) {
        return bookFeignManager.publishComment(dto);
    }

    /**
     * 修改评论接口
     */
    @Operation(summary = "修改评论接口")
    @PutMapping("comment/{id}")
    public RestResp<Void> updateComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id,
            @Parameter(description = "评论内容", required = true) String content
    ) {
        BookCommentReqDto dto = new BookCommentReqDto();
        dto.setUserId(UserHolder.getUserId());
        dto.setCommentId(id);
        dto.setCommentContent(content);
        return bookFeignManager.updateComment(dto);
    }

    /**
     * 删除评论接口
     */
    @Operation(summary = "删除评论接口")
    @DeleteMapping("comment/{id}")
    public RestResp<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long id
    ) {
        BookCommentReqDto dto = new BookCommentReqDto();
        dto.setUserId(UserHolder.getUserId());
        dto.setCommentId(id);
        return bookFeignManager.deleteComment(dto);
    }

    /**
     * 查询书架状态接口 0-不在书架 1-已在书架
     */
    @Operation(summary = "查询书架状态接口")
    @GetMapping("bookshelf_status")
    public RestResp<Integer> getBookshelfStatus(
            @Parameter(description = "小说ID") String bookId
    ) {
        return userService.getBookshelfStatus(UserHolder.getUserId(), bookId);
    }

}
