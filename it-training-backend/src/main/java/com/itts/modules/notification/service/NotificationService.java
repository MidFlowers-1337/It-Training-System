package com.itts.modules.notification.service;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 发送邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendEmail(String to, String subject, String content);

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationEmail(String to, String code);

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    boolean sendSms(String phone, String content);

    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationSms(String phone, String code);
}
