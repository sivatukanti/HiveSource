// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import javax.sql.DataSource;
import org.datanucleus.ClassLoaderResolver;
import java.sql.SQLException;
import com.mchange.v2.c3p0.PooledDataSource;
import java.util.Properties;
import com.mchange.v2.c3p0.DataSources;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;

public class C3P0ConnectionPoolFactory extends AbstractConnectionPoolFactory
{
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        final String dbDriver = storeMgr.getConnectionDriverName();
        final String dbURL = storeMgr.getConnectionURL();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        this.loadDriver(dbDriver, clr);
        ClassUtils.assertClassForJarExistsInClasspath(clr, "com.mchange.v2.c3p0.DataSources", "c3p0.jar");
        try {
            final Properties dbProps = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
            final DataSource unpooled = DataSources.unpooledDataSource(dbURL, dbProps);
            final Properties c3p0Props = new Properties();
            if (storeMgr.hasProperty("datanucleus.connectionPool.maxStatements")) {
                final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxStatements");
                if (size >= 0) {
                    c3p0Props.setProperty("maxStatementsPerConnection", "" + size);
                    c3p0Props.setProperty("maxStatements", "" + size);
                }
            }
            if (storeMgr.hasProperty("datanucleus.connectionPool.maxPoolSize")) {
                final int size = storeMgr.getIntProperty("datanucleus.connectionPool.maxPoolSize");
                if (size >= 0) {
                    c3p0Props.setProperty("maxPoolSize", "" + size);
                }
            }
            if (storeMgr.hasProperty("datanucleus.connectionPool.minPoolSize")) {
                final int size = storeMgr.getIntProperty("datanucleus.connectionPool.minPoolSize");
                if (size >= 0) {
                    c3p0Props.setProperty("minPoolSize", "" + size);
                }
            }
            if (storeMgr.hasProperty("datanucleus.connectionPool.initialPoolSize")) {
                final int size = storeMgr.getIntProperty("datanucleus.connectionPool.initialPoolSize");
                if (size >= 0) {
                    c3p0Props.setProperty("initialPoolSize", "" + size);
                }
            }
            final PooledDataSource ds = (PooledDataSource)DataSources.pooledDataSource(unpooled, c3p0Props);
            return new C3P0ConnectionPool(ds);
        }
        catch (SQLException sqle) {
            throw new DatastorePoolException("c3p0", dbDriver, dbURL, sqle);
        }
    }
    
    public class C3P0ConnectionPool implements ConnectionPool
    {
        final PooledDataSource dataSource;
        
        public C3P0ConnectionPool(final PooledDataSource ds) {
            this.dataSource = ds;
        }
        
        @Override
        public void close() {
            try {
                this.dataSource.close();
            }
            catch (SQLException ex) {}
        }
        
        @Override
        public DataSource getDataSource() {
            return (DataSource)this.dataSource;
        }
    }
}
