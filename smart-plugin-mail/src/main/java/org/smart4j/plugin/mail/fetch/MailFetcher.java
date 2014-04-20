package org.smart4j.plugin.mail.fetch;

import java.util.List;

public interface MailFetcher {

    List<MailInfo> fetch(int count);

    MailInfo fetchLatest();
}
