package smart.plugin.job.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import smart.framework.annotation.Bean;
import smart.plugin.job.BaseJob;

@Bean
public class SmartHelloJob extends BaseJob {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void execute() {
        System.out.println(format.format(new Date()) + " - Hello Smart!");
    }
}
