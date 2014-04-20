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
    /**包扫描路径,逗号隔开*/
    public static final String MYBATIS_SESSION = "mybatis.session.auto";

    /**是否自动开启Mybatis的Session，默认为false不开启，必须使用MybatisSession注解**/
    public static final Boolean auto;

    static {
        Boolean _auto = false;
        try {
            _auto = ConfigHelper.getConfigBoolean(MYBATIS_SESSION);
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
        //开启Session
        if(auto || method.isAnnotationPresent(MybatisSession.class)){
            MybatisHelper.getSqlSession();
        }
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
        MybatisHelper.rollback();
        logger.error("操作异常,Mybatis自动回滚,错误信息:"+e.getMessage());
    }

    @Override
    public void end() {
        MybatisHelper.closeSession();
    }
}
