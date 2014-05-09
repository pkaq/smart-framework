package org.smart4j.plugin.mail.send;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.StringUtil;
import org.smart4j.plugin.mail.MailConstant;
import org.smart4j.plugin.mail.util.MailUtil;

public abstract class AbstractMailSender implements MailSender {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMailSender.class);

    // 创建 Email 对象（在子类中实现）
    private final Email email = createEmail();

    // 定义发送邮件的必填字段
    private final String subject;
    private final String content;
    private final String[] to;

    public AbstractMailSender(String subject, String content, String[] to) {
        this.subject = subject;
        this.content = content;
        this.to = to;
    }

    @Override
    public void addCc(String[] cc) {
        try {
            if (ArrayUtil.isNotEmpty(cc)) {
                for (String address : cc) {
                    email.addCc(MailUtil.encodeAddress(address));
                }
            }
        } catch (EmailException e) {
            logger.error("错误：添加 CC 出错！", e);
        }
    }

    @Override
    public void addBcc(String[] bcc) {
        try {
            if (ArrayUtil.isNotEmpty(bcc)) {
                for (String address : bcc) {
                    email.addBcc(MailUtil.encodeAddress(address));
                }
            }
        } catch (EmailException e) {
            logger.error("错误：添加 BCC 出错！", e);
        }
    }

    @Override
    public void addAttachment(String path) {
        try {
            if (email instanceof MultiPartEmail) {
                MultiPartEmail multiPartEmail = (MultiPartEmail) email;
                EmailAttachment emailAttachment = new EmailAttachment();
                emailAttachment.setURL(new URL(path));
                emailAttachment.setName(path.substring(path.lastIndexOf("/") + 1));
                multiPartEmail.attach(emailAttachment);
            }
        } catch (MalformedURLException e) {
            logger.error("错误：创建 URL 出错！", e);
        } catch (EmailException e) {
            logger.error("错误：添加附件出错！", e);
        }
    }

    @Override
    public final void send() {
        try {
            // 判断协议名是否为 smtp（暂时仅支持 smtp，未来可考虑扩展）
            if (!MailConstant.Sender.PROTOCOL.equalsIgnoreCase("smtp")) {
                logger.error("错误：不支持该协议！目前仅支持 smtp 协议");
                return;
            }
            // 判断是否支持 SSL 连接
            if (MailConstant.Sender.IS_SSL) {
                email.setSSLOnConnect(true);
            }
            // 设置 主机名 与 端口号
            email.setHostName(MailConstant.Sender.HOST);
            email.setSmtpPort(MailConstant.Sender.PORT);
            // 判断是否进行身份认证
            if (MailConstant.Sender.IS_AUTH) {
                email.setAuthentication(MailConstant.Sender.AUTH_USERNAME, MailConstant.Sender.AUTH_PASSWORD);
            }
            // 判断是否开启 Debug 模式
            if (MailConstant.IS_DEBUG) {
                email.setDebug(true);
            }
            // 设置 From 地址
            if (StringUtil.isNotEmpty(MailConstant.Sender.FROM)) {
                email.setFrom(MailUtil.encodeAddress(MailConstant.Sender.FROM));
            }
            // 设置 To 地址
            for (String address : to) {
                email.addTo(MailUtil.encodeAddress(address));
            }
            // 设置主题
            email.setSubject(subject);
            // 设置内容（在子类中实现）
            setContent(email, content);
            // 发送邮件
            email.send();
        } catch (Exception e) {
            logger.error("错误：发送邮件出错！", e);
        }
    }

    public abstract Email createEmail();

    public abstract void setContent(Email email, String content) throws MalformedURLException, EmailException;
}
