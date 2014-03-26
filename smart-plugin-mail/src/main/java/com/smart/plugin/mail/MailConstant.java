package com.smart.plugin.mail;

import com.smart.framework.helper.ConfigHelper;

public interface MailConstant {

    boolean IS_DEBUG = ConfigHelper.getConfigBoolean("mail.is_debug");

    interface Sender {

        String PROTOCOL = ConfigHelper.getConfigString("mail.sender.protocol");
        boolean IS_SSL = ConfigHelper.getConfigBoolean("mail.sender.protocol.ssl");
        String HOST = ConfigHelper.getConfigString("mail.sender.protocol.host");
        int PORT = ConfigHelper.getConfigNumber("mail.sender.protocol.port");
        String FROM = ConfigHelper.getConfigString("mail.sender.from");
        boolean IS_AUTH = ConfigHelper.getConfigBoolean("mail.sender.auth");
        String AUTH_USERNAME = ConfigHelper.getConfigString("mail.sender.auth.username");
        String AUTH_PASSWORD = ConfigHelper.getConfigString("mail.sender.auth.password");
    }

    interface Fetcher {

        String PROTOCOL = ConfigHelper.getConfigString("mail.fetcher.protocol");
        boolean IS_SSL = ConfigHelper.getConfigBoolean("mail.fetcher.protocol.ssl");
        String HOST = ConfigHelper.getConfigString("mail.fetcher.protocol.host");
        int PORT = ConfigHelper.getConfigNumber("mail.fetcher.protocol.port");
        String FOLDER = ConfigHelper.getConfigString("mail.fetcher.folder");
        boolean FOLDER_READONLY = ConfigHelper.getConfigBoolean("mail.fetcher.folder.readonly");
    }
}
