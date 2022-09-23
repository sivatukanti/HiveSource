// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import javax.sql.DataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.dbcp.ConnectionFactory;
import java.util.Properties;
import org.apache.commons.pool.ObjectPool;
import org.datanucleus.ClassLoaderResolver;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;

public class DBCPConnectionPoolFactory extends AbstractConnectionPoolFactory
{
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        final String dbDriver = storeMgr.getConnectionDriverName();
        final String dbURL = storeMgr.getConnectionURL();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        this.loadDriver(dbDriver, clr);
        ClassUtils.assertClassForJarExistsInClasspath(clr, "org.apache.commons.pool.ObjectPool", "commons-pool.jar");
        ClassUtils.assertClassForJarExistsInClasspath(clr, "org.apache.commons.dbcp.ConnectionFactory", "commons-dbcp.jar");
        final ObjectPool connectionPool = new GenericObjectPool(null);
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxIdle")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxIdle");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setMaxIdle(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.minIdle")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.minIdle");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setMinIdle(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxActive")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxActive");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setMaxActive(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxWait")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxWait");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setMaxWait(value);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.timeBetweenEvictionRunsMillis")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.timeBetweenEvictionRunsMillis");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setTimeBetweenEvictionRunsMillis(value);
                final int maxIdle = ((GenericObjectPool)connectionPool).getMaxIdle();
                final int numTestsPerEvictionRun = (int)Math.ceil(maxIdle / 4.0);
                ((GenericObjectPool)connectionPool).setNumTestsPerEvictionRun(numTestsPerEvictionRun);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.minEvictableIdleTimeMillis")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.minEvictableIdleTimeMillis");
            if (value > 0) {
                ((GenericObjectPool)connectionPool).setMinEvictableIdleTimeMillis(value);
            }
        }
        final Properties dbProps = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
        final ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbURL, dbProps);
        KeyedObjectPoolFactory kpf = null;
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxStatements")) {
            final int value2 = storeMgr.getIntProperty("datanucleus.connectionPool.maxStatements");
            if (value2 > 0) {
                kpf = new StackKeyedObjectPoolFactory(null, value2);
            }
        }
        try {
            String testSQL = null;
            if (storeMgr.hasProperty("datanucleus.connectionPool.testSQL")) {
                testSQL = storeMgr.getStringProperty("datanucleus.connectionPool.testSQL");
            }
            new PoolableConnectionFactory(connectionFactory, connectionPool, kpf, testSQL, false, false);
            if (testSQL != null) {
                ((GenericObjectPool)connectionPool).setTestOnBorrow(true);
            }
        }
        catch (Exception e) {
            throw new DatastorePoolException("DBCP", dbDriver, dbURL, e);
        }
        final PoolingDataSource ds = new PoolingDataSource(connectionPool);
        return new DBCPConnectionPool(ds, connectionPool);
    }
    
    public class DBCPConnectionPool implements ConnectionPool
    {
        final PoolingDataSource dataSource;
        final ObjectPool pool;
        
        public DBCPConnectionPool(final PoolingDataSource ds, final ObjectPool pool) {
            this.dataSource = ds;
            this.pool = pool;
        }
        
        @Override
        public void close() {
            try {
                this.pool.close();
            }
            catch (Exception ex) {}
        }
        
        @Override
        public DataSource getDataSource() {
            return this.dataSource;
        }
    }
}
