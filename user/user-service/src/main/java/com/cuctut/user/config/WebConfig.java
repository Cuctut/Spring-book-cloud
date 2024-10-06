package com.cuctut.user.config;

import com.cuctut.common.constant.ApiRouterConsts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>Spring Web Mvc 相关配置<p/>
 * 不要加 @EnableWebMvc 注解，否则会导致 jackson 的全局配置失效。因为 @EnableWebMvc 注解会导致 WebMvcAutoConfiguration 自动配置失效
 *
 * @author cuctut
 * @since 2024/10/05
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 权限认证拦截
        // 拦截用户模块相关请求接口，放行登录注册相关请求接口
        registry.addInterceptor(authInterceptor)
            .addPathPatterns(ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/**")
            .excludePathPatterns(
                    ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/register",
                    ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/login"
            )
            .order(2);

    }

}
