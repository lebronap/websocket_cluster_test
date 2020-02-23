package com.anpeng.websocket_cluster_test.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * WebSocket通讯拦截器
 * 建立WebSocket连接前后的业务处理
 */
@Slf4j
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> map) throws Exception {
        //WebSocket握手建立前调用，获取HttpSession
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            //这里从request中获取session,获取不到不创建,可以根据业务处理此段
            HttpSession httpSession = serverHttpRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                //这里打印一下session id 方便等下对比和springMVC获取到httpsession是不是同一个
                log.info("httpSession key: {}" + httpSession.getId());

                //获取到httpSession后,可根据自身业务，操作其中的信息，这里只是单独的和webSocket进行关联
                map.put("HTTP_SESSION",httpSession);
            }else {
                log.warn("httpSession is null");
            }

        }
        //调用父类方法
        return super.beforeHandshake(request, response, wsHandler, map);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        //webSocket 建立连接后调用
        log.info("webSocket连接握手成功");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
