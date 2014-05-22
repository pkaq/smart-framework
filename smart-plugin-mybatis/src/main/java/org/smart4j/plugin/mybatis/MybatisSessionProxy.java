package org.smart4j.plugin.mybatis;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.aop.AspectProxy;
import org.smart4j.framework.aop.annotation.Aspect;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.tx.annotation.Service;

/**
 * Mybatis - 代理类，用于自动处理SqlSession
 * Created by liuzh on 14-4-9.
 */
@Aspect(annotation = Service.class)
public class MybatisSessionProxy extends AspectProxy {
    private static final Logger logger = LoggerFactory.getLogger(MybatisSessionProxy.class);

    // 定义一个线程局部变量，用于保存当前线程中事务处理的阶段，初始为0
    private static final ThreadLocal<Short> flagContainer = new ThreadLocal<Short>() {
        protected Short initialValue() {
            return 0;
        }
    };

    private static void incrementFlag() {
        short flag = flagContainer.get();
        flagContainer.set(++flag);
    }

    private static void decrementFlag() {
        short flag = flagContainer.get();
        flagContainer.set(--flag);
    }

    /**包扫描路径,逗号隔开*/
    public static final String MYBATIS_SESSION = "mybatis.session.auto";

    /**是否自动开启Mybatis的Session，默认为false不开启，必须使用MybatisSession注解**/
    public static final Boolean auto;

    static {
        Boolean _auto = false;
        try {
            _auto = ConfigHelper.getBoolean(MYBATIS_SESSION);
        }
        catch (Exception ex){
            _auto = false;
        }
        finally {
            auto = _auto;
        }
    }

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        //判断是否需要Session
        if (auto || method.isAnnotationPresent(MybatisSession.class)) {
            //初始进入方法 - 开启Session
            if (flagContainer.get() == 0) {
                MybatisHelper.getSqlSession();
            }
            //+1
            incrementFlag();
        }
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        //-1
        if (auto || method.isAnnotationPresent(MybatisSession.class)) {
            decrementFlag();
        }
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
        //出现异常的时候，进不去after，需要在这里-1
        if (auto || method.isAnnotationPresent(MybatisSession.class)) {
            decrementFlag();
        }
        //只有返回到最后一层，才会回滚，如果中间catch了异常，这里不会自动回滚
        if (flagContainer.get() == 0) {
            MybatisHelper.rollback();
            logger.error("操作异常,Mybatis自动回滚,错误信息:"+e.getMessage());
        }
    }

    @Override
    public void end() {
        if (flagContainer.get() == 0) {
            //移除
            flagContainer.remove();
            MybatisHelper.closeSession();
        }
    }
}