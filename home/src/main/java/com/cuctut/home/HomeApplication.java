package com.cuctut.home;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.cuctut"})
@MapperScan("com.cuctut.home.dao.mapper")
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.cuctut.book.feign"})
public class HomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeApplication.class, args);
    }

}
