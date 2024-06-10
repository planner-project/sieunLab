package com.planner.travel.domain.planner.controller;

import com.planner.travel.domain.planner.dto.request.PlanCreateRequest;
import com.planner.travel.domain.planner.dto.request.PlanUpdateRequest;
import com.planner.travel.domain.planner.service.PlanBoxService;
import com.planner.travel.domain.planner.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
@Controller
@Slf4j
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final PlanBoxService planBoxService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value = "/planner/{plannerId}/planBox/{planBoxId}/create")
    public void createDate(@DestinationVariable("plannerId") Long plannerId, @DestinationVariable("planBoxId") Long planBoxId, @RequestBody PlanCreateRequest request) {
        planService.create(request, planBoxId);

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "create-planBox", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }

    @MessageMapping(value = "/planner/{plannerId}/planBox/{planBoxId}/update/{planId}")
    public void updateDate(@DestinationVariable Long plannerId, @DestinationVariable("planBoxId") Long planBoxId, @DestinationVariable("planId") Long planId, @RequestBody PlanUpdateRequest request) {
        planService.update(request, planId);

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "update-plan", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }

    @MessageMapping(value = "/planner/{plannerId}/planBox/{planBoxId}/delete/{planId}")
    public void deleteDate(@DestinationVariable("plannerId") Long plannerId, @DestinationVariable("planBoxId") Long planBoxId, @DestinationVariable("planId") Long planId) {
        planService.delete(planId);

        simpMessagingTemplate.convertAndSend("/sub/planner/" + plannerId,
                Map.of("type", "delete-plan", "message", planBoxService.getAllPlanBox(plannerId))
        );
    }
}
