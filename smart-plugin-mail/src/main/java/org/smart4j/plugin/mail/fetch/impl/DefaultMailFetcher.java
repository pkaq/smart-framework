package org.smart4j.plugin.mail.fetch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.DateUtil;
import org.smart4j.plugin.mail.MailConstant;
import org.smart4j.plugin.mail.fetch.MailFetcher;
import org.smart4j.plugin.mail.fetch.MailInfo;
import org.smart4j.plugin.mail.util.MailUtil;

public class DefaultMailFetcher implements MailFetcher {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMailFetcher.class);

    // 获取协议名（pop3 或 imap）
    private static final String PROTOCOL = MailConstant.Fetcher.PROTOCOL;

    private final String username;
    private final String password;

    public DefaultMailFetcher(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public List<MailInfo> fetch(int count) {
        // 创建 Session
        Session session = createSession();
        // 创建 MailInfo 列表
        List<MailInfo> mailInfoList = new ArrayList<MailInfo>();
        // 收取邮件
        Store store = null;
        Folder folder = null;
        try {
            // 获取 Store，并连接 Store（登录）
            store = session.getStore(PROTOCOL);
            store.connect(username, password);
            // 获取 Folder（收件箱）
            folder = store.getFolder(MailConstant.Fetcher.FOLDER);
            // 判断是 只读方式 还是 读写方式 打开收件箱
            if (MailConstant.Fetcher.FOLDER_READONLY) {
                folder.open(Folder.READ_ONLY);
            } else {
                folder.open(Folder.READ_WRITE);
            }
            // 获取邮件总数
            int size = folder.getMessageCount();
            // 获取并遍历邮件列表
            Message[] messages = folder.getMessages();
            if (ArrayUtil.isNotEmpty(messages)) {
                for (int i = size - 1; i > size - count - 1; i--) {
                    // 创建并累加 MailInfo
                    Message message = messages[i];
                    if (message instanceof MimeMessage) {
                        MailInfo mailInfo = createMailInfo((MimeMessage) message);
                        mailInfoList.add(mailInfo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("错误：收取邮件出错！", e);
        } finally {
            try {
                // 关闭收件箱
                if (folder != null) {
                    folder.close(false);
                }
                // 注销
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                logger.error("错误：释放资源出错！", e);
            }
        }
        return mailInfoList;
    }

    @Override
    public MailInfo fetchLatest() {
        List<MailInfo> mailInfoList = fetch(1);
        return CollectionUtil.isNotEmpty(mailInfoList) ? mailInfoList.get(0) : null;
    }

    private Session createSession() {
        // 初始化 Session 配置项
        Properties props = new Properties();
        // 判断是否支持 SSL 连接
        if (MailConstant.Fetcher.IS_SSL) {
            props.put("mail." + PROTOCOL + ".ssl.enable", true);
        }
        // 设置 主机名 与 端口号
        props.put("mail." + PROTOCOL + ".host", MailConstant.Fetcher.HOST);
        props.put("mail." + PROTOCOL + ".port", MailConstant.Fetcher.PORT);
        // 创建 Session
        Session session = Session.getDefaultInstance(props);
        // 判断是否开启 debug 模式
        if (MailConstant.IS_DEBUG) {
            session.setDebug(true);
        }
        return session;
    }

    private String[] parseTo(MimeMessageParser parser) throws Exception {
        return doParse(parser.getTo());
    }

    private String[] parseCc(MimeMessageParser parser) throws Exception {
        return doParse(parser.getCc());
    }

    private String[] parseBcc(MimeMessageParser parser) throws Exception {
        return doParse(parser.getBcc());
    }

    private String[] doParse(List<Address> addressList) {
        List<String> list = new ArrayList<String>();
        if (CollectionUtil.isNotEmpty(addressList)) {
            for (Address address : addressList) {
                list.add(MailUtil.decodeAddress(address.toString()));
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private MailInfo createMailInfo(MimeMessage message) throws Exception {
        // 创建 MailInfo
        MailInfo mailInfo = new MailInfo();
        // 解析邮件内容
        MimeMessageParser parser = new MimeMessageParser(message).parse();
        // 设置 MailInfo 相关属性
        mailInfo.setSubject(parser.getSubject());
        if (parser.hasHtmlContent()) {
            mailInfo.setContent(parser.getHtmlContent());
        } else if (parser.hasPlainContent()) {
            mailInfo.setContent(parser.getPlainContent());
        }
        mailInfo.setFrom(parser.getFrom());
        mailInfo.setTo(parseTo(parser));
        mailInfo.setCc(parseCc(parser));
        mailInfo.setBcc(parseBcc(parser));
        mailInfo.setDate(DateUtil.formatDatetime(message.getSentDate().getTime()));
        return mailInfo;
    }
}
