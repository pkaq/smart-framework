package org.smart4j.plugin.mail.send.impl;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.smart4j.plugin.mail.send.AbstractMailSender;

public class HtmlMailSender extends AbstractMailSender {

    public HtmlMailSender(String subject, String content, String[] to) {
        super(subject, content, to);
    }

    @Override
    public Email createEmail() {
        return new ImageHtmlEmail();
    }

    @Override
    public void setContent(Email email, String content) throws MalformedURLException, EmailException {
        ImageHtmlEmail imageHtmlEmail = (ImageHtmlEmail) email;
        imageHtmlEmail.setDataSourceResolver(new DataSourceUrlResolver(new URL("http://"), true));
        imageHtmlEmail.setHtmlMsg(content);
    }
}
