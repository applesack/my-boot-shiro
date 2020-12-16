package xyz.scootaloo.bootshiro.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;

/**
 * @author : flutterdash@qq.com
 * @since : 2020年12月15日 14:14
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;

    // 初始化大小，最小，最大
    // druid 配置: https://github.com/alibaba/druid/wiki/
    private static final int initialSize = 1;
    private static final int minIdle = 1;
    private static final int maxActive = 20;
    // 配置获取连接等待超时的时间
    private static final int maxWait = 60000;
    // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    private static final int timeBetweenEvictionRunsMillis = 60000;
    // 配置一个连接在池中最小生存的时间，单位是毫秒
    private static final int minEvictableIdleTimeMillis = 300000;
    private static final String validationQuery = "select 1";
    private static final boolean testWhileIdle = true;
    private static final boolean testOnBorrow = false;
    private static final boolean testOnReturn = false;
    // 打开PSCache，并且指定每个连接上PSCache的大小
    private static final boolean poolPreparedStatements = true;
    private static final int maxPoolPreparedStatementPerConnectionSize = 20;
    // 配置监控统计拦截的filters，stat用于监控界面，wall用于防火墙防御sql注入, slf4j用于druid记录sql日志
    private static final String filters = "stat,slf4j,wall";
    // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
    private static final String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";
    // 合并多个DruidDataSource的监控数据
    private static final boolean useGlobalDataSourceStat = false;

    @Bean
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        /*数据源主要配置*/
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);

        /*数据源补充配置*/
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        dataSource.setConnectionProperties(connectionProperties);
        dataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);

        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            log.error("druid configuration initialization filter: " + e);
        }

        dataSource.setConnectionProperties(connectionProperties);
        return dataSource;
    }

}
