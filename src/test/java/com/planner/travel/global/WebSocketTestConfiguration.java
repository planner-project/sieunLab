package com.planner.travel.global;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;

@Configuration
public class WebSocketTestConfiguration {
    @Bean
    public WebSocketStompClient webSocketStompClient() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        SockJsClient sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(standardWebSocketClient)));
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }
}
