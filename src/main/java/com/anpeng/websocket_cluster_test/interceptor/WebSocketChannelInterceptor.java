package com.anpeng.websocket_cluster_test.interceptor;

import com.anpeng.websocket_cluster_test.auth.WebSocketUserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * websocket消息监听，用于监听websocket用户连接情况
 */
@Slf4j
@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    public WebSocketChannelInterceptor() {
    }

    /**
     * 在消息发送前调用，方法中可以对消息进行修改，如果此方法返回值为空，则不会发生实际的消息发送调用
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        /**
         * 1. 判断是否为首次连接请求，如果已经连接过，直接返回message
         * 2. 网上有种写法是在这里封装认证用户的信息，本文是在http阶段，websocket 之前就做了认证的封装，所以这里直接取的信息
         */
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            /*
             * 1. 这里获取就是JS stompClient.connect(headers, function (frame){.......}) 中header的信息
             * 2. JS中header可以封装多个参数，格式是{key1:value1,key2:value2}
             * 3. header参数的key可以一样，取出来就是list
             * 4. 样例代码header中只有一个token，所以直接取0位
             */
            String mobile = accessor.getNativeHeader("mobile").get(0);
            /*
             * 1. 这里直接封装到StompHeaderAccessor 中，可以根据自身业务进行改变
             * 2. 封装大搜StompHeaderAccessor中后，可以在@Controller / @MessageMapping注解的方法中直接带上StompHeaderAccessor
             *    就可以通过方法提供的 getUser()方法获取到这里封装user对象
             * 2. 例如可以在这里拿到前端的信息进行登录鉴权
             */
            WebSocketUserAuthentication user = (WebSocketUserAuthentication) accessor.getUser();
            log.info("认证用户:[{}] 页面传递令牌:[{}]", user.toString(), mobile);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

        }
        return message;
    }


    /**
     * 在消息发送之后立即调用，boolean值参数表示该调用的返回值
     *
     * @param message
     * @param channel
     * @param sent
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        /*
         * 拿到消息头对象后，我们可以做一系列业务操作
         * 1. 通过getSessionAttributes()方法获取到websocketSession，
         *    就可以取到我们在WebSocketHandshakeInterceptor拦截器中存在session中的信息
         * 2. 我们也可以获取到当前连接的状态，做一些统计，例如统计在线人数，或者缓存在线人数对应的令牌，方便后续业务调用
         */
        HttpSession httpSession = (HttpSession) accessor.getSessionAttributes().get("HTTP_SESSION");

        //这里只是单纯打印，可以根据项目的实际情况做业务处理
        log.info("postSend 中获取httpSession key: {}", httpSession.getId());

        //忽略心跳信息等非stomp消息
        if (accessor.getCommand() == null) {
            return;
        }

        //根据连接状态做处理，这里也只是打印了下，可以根据实际场景，对上线，下线，首次成功连接做处理
        System.out.println(accessor.getCommand());
        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("httpSession key: {} , 首次连接", httpSession.getId());
                break;
            case CONNECTED:
                log.info("httpSession key: {} , 连接中", httpSession.getId());
                break;
            case DISCONNECT:
                log.info("httpSession key: {} , 下线", httpSession.getId());
                break;
            default:
                break;
        }
    }


    /*
     * 1. 在消息发送完成后调用，而不管消息发送是否产生异常，在次方法中，我们可以做一些资源释放清理的工作
     * 2. 此方法的触发必须是preSend方法执行成功，且返回值不为null,发生了实际的消息推送，才会触发
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

    }


    /* 1. 在消息被实际检索之前调用，如果返回false,则不会对检索任何消息，只适用于(PollableChannels)，
     * 2. 在websocket的场景中用不到
     */
    @Override
    public boolean preReceive(MessageChannel channel) {
        return false;
    }


    /*
     * 1. 在检索到消息之后，返回调用方之前调用，可以进行信息修改，如果返回null,就不会进行下一步操作
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return null;
    }


    /*
     * 1. 在消息接收完成之后调用，不管发生什么异常，可以用于消息发送后的资源清理
     * 2. 只有当preReceive 执行成功，并返回true才会调用此方法
     * 2. 适用于PollableChannels，轮询场景
     */
    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

    }
}