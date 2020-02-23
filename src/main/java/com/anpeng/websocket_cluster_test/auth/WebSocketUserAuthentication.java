package com.anpeng.websocket_cluster_test.auth;

import java.security.Principal;
import java.util.StringJoiner;

public class WebSocketUserAuthentication implements Principal {


    private String mobile;

    public WebSocketUserAuthentication(String mobile) {
        this.mobile = mobile;
    }

    public WebSocketUserAuthentication() {
    }

    /**
     * 获取用户登录身份令牌
     * @return
     */
    @Override
    public String getName() {
        return mobile;
    }

    @Override
    public String toString() {
        return new StringJoiner("用户身份:").add(mobile).toString();
    }
}
