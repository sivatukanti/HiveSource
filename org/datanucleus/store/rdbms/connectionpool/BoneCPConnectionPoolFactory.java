// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import javax.sql.DataSource;
import java.util.Properties;
import org.datanucleus.ClassLoaderResolver;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.BoneCPConfig;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;

public class BoneCPConnectionPoolFactory extends AbstractConnectionPoolFactory
{
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        final String dbDriver = storeMgr.getConnectionDriverName();
        final String dbURL = storeMgr.getConnectionURL();
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
        ClassUtils.assertClassForJarExistsInClasspath(clr, "com.jolbox.bonecp.BoneCPDataSource", "bonecp.jar");
        final BoneCPConfig config = new BoneCPConfig();
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        final Properties dbProps = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
        config.setDriverProperties(dbProps);
        final BoneCPDataSource ds = new BoneCPDataSource(config);
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxStatements")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxStatements");
            if (size >= 0) {
                ds.setStatementsCacheSize(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxPoolSize")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxPoolSize");
            if (size >= 0) {
                ds.setMaxConnectionsPerPartition(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.minPoolSize")) {
            final int size = storeMgr.getIntProperty("datanucleus.connectionPool.minPoolSize");
            if (size >= 0) {
                ds.setMinConnectionsPerPartition(size);
            }
        }
        if (storeMgr.hasProperty("datanucleus.connectionPool.maxIdle")) {
            final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxIdle");
            if (value > 0) {
                ds.setIdleMaxAgeInMinutes(value);
            }
        }
        ds.setJdbcUrl(dbURL);
        ds.setUsername(dbUser);
        ds.setPassword(dbPassword);
        return new BoneCPConnectionPool(ds);
    }
    
    public class BoneCPConnectionPool implements ConnectionPool
    {
        final BoneCPDataSource dataSource;
        
        public BoneCPConnectionPool(final BoneCPDataSource ds) {
            this.dataSource = ds;
        }
        
        @Override
        public void close() {
            this.dataSource.close();
        }
        
        @Override
        public DataSource getDataSource() {
            return this.dataSource;
        }
    }
}
