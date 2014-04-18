package smart.plugin.mail;

import java.util.List;
import org.junit.Test;
import smart.plugin.mail.fetch.MailFetcher;
import smart.plugin.mail.fetch.MailInfo;
import smart.plugin.mail.fetch.impl.DefaultMailFetcher;

public class FetchMailTest {

    private static final String username = "hy_think@163.com";
    private static final String password = "xxx";
    private static final MailFetcher mailFetcher = new DefaultMailFetcher(username, password);

    @Test
    public void fetchTest() {
        List<MailInfo> mailInfoList = mailFetcher.fetch(5);
        for (MailInfo mailInfo : mailInfoList) {
            System.out.println(mailInfo.getSubject());
        }
    }

    @Test
    public void fetchLatestTest() {
        MailInfo mailInfo = mailFetcher.fetchLatest();
        System.out.println(mailInfo.getSubject());
    }
}
