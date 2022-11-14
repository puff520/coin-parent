package cn.ztuo.bitrade.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@ServerEndpoint(value = "/ws/marketPush/{token}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    private static Cache<String, Session> SESSION_CACHE = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(300).expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        String sessionId = session.getId();
        onlineCount.incrementAndGet(); // 在线数加1
        this.sendMessage("sessionId:" + sessionId + ",已经和server建立连接", session);
        SESSION_CACHE.put(sessionId, session);
        log.info("有新连接加入：{}，当前在线连接数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("token") String token) {
        onlineCount.decrementAndGet(); // 在线数减1
        SESSION_CACHE.invalidate(session.getId());
        log.info("有一连接关闭：{}，当前在线连接数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
        this.sendMessage("服务端已收到推送消息:" + message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private static void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}", e);
        }
    }


}