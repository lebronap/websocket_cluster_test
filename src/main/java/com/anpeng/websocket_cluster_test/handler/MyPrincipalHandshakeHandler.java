package com.anpeng.websocket_cluster_test.handler;

import com.anpeng.websocket_cluster_test.auth.WebSocketUserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 * 设置认证用户信息
 */
@Slf4j
public class MyPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        HttpSession httpSession = getSession(request);
        //获取登录的信息，就是controller跳转页面的信息，可以根据业务修改
        String mobile = (String) httpSession.getAttribute("mobile");
        if (StringUtils.isEmpty(mobile)){
            log.error("未登录系统，禁止登录WebSocket");
            return null;
        }
        log.info("MyPrincipalHandshakeHandler login = {}",mobile);
        return new WebSocketUserAuthentication(mobile);
    }

    private HttpSession getSession(ServerHttpRequest request){
        if (request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            return serverHttpRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
