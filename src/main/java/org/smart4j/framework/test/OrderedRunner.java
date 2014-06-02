package org.smart4j.framework.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.smart4j.framework.HelperLoader;
import org.smart4j.framework.test.annotation.TestOrder;

/**
 * 使测试用例可按顺序执行
 *
 * @author huangyong
 * @since 1.0
 */
public class OrderedRunner extends BlockJUnit4ClassRunner {

    /**
     * 定义一个静态变量，确保 computeTestMethods() 中的排序逻辑只运行一次（JUnit 会调用两次）
     */
    private static List<FrameworkMethod> testMethodList;

    public OrderedRunner(Class<?> cls) throws InitializationError {
        // 调用父类构造器
        super(cls);
        // 初始化 Helper 类
        HelperLoader.init();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (testMethodList == null) {
            // 获取带有 Test 注解的方法
            testMethodList = super.computeTestMethods();
            // 获取测试方法上的 Order 注解，并对所有的测试方法重新排序
            Collections.sort(testMethodList, new Comparator<FrameworkMethod>() {
                @Override
                public int compare(FrameworkMethod m1, FrameworkMethod m2) {
                    TestOrder o1 = m1.getAnnotation(TestOrder.class);
                    TestOrder o2 = m2.getAnnotation(TestOrder.class);
                    if (o1 == null || o2 == null) {
                        return 0;
                    }
                    return o1.value() - o2.value();
                }
            });
        }
        return testMethodList;
    }
}
