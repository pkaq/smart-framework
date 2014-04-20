package org.smart4j.plugin.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class FetchMail {

    public static void main(String[] args) {
        String protocol = "pop3";
        boolean isSSL = true;
        String host = "pop.163.com";
        int port = 995;
        String username = "huang_yong_2006@163.com";
        String password = "xxx";

        Properties props = new Properties();
        props.put("mail.pop3.ssl.enable", isSSL);
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", port);

        Session session = Session.getDefaultInstance(props);

        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(protocol);
            store.connect(username, password);

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            int size = folder.getMessageCount();
            Message message = folder.getMessage(size);

            String from = message.getFrom()[0].toString();
            String subject = message.getSubject();
            Date date = message.getSentDate();

            System.out.println("from: " + from);
            System.out.println("subject: " + subject);
            System.out.println("date: " + date);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (folder != null) {
                    folder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        System.out.println("接收完毕！");
    }
}
