package com.planner.travel.domain.planner.controller;

import com.planner.travel.domain.planner.dto.request.PlanBoxCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlanBoxUpdateRequest;
import com.planner.travel.domain.planner.service.PlanBoxService;
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
public class PlanBoxController {
    private final PlanBoxService planBoxService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value = "/planner/{plannerId}/create")
    public void createDate(@DestinationVariable Long plannerId, @RequestBody PlanBoxCreateRequest request) {
        planBoxService.create(request, plannerId);

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "create-planBox", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }

    @MessageMapping(value = "/planner/{plannerId}/update/{planBoxId}")
    public void updateDate(@DestinationVariable("plannerId") Long plannerId, @DestinationVariable("planBoxId") Long planBoxId, @RequestBody PlanBoxUpdateRequest request) {
        planBoxService.update(request, plannerId, planBoxId);

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "update-planBox", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }

    @MessageMapping(value = "/planner/{plannerId}/delete/{planBoxId}")
    public void deleteDate(@DestinationVariable("plannerId") Long plannerId, @DestinationVariable("planBoxId") Long planBoxId) {
        planBoxService.delete(plannerId, planBoxId);

    simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "delete-planBox", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }
}
