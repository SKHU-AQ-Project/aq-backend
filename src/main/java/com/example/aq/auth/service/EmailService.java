package com.example.aq.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "AQ");
            helper.setTo(toEmail);
            helper.setSubject("[AQ] 이메일 인증을 완료해주세요");

            String verificationUrl = frontendUrl + "/auth/verify-email?token=" + token;
            String htmlContent = buildVerificationEmailHtml(verificationUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("인증 이메일 전송 완료: {}", toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("인증 이메일 전송 실패: {}", toEmail, e);
            throw new RuntimeException("이메일 전송에 실패했습니다", e);
        }
    }

    private String buildVerificationEmailHtml(String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>이메일 인증</h1>
                    </div>
                    <div class="content">
                        <p>안녕하세요,</p>
                        <p>AQ에 가입해주셔서 감사합니다.</p>
                        <p>아래 버튼을 클릭하여 이메일 인증을 완료해주세요:</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">이메일 인증하기</a>
                        </div>
                        <p>또는 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p>이 링크는 24시간 동안 유효합니다.</p>
                    </div>
                    <div class="footer">
                        <p>이 이메일은 자동으로 발송된 메일입니다.</p>
                        <p>© 2024 AQ. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl);
    }
}

