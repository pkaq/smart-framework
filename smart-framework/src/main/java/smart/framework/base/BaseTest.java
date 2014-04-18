package smart.framework.base;

import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smart.framework.HelperLoader;
import smart.framework.OrderedRunner;
import smart.framework.helper.DatabaseHelper;
import smart.framework.util.ClassUtil;

@RunWith(OrderedRunner.class)
public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    static {
        HelperLoader.init();
    }

    protected static void initSQL(String sqlPath) {
        try {
            File sqlFile = new File(ClassUtil.getClassPath() + sqlPath);
            List<String> sqlList = FileUtils.readLines(sqlFile);
            for (String sql : sqlList) {
                DatabaseHelper.update(sql);
            }
        } catch (Exception e) {
            logger.error("执行数据初始化脚本出错！", e);
            throw new RuntimeException(e);
        }
    }
}
