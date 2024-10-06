package com.cuctut.home.controller;

import com.cuctut.common.constant.ApiRouterConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.home.dto.resp.HomeBookRespDto;
import com.cuctut.home.dto.resp.HomeFriendLinkRespDto;
import com.cuctut.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台门户-首页模块 API 控制器
 *
 * @author cuctut
 * @since 2024/10/07
 */
@Tag(name = "HomeController", description = "前台门户-首页模块")
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_HOME_URL_PREFIX)
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 首页小说推荐查询接口
     */
    @Operation(summary = "首页小说推荐查询接口")
    @GetMapping("books")
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        return homeService.listHomeBooks();
    }

    /**
     * 首页友情链接列表查询接口
     */
    @Operation(summary = "首页友情链接列表查询接口")
    @GetMapping("friend_Link/list")
    public RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks() {
        return homeService.listHomeFriendLinks();
    }

}
