package org.smart4j.plugin.druid;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.ds.impl.AbstractDataSourceFactory;

/**
 * 基于 Durid 的数据源工厂
 *
 * @author huangyong
 * @since 2.3
 */
public class DruidDataSourceFactory extends AbstractDataSourceFactory<DruidDataSource> {

    private static final Logger logger = LoggerFactory.getLogger(DruidDataSourceFactory.class);

    @Override
    public DruidDataSource createDataSource() {
        return new DruidDataSource();
    }

    @Override
    public void setDriver(DruidDataSource ds, String driver) {
        ds.setDriverClassName(driver);
    }

    @Override
    public void setUrl(DruidDataSource ds, String url) {
        ds.setUrl(url);
    }

    @Override
    public void setUsername(DruidDataSource ds, String username) {
        ds.setUsername(username);
    }

    @Override
    public void setPassword(DruidDataSource ds, String password) {
        ds.setPassword(password);
    }

    @Override
    public void setAdvancedConfig(DruidDataSource ds) {
        try {
            ds.setFilters("stat");
        } catch (SQLException e) {
            logger.error("错误：设置 Stat Filter 失败！", e);
        }
    }
}
