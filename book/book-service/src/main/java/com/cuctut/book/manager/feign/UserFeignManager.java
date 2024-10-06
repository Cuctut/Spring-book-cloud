package com.cuctut.book.manager.feign;

import com.cuctut.common.resp.RestResp;
import com.cuctut.user.dto.resp.UserInfoRespDto;
import com.cuctut.user.feign.UserFeign;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 用户微服务调用 Feign 客户端管理
 *
 * @author cuctut
 * @since 2024/10/04
 */
@Component
@AllArgsConstructor
public class UserFeignManager {

    private final UserFeign userFeign;

    /**
     * UserFeign 远程调用 user 微服务批量查询用户信息
     * @param userIds 批量用户Id
     * @return 批量用户信息
     */
    public List<UserInfoRespDto> listUserInfoByIds(List<Long> userIds) {
        RestResp<List<UserInfoRespDto>> resp = userFeign.listUserInfoByIds(userIds);
        return resp.isOk()? resp.getData() : Collections.emptyList();
    }


}
