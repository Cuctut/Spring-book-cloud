package com.cuctut.user.manager.redis;

import com.cuctut.common.constant.CacheConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 验证码 管理类
 *
 * @author cuctut
 * @date 2024/10/05
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyCodeManager {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 校验图形验证码
     * @param sessionId 请求会话标识，用来标识图形验证码属于哪个会话
     * @param verifyCode 待校验验证码
     * @return
     */
    public boolean imgVerifyCodeOk(String sessionId, String verifyCode) {
        return Objects.equals(stringRedisTemplate.opsForValue()
            .get(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId), verifyCode);
    }

    /**
     * 从 Redis 中删除验证码
     */
    public void removeImgVerifyCode(String sessionId) {
        stringRedisTemplate.delete(CacheConsts.IMG_VERIFY_CODE_CACHE_KEY + sessionId);
    }

}
