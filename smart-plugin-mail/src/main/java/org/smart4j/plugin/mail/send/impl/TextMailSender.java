package org.smart4j.plugin.mail.send.impl;

import java.net.MalformedURLException;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.smart4j.plugin.mail.send.AbstractMailSender;

public class TextMailSender extends AbstractMailSender {

    public TextMailSender(String subject, String content, String[] to) {
        super(subject, content, to);
    }

    @Override
    public Email createEmail() {
        return new MultiPartEmail();
    }

    @Override
    public void setContent(Email email, String content) throws MalformedURLException, EmailException {
        email.setMsg(content);
    }
}
