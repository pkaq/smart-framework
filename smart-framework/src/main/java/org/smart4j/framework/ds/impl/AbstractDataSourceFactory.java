package org.smart4j.framework.ds.impl;

import javax.sql.DataSource;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.ds.DataSourceFactory;
import org.smart4j.framework.util.StringUtil;

/**
 * 抽象数据源工厂实现
 *
 * @param <T> 数据源
 */
public abstract class AbstractDataSourceFactory<T extends DataSource> implements DataSourceFactory {

    // 从配置文件中读取 JDBC 配置项
    protected String driver = ConfigHelper.getConfigString("jdbc.driver");
    protected String url = ConfigHelper.getConfigString("jdbc.url");
    protected String username = ConfigHelper.getConfigString("jdbc.username");
    protected String password = ConfigHelper.getConfigString("jdbc.password");

    @Override
    public final T getDataSource() {
        // 创建数据源
        T ds = createDataSource();
        // 设置基础配置
        if (StringUtil.isNotEmpty(driver)) {
            setDriver(ds, driver);
        }
        if (StringUtil.isNotEmpty(url)) {
            setUrl(ds, url);
        }
        if (StringUtil.isNotEmpty(username)) {
            setUsername(ds, username);
        }
        if (StringUtil.isNotEmpty(password)) {
            setPassword(ds, password);
        }
        // 设置高级配置
        setAdvancedConfig(ds);
        return ds;
    }

    public abstract T createDataSource();

    public abstract void setDriver(T ds, String driver);

    public abstract void setUrl(T ds, String url);

    public abstract void setUsername(T ds, String username);

    public abstract void setPassword(T ds, String password);

    public abstract void setAdvancedConfig(T ds);
}
