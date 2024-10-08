package com.cuctut.config;

import com.cuctut.common.constant.AmqpConsts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP 配置类
 *
 * @author cuctut
 * @since 2024/10/01
 */
@Configuration
public class AmqpConfig {

    /**
     * 小说信息改变交换机
     */
//    @Bean
//    public FanoutExchange bookChangeExchange() {
//        return new FanoutExchange(AmqpConsts.BookChangeMq.EXCHANGE_NAME);
//    }
//
//    /**
//     * Elasticsearch book 索引更新队列
//     */
//    @Bean
//    public Queue esBookUpdateQueue() {
//        return new Queue(AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE);
//    }
//
//    /**
//     * Elasticsearch book 索引更新队列绑定到小说信息改变交换机
//     */
//    @Bean
//    public Binding esBookUpdateQueueBinding() {
//        return BindingBuilder.bind(esBookUpdateQueue()).to(bookChangeExchange());
//    }

}
