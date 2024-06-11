package com.planner.travel.global.util.mail.controller;

import com.planner.travel.domain.user.entity.User;
import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.util.mail.dto.MailAuthenticaionMessage;
import com.planner.travel.global.util.mail.dto.request.MailAuthenticationRequest;
import com.planner.travel.global.util.mail.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MailController {
    private final MailService mailService;
    private final UserRepository userRepository;

    @PostMapping("/signup/authentication/send")
    public ResponseEntity<?> sendAuthenticationEmail(@RequestBody MailAuthenticationRequest request) throws MessagingException {
        Optional<User> optionalUser = userRepository.findByEmailAndProvider(request.email(), "basic");

        if (optionalUser.isPresent()) {
            throw new EntityExistsException();
        }

        MailAuthenticaionMessage mailAuthenticaionMessage = new MailAuthenticaionMessage(
                request.email(),
                "[travel-planner] 이메일 인증 코드 입니다."
        );

        String tempCode = mailService.sendMailAuthenticationCode(mailAuthenticaionMessage);

        return ResponseEntity.ok(tempCode);
    }
}
