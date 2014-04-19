package smart.framework.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import smart.framework.annotation.Action;
import smart.framework.annotation.Aspect;
import smart.framework.annotation.Bean;
import smart.framework.annotation.Service;
import smart.framework.throwable.InitializationError;

public class BeanHelper {

    private static final Map<Class<?>, Object> beanMap = new HashMap<Class<?>, Object>(); // Bean 类 => Bean 实例

    static {
        try {
            // 获取应用包路径下所有的类
            List<Class<?>> classList = ClassHelper.getClassList();
            for (Class<?> cls : classList) {
                // 处理带有 Bean/Service/Action/Aspect 注解的类
                if (cls.isAnnotationPresent(Bean.class) ||
                    cls.isAnnotationPresent(Service.class) ||
                    cls.isAnnotationPresent(Action.class) ||
                    cls.isAnnotationPresent(Aspect.class)) {
                    // 创建 Bean 实例
                    Object beanInstance = cls.newInstance();
                    // 将 Bean 实例放入 Bean Map 中（键为 Bean 类，值为 Bean 实例）
                    beanMap.put(cls, beanInstance);
                }
            }
        } catch (Exception e) {
            throw new InitializationError("初始化 BeanHelper 出错！", e);
        }
    }

    public static Map<Class<?>, Object> getBeanMap() {
        return beanMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> cls) {
        if (!beanMap.containsKey(cls)) {
            throw new RuntimeException("无法根据类名获取实例！" + cls);
        }
        return (T) beanMap.get(cls);
    }

    public static void setBean(Class<?> cls, Object obj) {
        beanMap.put(cls, obj);
    }
}
