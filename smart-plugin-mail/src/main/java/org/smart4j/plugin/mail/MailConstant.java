package org.smart4j.plugin.mail;

import org.smart4j.framework.core.ConfigHelper;

public interface MailConstant {

    boolean IS_DEBUG = ConfigHelper.getConfigBoolean("mail.is_debug");

    interface Sender {

        String PROTOCOL = ConfigHelper.getString("mail.sender.protocol");
        boolean IS_SSL = ConfigHelper.getConfigBoolean("mail.sender.protocol.ssl");
        String HOST = ConfigHelper.getString("mail.sender.protocol.host");
        int PORT = ConfigHelper.getInt("mail.sender.protocol.port");
        String FROM = ConfigHelper.getString("mail.sender.from");
        boolean IS_AUTH = ConfigHelper.getConfigBoolean("mail.sender.auth");
        String AUTH_USERNAME = ConfigHelper.getString("mail.sender.auth.username");
        String AUTH_PASSWORD = ConfigHelper.getString("mail.sender.auth.password");
    }

    interface Fetcher {

        String PROTOCOL = ConfigHelper.getString("mail.fetcher.protocol");
        boolean IS_SSL = ConfigHelper.getConfigBoolean("mail.fetcher.protocol.ssl");
        String HOST = ConfigHelper.getString("mail.fetcher.protocol.host");
        int PORT = ConfigHelper.getInt("mail.fetcher.protocol.port");
        String FOLDER = ConfigHelper.getString("mail.fetcher.folder");
        boolean FOLDER_READONLY = ConfigHelper.getConfigBoolean("mail.fetcher.folder.readonly");
    }
}
