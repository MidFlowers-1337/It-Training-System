package com.itts.modules.notification.service.impl;

import com.itts.modules.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务实现
 *
 * 注意：这是一个简化的实现，使用日志记录来模拟发送邮件和短信
 * 在生产环境中，应该替换为真实的邮件服务（如 Spring Mail）和短信服务（如阿里云短信）
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        log.info("发送邮件 - 收件人: {}, 主题: {}, 内容: {}", to, subject, content);

        // TODO: 集成真实的邮件服务
        // 示例：使用 Spring Mail
        // try {
        //     MimeMessage message = mailSender.createMimeMessage();
        //     MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        //     helper.setTo(to);
        //     helper.setSubject(subject);
        //     helper.setText(content, true);
        //     mailSender.send(message);
        //     return true;
        // } catch (Exception e) {
        //     log.error("发送邮件失败", e);
        //     return false;
        // }

        // 模拟发送成功
        return true;
    }

    @Override
    public boolean sendVerificationEmail(String to, String code) {
        String subject = "IT培训系统 - 邮箱验证码";
        String content = String.format(
            "<html><body>" +
            "<h2>邮箱验证</h2>" +
            "<p>您的验证码是：<strong style='font-size: 24px; color: #409eff;'>%s</strong></p>" +
            "<p>验证码有效期为 5 分钟，请尽快完成验证。</p>" +
            "<p>如果这不是您的操作，请忽略此邮件。</p>" +
            "</body></html>",
            code
        );

        return sendEmail(to, subject, content);
    }

    @Override
    public boolean sendSms(String phone, String content) {
        log.info("发送短信 - 手机号: {}, 内容: {}", phone, content);

        // TODO: 集成真实的短信服务
        // 示例：使用阿里云短信服务
        // try {
        //     SendSmsRequest request = new SendSmsRequest();
        //     request.setPhoneNumbers(phone);
        //     request.setSignName("IT培训系统");
        //     request.setTemplateCode("SMS_123456789");
        //     request.setTemplateParam("{\"code\":\"" + content + "\"}");
        //     SendSmsResponse response = client.sendSms(request);
        //     return "OK".equals(response.getCode());
        // } catch (Exception e) {
        //     log.error("发送短信失败", e);
        //     return false;
        // }

        // 模拟发送成功
        return true;
    }

    @Override
    public boolean sendVerificationSms(String phone, String code) {
        String content = String.format("【IT培训系统】您的验证码是：%s，有效期5分钟。", code);
        return sendSms(phone, content);
    }
}
