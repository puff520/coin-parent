package cn.ztuo.bitrade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static long HEART_BEAT = 10000;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.initialize();
        //基于内存的STOMP消息代理来代替mq的消息代理
        //订阅Broker名称,/user代表点对点即发指定用户，/topic代表发布广播即群发
        //setHeartbeatValue 设置心跳及心跳时间
        registry.enableSimpleBroker("/user", "/topic")
                .setHeartbeatValue(new long[]{HEART_BEAT, HEART_BEAT}).setTaskScheduler(te);
        //点对点使用的订阅前缀，不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user/");

    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/market-ws").setAllowedOrigins("*")
                .withSockJS();
    }

}