package com.cuctut.user.feign;

import com.cuctut.common.constant.ApiRouterConsts;
import com.cuctut.common.resp.RestResp;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * user微服务调用客户端
 *
 * @author cuctut
 * @since  2024/10/04
 */
@Component
@FeignClient(value = "user-service", fallback = UserFeign.UserFeignFallback.class)
public interface UserFeign {

    /**
     * 批量查询用户信息
     */
    @PostMapping(ApiRouterConsts.API_INNER_USER_URL_PREFIX + "/listUserInfoByIds")
    RestResp<List<UserInfoRespDto>> listUserInfoByIds(List<Long> userIds);

    @Component
    class UserFeignFallback implements UserFeign {

        @Override
        public RestResp<List<UserInfoRespDto>> listUserInfoByIds(List<Long> userIds) {
            return RestResp.ok(Collections.emptyList());
        }
    }

}
