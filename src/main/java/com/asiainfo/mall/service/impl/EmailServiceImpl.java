package com.asiainfo.mall.service.impl;

import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.service.EmailService;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(Constant.EMAIL_ADDRESS);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setText(text);
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public boolean saveEmailToRedis(String emailAddress, String verificationCode) {
        RedissonClient redissonClient = Redisson.create();
        RBucket<String> bucket = redissonClient.getBucket(emailAddress);
        boolean exists = bucket.isExists();
        if (!exists) {
            bucket.set(verificationCode, 60, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmailAndCode(String emailAddress, String verificationCode) {
        RedissonClient redissonClient = Redisson.create();
        RBucket<String> bucket = redissonClient.getBucket(emailAddress);
        boolean exists = bucket.isExists();
        if (exists) {
            String code = bucket.get();
            if (code.equals(verificationCode)) {
                return true;
            }
        }
        return false;
    }
}
