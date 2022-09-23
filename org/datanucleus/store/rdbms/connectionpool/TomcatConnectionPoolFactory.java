// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import java.util.Properties;
import org.datanucleus.ClassLoaderResolver;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;

public class TomcatConnectionPoolFactory extends AbstractConnectionPoolFactory
{
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        final String dbURL = storeMgr.getConnectionURL();
        String dbDriver = storeMgr.getConnectionDriverName();
        if (dbDriver == null) {
            dbDriver = "";
        }
        String dbUser = storeMgr.getConnectionUserName();
        if (dbUser == null) {
            dbUser = "";
        }
        String dbPassword = storeMgr.getConnectionPassword();
        if (dbPassword == null) {
            dbPassword = "";
        }
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        this.loadDriver(dbDriver, clr);
        ClassUtils.assertClassForJarExistsInClasspath(clr, "org.apache.tomcat.jdbc.pool.DataSource", "tomcat-jdbc.jar");
        final PoolProperties config = new PoolProperties();
        config.setUrl(dbURL);
        config.setDriverClassName(dbDriver);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        final Properties dbProps = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
        config.setDbProperties(dbProps);
        if (storeMgr.hasProperty("datanucleus.connectionPool.abandonWhenPercentageFull")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.abandonWhenPercentageFull");
            if (value >= 0) {
                config.setAbandonWhenPercentageFull(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.initialPoolSize")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.initialPoolSize");
            if (size > 0) {
                config.setInitialSize(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.initSQL")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.initSQL");
            config.setInitSQL(value2);
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.jdbcInterceptors")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.jdbcInterceptors");
            config.setJdbcInterceptors(value2);
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.logAbandonded")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.logAbandonded");
            config.setLogAbandoned(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxActive")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxActive");
            if (size > 0) {
                config.setMaxActive(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxAge")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxAge");
            if (value >= 0) {
                config.setMaxAge((long)value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxIdle")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxIdle");
            if (size >= 0) {
                config.setMaxIdle(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxWait")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxWait");
            if (value > 0) {
                config.setMaxWait(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.minEvictableIdleTimeMillis")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.minEvictableIdleTimeMillis");
            if (value > 0) {
                config.setMinEvictableIdleTimeMillis(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.minIdle")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.minIdle");
            if (value >= 0) {
                config.setMinIdle(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.removeAbandonded")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.removeAbandonded");
            config.setRemoveAbandoned(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.removeAbandondedTimeout")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.removeAbandondedTimeout");
            if (value > 0) {
                config.setRemoveAbandonedTimeout(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.suspectTimeout")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.suspectTimeout");
            if (value > 0) {
                config.setSuspectTimeout(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.testOnBorrow")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.testOnBorrow");
            config.setTestOnBorrow(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.testOnConnect")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.testOnConnect");
            config.setTestOnConnect(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.testOnReturn")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.testOnReturn");
            config.setTestOnReturn(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.testWhileIdle")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.testWhileIdle");
            config.setTestWhileIdle(Boolean.parseBoolean(value2));
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.timeBetweenEvictionRunsMillis")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.timeBetweenEvictionRunsMillis");
            if (value > 0) {
                config.setTimeBetweenEvictionRunsMillis(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.validationInterval")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.validationInterval");
            if (value >= 0) {
                config.setValidationInterval((long)value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.validationQuery")) {
            final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.validationQuery");
            config.setValidationQuery(value2);
        }
        return new TomcatConnectionPool(new DataSource((PoolConfiguration)config));
    }
    
    public class TomcatConnectionPool implements ConnectionPool
    {
        final DataSource dataSource;
        
        public TomcatConnectionPool(final DataSource ds) {
            this.dataSource = ds;
        }
        
        @Override
        public void close() {
            this.dataSource.close();
        }
        
        @Override
        public javax.sql.DataSource getDataSource() {
            return (javax.sql.DataSource)this.dataSource;
        }
    }
}
