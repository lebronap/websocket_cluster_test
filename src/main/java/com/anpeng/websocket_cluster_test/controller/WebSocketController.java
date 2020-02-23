package com.anpeng.websocket_cluster_test.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/SpringBootDemo")
public class WebSocketController {

    // 跳转stomp websocket 页面
    @RequestMapping(value = "/spring/stompSocket.do",method = RequestMethod.GET)
    public String toStompWebSocket(HttpSession session, HttpServletRequest request, Model model){

        // 这里封装一个登录的用户组参数，模拟进入通讯后的简单初始化
        model.addAttribute("groupId","user_groupId");
        model.addAttribute("session_id",session.getId());
        model.addAttribute("token",session.getId());
        model.addAttribute("mobile","15902209821");
        System.out.println("跳转:15902209821");
        session.setAttribute("loginName",session.getId());
        session.setAttribute("mobile","15902209821");
        return "/test/springWebSocketStomp";
    }

    // 跳转stomp websocket 页面
    @RequestMapping(value = "/spring/stompSocket1.do",method = RequestMethod.GET)
    public String toStompWebSocket1(HttpSession session, HttpServletRequest request, Model model){

        // 这里封装一个登录的用户组参数，模拟进入通讯后的简单初始化
        model.addAttribute("groupId","user_groupId");
        model.addAttribute("session_id",session.getId());
        model.addAttribute("token",session.getId());
        model.addAttribute("mobile","15902209822");
        System.out.println("跳转:15902209822");
        session.setAttribute("loginName",session.getId());
        session.setAttribute("mobile","15902209822");
        return "/test/springWebSocketStomp";
    }
}
