package smart.framework.helper.bean;

import java.lang.reflect.Method;

public class ActionBean {

    private Class<?> actionClass;
    private Method actionMethod;

    public ActionBean(Class<?> actionClass, Method actionMethod) {
        this.actionClass = actionClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getActionClass() {
        return actionClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}