package com.cuctut.author.service;


import com.cuctut.author.dto.req.AuthorRegisterReqDto;
import com.cuctut.common.resp.RestResp;

/**
 * 作家模块 业务服务类
 *
 * @author cuctut
 * @since 2024/10/06
 */
public interface AuthorService {

    /**
     * 作家注册
     *
     * @param dto 注册参数
     * @return void
     */
    RestResp<Void> register(AuthorRegisterReqDto dto);

    /**
     * 查询作家状态
     *
     * @param userId 用户ID
     * @return 作家状态
     */
    RestResp<Integer> getStatus(Long userId);
}
