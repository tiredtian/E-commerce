package com.asiainfo.mall.service;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);

    boolean saveEmailToRedis(String emailAddress, String verificationCode);

    boolean checkEmailAndCode(String emailAddress, String verificationCode);
}
