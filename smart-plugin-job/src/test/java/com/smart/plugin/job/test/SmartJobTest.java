package com.smart.plugin.job.test;

import com.smart.framework.base.BaseTest;
import com.smart.plugin.job.JobHelper;
import org.junit.Test;

public class SmartJobTest extends BaseTest {

    @Test
    public void test() {
        JobHelper.startJob(SmartHelloJob.class, "0/1 * * * * ?");

        sleep(3000);

        JobHelper.stopJob(SmartHelloJob.class);

        sleep(3000);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
