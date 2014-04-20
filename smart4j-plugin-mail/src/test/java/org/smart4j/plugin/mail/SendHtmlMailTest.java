package org.smart4j.plugin.mail;

import org.junit.Test;
import org.smart4j.plugin.mail.send.MailSender;
import org.smart4j.plugin.mail.send.impl.HtmlMailSender;

public class SendHtmlMailTest {

    private static final String subject = "测试";
    private static final String content = "" +
        "<p><a href='http://my.oschina.net/huangyong/blog/158380'>欢迎使用 Smart Framework！</a></p>" +
        "<p><a href='http://my.oschina.net/huangyong'><img src='http://static.oschina.net/uploads/user/111/223750_100.jpg'></a></p>";
    private static final String[] to = {"黄勇<hy_think@163.com>"};

    @Test
    public void sendTest() {
        MailSender mailSender = new HtmlMailSender(subject, content, to);
        mailSender.addAttachment("http://www.oschina.net/img/logo_s2.png");
        mailSender.send();
    }
}
