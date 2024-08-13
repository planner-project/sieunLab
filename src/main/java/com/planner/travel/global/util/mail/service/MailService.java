package com.planner.travel.global.util.mail.service;

import com.planner.travel.domain.user.repository.UserRepository;
import com.planner.travel.global.util.RandomNumberUtil;
import com.planner.travel.global.util.RedisUtil;
import com.planner.travel.global.util.mail.dto.MailAuthenticaionMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final RandomNumberUtil randomNumberUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final SpringTemplateEngine templateEngine;

    public String sendMailAuthenticationCode(MailAuthenticaionMessage message) throws MessagingException {
        validateUser(message.to());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        Long tempCode = randomNumberUtil.setTempCode();
        redisUtil.setDataWithExpire(message.to(), String.valueOf(tempCode), Duration.ofMinutes(5));

        messageHelper.setTo(message.to());
        messageHelper.setSubject(message.subject());
        messageHelper.setText(setContext(String.valueOf(tempCode)), true);
        javaMailSender.send(mimeMessage);

        return String.valueOf(tempCode);
    }

    public String setContext(String tempCode) {
        Context context = new Context();
        context.setVariable("code", tempCode);
        return templateEngine.process("emailAuthentication", context);
    }

    private void validateUser(String email) {
        userRepository.findByEmailAndProvider(email, "basic")
                .ifPresent(u -> {
                    throw new IllegalArgumentException();
                });
    }
}
