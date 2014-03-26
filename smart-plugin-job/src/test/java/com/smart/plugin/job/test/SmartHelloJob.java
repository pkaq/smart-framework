package com.smart.plugin.job.test;

import com.smart.framework.annotation.Bean;
import com.smart.plugin.job.BaseJob;
import java.text.SimpleDateFormat;
import java.util.Date;

@Bean
public class SmartHelloJob extends BaseJob {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void execute() {
        System.out.println(format.format(new Date()) + " - Hello Smart!");
    }
}
