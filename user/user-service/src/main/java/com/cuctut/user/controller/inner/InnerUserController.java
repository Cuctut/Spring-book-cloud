package com.cuctut.user.controller.inner;

import com.cuctut.common.constant.ApiRouterConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import com.cuctut.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户微服务内部调用接口
 *
 * @author cuctut
 * @since 2024/10/05
 */
@Tag(name = "InnerBookController", description = "内部调用-用户模块")
@RestController
@RequestMapping(ApiRouterConsts.API_INNER_USER_URL_PREFIX)
@RequiredArgsConstructor
public class InnerUserController {

    private final UserService userService;

    /**
     * 批量查询用户信息
     */
    @Operation(summary = "批量查询用户信息")
    @PostMapping("listUserInfoByIds")
    RestResp<List<UserInfoRespDto>> listUserInfoByIds(
            @RequestBody List<Long> userIds
    ) {
        return userService.listUserInfoByIds(userIds);
    }

}
