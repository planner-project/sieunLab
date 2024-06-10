package com.planner.travel.domain.chat.controller;

import com.planner.travel.domain.chat.dto.ChatDto;
import com.planner.travel.global.jwt.token.TokenAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TokenAuthenticator tokenAuthenticator;

    @MessageMapping(value = "/planner/{plannerId}/chat/send")
    public void sendMessage(
            @DestinationVariable Long plannerId,
            @RequestBody ChatDto chatDto) {

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "chat", "message", chatDto)
        );
    }
}
