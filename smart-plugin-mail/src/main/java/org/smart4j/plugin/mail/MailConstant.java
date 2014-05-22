package org.smart4j.plugin.mail;

import org.smart4j.framework.core.ConfigHelper;

public interface MailConstant {

    boolean IS_DEBUG = ConfigHelper.getBoolean("smart.plugin.mail.is_debug");

    interface Sender {

        String PROTOCOL = ConfigHelper.getString("smart.plugin.mail.sender.protocol");
        boolean IS_SSL = ConfigHelper.getBoolean("smart.plugin.mail.sender.protocol.ssl");
        String HOST = ConfigHelper.getString("smart.plugin.mail.sender.protocol.host");
        int PORT = ConfigHelper.getInt("smart.plugin.mail.sender.protocol.port");
        String FROM = ConfigHelper.getString("smart.plugin.mail.sender.from");
        boolean IS_AUTH = ConfigHelper.getBoolean("smart.plugin.mail.sender.auth");
        String AUTH_USERNAME = ConfigHelper.getString("smart.plugin.mail.sender.auth.username");
        String AUTH_PASSWORD = ConfigHelper.getString("smart.plugin.mail.sender.auth.password");
    }

    interface Fetcher {

        String PROTOCOL = ConfigHelper.getString("smart.plugin.mail.fetcher.protocol");
        boolean IS_SSL = ConfigHelper.getBoolean("smart.plugin.mail.fetcher.protocol.ssl");
        String HOST = ConfigHelper.getString("smart.plugin.mail.fetcher.protocol.host");
        int PORT = ConfigHelper.getInt("smart.plugin.mail.fetcher.protocol.port");
        String FOLDER = ConfigHelper.getString("smart.plugin.mail.fetcher.folder");
        boolean FOLDER_READONLY = ConfigHelper.getBoolean("smart.plugin.mail.fetcher.folder.readonly");
    }
}
