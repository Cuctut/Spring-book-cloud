package com.cuctut.author.service.impl;

import com.cuctut.author.dao.entity.AuthorInfo;
import com.cuctut.author.dao.mapper.AuthorInfoMapper;
import com.cuctut.author.dto.AuthorInfoDto;
import com.cuctut.author.dto.req.AuthorRegisterReqDto;
import com.cuctut.author.manager.cache.AuthorInfoCacheManager;
import com.cuctut.author.service.AuthorService;
import com.cuctut.common.resp.RestResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 作家模块 服务实现类
 *
 * @author cuctut
 * @since 2024/10/06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorInfoCacheManager authorInfoCacheManager;

    private final AuthorInfoMapper authorInfoMapper;

    @Override
    public RestResp<Void> register(AuthorRegisterReqDto dto) {
        // 校验该用户是否已注册为作家
        AuthorInfoDto author = authorInfoCacheManager.getAuthor(dto.getUserId());
        if (Objects.nonNull(author)) return RestResp.ok();

        // 保存作家注册信息
        AuthorInfo authorInfo = AuthorInfo.builder()
                        .userId(dto.getUserId())
                        .chatAccount(dto.getChatAccount())
                        .email(dto.getEmail())
                        .inviteCode("0")
                        .telPhone(dto.getTelPhone())
                        .penName(dto.getPenName())
                        .workDirection(dto.getWorkDirection())
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
        authorInfoMapper.insert(authorInfo);
        authorInfoCacheManager.evictAuthorCache();
        return RestResp.ok();
    }

    @Override
    public RestResp<Integer> getStatus(Long userId) {
        AuthorInfoDto author = authorInfoCacheManager.getAuthor(userId);
        return RestResp.ok(
                Objects.isNull(author) ? null : author.getStatus()
        );
    }

}
