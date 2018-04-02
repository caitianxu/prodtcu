/*
 * COPYRIGHT Beijing NetQin-Tech Co.,Ltd.                                   *
 ****************************************************************************
 * 源文件名:  web.config.DaoConfig.java
 * 功能: cpframework框架
 * 版本:	@version 1.0
 * 编制日期: 2014年9月3日 下午2:55:14
 * 修改历史: (主要历史变动原因及说明)
 * YYYY-MM-DD |    Author      |	 Change Description
 * 2014年9月3日    |    Administrator     |     Created
 */
package com.cjsz.tech.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 平台配置
 * 扫描com.cjsz.tech包下面的所有Bean组件
 * 激活事务管理（代理类模式）
 */
@Configuration
@ComponentScan(basePackages = {"com.cjsz.tech"})
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = "com.cjsz.tech.**.mapper",sqlSessionFactoryRef = "sqlSessionFactory",sqlSessionTemplateRef = "sqlSessionTemplate")
public class PlatformConfig implements TransactionManagementConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(PlatformConfig.class);

    @Autowired
    private DruidDataSourceEntity druidDataSourceEntity;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public DataSource dataSource() {
        logger.debug("druidDataSourceEntity"+druidDataSourceEntity);
        //加载配置文件属性
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(druidDataSourceEntity.getDriverClassName());
        ds.setUsername(druidDataSourceEntity.getUsername());
        ds.setPassword(druidDataSourceEntity.getPassword());
        ds.setUrl(druidDataSourceEntity.getUrl());
        ds.setMaxActive(druidDataSourceEntity.getMaxActive());
        ds.setValidationQuery(druidDataSourceEntity.getValidationQuery());
        ds.setTestOnBorrow(druidDataSourceEntity.isTestOnBorrow());
        ds.setTestOnReturn(druidDataSourceEntity.isTestOnReturn());
        ds.setTestWhileIdle(druidDataSourceEntity.isTestWhileIdle());
        ds.setTimeBetweenEvictionRunsMillis(druidDataSourceEntity.getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(druidDataSourceEntity.getMinEictableIdleTimeMillis());
        ds.setPoolPreparedStatements(druidDataSourceEntity.isPoolPreparedStatements());
        ds.setMaxOpenPreparedStatements(druidDataSourceEntity.getMaxOpenPreparedStatements());
        try {
            ds.setFilters(druidDataSourceEntity.getFilters());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Resource[] getResource(String basePackage, String pattern ) throws IOException {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(new StandardEnvironment()
                .resolveRequiredPlaceholders(basePackage)) + "/" + pattern;
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
        return resources;
    }


    @Bean
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }


    /**
     * 生成数据库vendor bean
     *
     * @return
     */
    @Bean(name = "vendorDatabaseIdProvider")
    public VendorDatabaseIdProvider getVendorDatabaseIdProvider() {
        VendorDatabaseIdProvider vendor = new VendorDatabaseIdProvider();
        Properties pros = new Properties();
        pros.put("SQL Server", "sqlserver");
        pros.put("Oracle", "oracle");
        pros.put("MySQL", "mysql");
        pros.put("H2", "h2");
        vendor.setProperties(pros);
        return vendor;
    }


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        bean.setTypeAliasesPackage("org.cjcm.wsc.beans");
        bean.setTypeHandlersPackage("org.cjcm.wsc.core.mybatis.handler");
        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("dialect","mysql");
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        //添加插件
        bean.setPlugins(new Interceptor[]{pageHelper});
        try {
        	//classpath*:mapper/*.xml
        	//classpath*:org.cjcm.wsc.mappers/*.xml
            //添加XML目录
//            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//            bean.setMapperLocations(resolver.getResources("classpath*:org.cjcm.wsc.mappers/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(SqlSessionFactory sqlSessionFactory) {
        return new JdbcTemplate(dataSource());
    }


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(10000);
        executor.initialize();
        return executor;
    }

}
