package org.smart4j.plugin.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.ds.impl.AbstractDataSourceFactory;

/**
 * 基于 C3P0 的数据源工厂
 *
 * @author huangyong
 * @since 2.3
 */
public class C3P0DataSourceFactory extends AbstractDataSourceFactory<ComboPooledDataSource> {

    private static final Logger logger = LoggerFactory.getLogger(C3P0DataSourceFactory.class);

    @Override
    public ComboPooledDataSource createDataSource() {
        return new ComboPooledDataSource();
    }

    @Override
    public void setDriver(ComboPooledDataSource ds, String driver) {
        try {
            ds.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            logger.error("错误：初始化 JDBC Driver Class 失败！", e);
        }
    }

    @Override
    public void setUrl(ComboPooledDataSource ds, String url) {
        ds.setJdbcUrl(url);
    }

    @Override
    public void setUsername(ComboPooledDataSource ds, String username) {
        ds.setUser(username);
    }

    @Override
    public void setPassword(ComboPooledDataSource ds, String password) {
        ds.setPassword(password);
    }

    @Override
    public void setAdvancedConfig(ComboPooledDataSource ds) {
    }
}
