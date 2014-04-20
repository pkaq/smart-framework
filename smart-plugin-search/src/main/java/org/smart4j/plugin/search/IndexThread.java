package org.smart4j.plugin.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(IndexThread.class);

    private static final long sleep_ms = 5 * 60 * 1000; // 5 分钟

    @Override
    public void run() {
        try {
            while (true) {
                SearchHelper.index();
                sleep(sleep_ms);
            }
        } catch (InterruptedException e) {
            logger.error("运行 IndexThread 出错", e);
        }
    }
}
