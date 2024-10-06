package com.cuctut.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Xss 过滤配置属性
 *
 * @author cuctut
 * @since 2024/10/01
 */
@ConfigurationProperties(prefix = "novel.xss")
public record XssProperties(Boolean enabled, List<String> excludes) {

}
