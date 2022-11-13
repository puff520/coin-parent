package cn.ztuo.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.huobi.client.MarketClient;
import com.huobi.constant.HuobiOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class StompSocketHandler {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    MarketClient marketClient = MarketClient.create(new HuobiOptions());

    /**
     * @MethodName: subscribeMapping
     * @Description: 订阅成功通知
     * @Param: [id]
     * @Return: void
     * @Author: scott
     * @Date: 2021/6/30
     **/
    @SubscribeMapping("/user/{id}/listener")
    public void subscribeMapping(@DestinationVariable("id") final long id) {
        System.out.println(">>>>>>用户:" + id + ",已订阅");
        SubscribeMsg param = new SubscribeMsg(id, String.format("用户【%s】已订阅成功", id));
        sendToUser(param);
    }


    /**
     * @MethodName: test
     * @Description: 接收订阅topic消息
     * @Param: [id, msg]
     * @Return: void
     * @Author: scott
     * @Date: 2021/6/30
     **/
    @MessageMapping(value = "/user/{id}/listener")
    public void UserSubListener(@DestinationVariable long id, String msg) {
        log.info("收到客户端:" + id + ",的消息");
        SubscribeMsg param = new SubscribeMsg(id, String.format("已收到用户【%s】发送消息【%s】", id, msg));
        sendToUser(param);
    }

    @GetMapping("/refresh/{userId}")
    public void refresh(@PathVariable Long userId, String msg) {
        StompSocketHandler.SubscribeMsg param = new StompSocketHandler
                .SubscribeMsg(userId, String.format("服务端向用户【%s】发送消息【%s】", userId, msg));
        sendToUser(param);
    }

    /**
     * 推送消息给订阅用户
     **/
    public void sendToUser(SubscribeMsg screenChangeMsg) {
        simpMessagingTemplate.convertAndSendToUser(screenChangeMsg.getUserId()
                        .toString(),
                "/listener", JSON.toJSONString(screenChangeMsg));
    }

    /**
     * 发送广播，需要用户事先订阅广播
     **/
    public void sendBroadCast(String topic, String msg) {
        simpMessagingTemplate.convertAndSend(topic, msg);
    }


    public static class SubscribeMsg {
        private Long userId;
        private String msg;

        public SubscribeMsg(Long UserId, String msg) {
            this.userId = UserId;
            this.msg = msg;
        }

        public Long getUserId() {
            return userId;
        }

        public String getMsg() {
            return msg;
        }
    }
}
