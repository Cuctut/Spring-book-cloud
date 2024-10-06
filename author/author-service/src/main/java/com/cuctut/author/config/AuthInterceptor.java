package com.cuctut.author.config;

import com.cuctut.author.dto.AuthorInfoDto;
import com.cuctut.author.manager.cache.AuthorInfoCacheManager;
import com.cuctut.common.auth.JwtUtils;
import com.cuctut.common.auth.UserHolder;
import com.cuctut.common.constant.ErrorCodeEnum;
import com.cuctut.common.constant.SystemConfigConsts;
import com.cuctut.config.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 认证授权 拦截器
 *
 * @author cuctut
 * @since 2024/10/06
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthorInfoCacheManager authorInfoCacheManager;

    /**
     * handle 执行前调用
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登录 JWT 并校验作家
        String token = request.getHeader(SystemConfigConsts.HTTP_AUTH_HEADER_NAME);
        if (!StringUtils.hasText(token)) throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);

        Long userId = JwtUtils.parseToken(token, SystemConfigConsts.NOVEL_FRONT_KEY);
        if (Objects.isNull(userId)) throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);

        AuthorInfoDto authorInfo = authorInfoCacheManager.getAuthor(userId);
        if (Objects.isNull(authorInfo)) throw new BusinessException(ErrorCodeEnum.USER_UN_AUTH);

        UserHolder.setAuthorId(authorInfo.getId());
        UserHolder.setUserId(userId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * handler 执行后调用，出现异常不调用
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * DispatcherServlet 完全处理完请求后调用，出现异常照常调用
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理当前线程保存的用户数据
        UserHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
