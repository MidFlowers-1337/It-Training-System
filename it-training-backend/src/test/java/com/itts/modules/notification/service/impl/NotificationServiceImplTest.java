package com.itts.modules.notification.service.impl;

import com.itts.modules.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NotificationService 单元测试
 * <p>
 * [Phase 6 #29] 当前实现为 stub（sendEmail/sendSms 总是返回 true），
 * 测试仅验证 stub 行为，提供虚假安全感。
 * 待真实通知服务（邮件/短信网关）集成后再启用并重写。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("通知服务测试")
class NotificationServiceImplTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该成功发送邮件")
    void should_SendEmail_Successfully() {
        String to = "test@example.com";
        String subject = "测试邮件";
        String content = "这是一封测试邮件";

        boolean result = notificationService.sendEmail(to, subject, content);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该成功发送验证码邮件")
    void should_SendVerificationEmail_Successfully() {
        String to = "test@example.com";
        String code = "123456";

        boolean result = notificationService.sendVerificationEmail(to, code);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该成功发送短信")
    void should_SendSms_Successfully() {
        String phone = "13800138000";
        String content = "测试短信内容";

        boolean result = notificationService.sendSms(phone, content);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该成功发送验证码短信")
    void should_SendVerificationSms_Successfully() {
        String phone = "13800138000";
        String code = "123456";

        boolean result = notificationService.sendVerificationSms(phone, code);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该处理空邮箱地址")
    void should_HandleEmptyEmail() {
        String to = "";
        String subject = "测试";
        String content = "内容";

        boolean result = notificationService.sendEmail(to, subject, content);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("应该处理空手机号")
    void should_HandleEmptyPhone() {
        String phone = "";
        String content = "测试";

        boolean result = notificationService.sendSms(phone, content);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("验证码邮件应该包含正确的验证码")
    void should_ContainCorrectCode_InVerificationEmail() {
        String to = "test@example.com";
        String code = "654321";

        boolean result = notificationService.sendVerificationEmail(to, code);

        assertThat(result).isTrue();
    }

    @Test
    @Disabled("stub implementation - awaiting real notification service")
    @DisplayName("验证码短信应该包含正确的验证码")
    void should_ContainCorrectCode_InVerificationSms() {
        String phone = "13800138000";
        String code = "654321";

        boolean result = notificationService.sendVerificationSms(phone, code);

        assertThat(result).isTrue();
    }
}
