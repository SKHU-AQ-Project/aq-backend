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

    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "AQ");
            helper.setTo(toEmail);
            helper.setSubject("[AQ] 이메일 인증 코드");

            String htmlContent = buildVerificationEmailHtml(code);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("인증 코드 이메일 전송 완료: {} (코드: {})", toEmail, code);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("인증 이메일 전송 실패: {}", toEmail, e);
            throw new RuntimeException("이메일 전송에 실패했습니다", e);
        }
    }

    private String buildVerificationEmailHtml(String code) {
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
                    .code-box { background-color: #fff; border: 3px solid #4CAF50; border-radius: 10px; padding: 20px; text-align: center; margin: 30px 0; }
                    .code { font-size: 36px; font-weight: bold; color: #4CAF50; letter-spacing: 8px; font-family: 'Courier New', monospace; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    .warning { color: #ff6b6b; font-size: 14px; margin-top: 20px; }
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
                        <p>아래 인증 코드를 입력하여 이메일 인증을 완료해주세요:</p>
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                        <p style="text-align: center; font-size: 14px; color: #666;">
                            이 코드는 <strong>10분간</strong> 유효합니다.
                        </p>
                        <p class="warning">
                            보안을 위해 이 코드를 다른 사람과 공유하지 마세요.
                        </p>
                    </div>
                    <div class="footer">
                        <p>이 이메일은 자동으로 발송된 메일입니다.</p>
                        <p>© 2024 AQ. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(code);
    }
}

