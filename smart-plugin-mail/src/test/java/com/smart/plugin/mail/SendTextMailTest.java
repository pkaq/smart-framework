package com.smart.plugin.mail;

import com.smart.plugin.mail.send.MailSender;
import com.smart.plugin.mail.send.impl.TextMailSender;
import org.junit.Test;

public class SendTextMailTest {

    private static final String subject = "测试";
    private static final String content = "欢迎使用 Smart Framework！";
    private static final String[] to = {"黄勇<hy_think@163.com>"};

    @Test
    public void sendTest() {
        MailSender mailSender = new TextMailSender(subject, content, to);
        mailSender.addAttachment("http://www.oschina.net/img/logo_s2.png");
        mailSender.send();
        System.out.println("发送完毕！");
    }
}
