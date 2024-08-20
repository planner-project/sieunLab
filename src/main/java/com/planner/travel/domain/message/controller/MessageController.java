package com.planner.travel.domain.message.controller;

import com.planner.travel.domain.message.dto.request.MessageSendRequest;
import com.planner.travel.domain.message.dto.request.MessagesRequest;
import com.planner.travel.domain.message.dto.response.MessageListResponse;
import com.planner.travel.domain.message.dto.response.MessageResponse;
import com.planner.travel.domain.message.query.MessageQueryService;
import com.planner.travel.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MessageQueryService messageQueryService;


    @GetMapping(value = "/{userId}")
    public Page<MessageListResponse> getMessageList(@PathVariable("userId") Long userId, Pageable pageable) {
        return messageQueryService.getMessageList(userId, pageable);
    }

    @PostMapping(value = "/contents")
    public Page<MessageResponse> getMessages(@RequestBody MessagesRequest request, Pageable pageable) {
        return messageQueryService.getMessages(request, pageable);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> sendMessage(@RequestBody MessageSendRequest request) {
        messageService.sendMessage(request);
        return ResponseEntity.ok().build();
    }
}
