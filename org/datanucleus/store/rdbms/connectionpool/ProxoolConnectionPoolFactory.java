// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import javax.sql.DataSource;
import java.util.Properties;
import org.datanucleus.ClassLoaderResolver;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.StoreManager;

public class ProxoolConnectionPoolFactory extends AbstractConnectionPoolFactory
{
    private static int poolNumber;
    
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        final String dbDriver = storeMgr.getConnectionDriverName();
        final String dbURL = storeMgr.getConnectionURL();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        this.loadDriver(dbDriver, clr);
        ClassUtils.assertClassForJarExistsInClasspath(clr, "org.apache.commons.logging.Log", "commons-logging.jar");
        ClassUtils.assertClassForJarExistsInClasspath(clr, "org.logicalcobwebs.proxool.ProxoolDriver", "proxool.jar");
        final String alias = "datanucleus" + ProxoolConnectionPoolFactory.poolNumber;
        String poolURL = null;
        try {
            final Properties dbProps = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
            if (storeMgr.hasProperty("datanucleus.connectionPool.maxConnections")) {
                final int value = storeMgr.getIntProperty("datanucleus.connectionPool.maxConnections");
                if (value > 0) {
                    dbProps.put("proxool.maximum-connection-count", "" + value);
                }
                else {
                    dbProps.put("proxool.maximum-connection-count", "10");
                }
            }
            else {
                dbProps.put("proxool.maximum-connection-count", "10");
            }
            if (storeMgr.hasProperty("datanucleus.connectionPool.testSQL")) {
                final String value2 = storeMgr.getStringProperty("datanucleus.connectionPool.testSQL");
                dbProps.put("proxool.house-keeping-test-sql", value2);
            }
            else {
                dbProps.put("proxool.house-keeping-test-sql", "SELECT 1");
            }
            poolURL = "proxool." + alias + ":" + dbDriver + ":" + dbURL;
            ++ProxoolConnectionPoolFactory.poolNumber;
            ProxoolFacade.registerConnectionPool(poolURL, dbProps);
        }
        catch (ProxoolException pe) {
            pe.printStackTrace();
            throw new DatastorePoolException("Proxool", dbDriver, dbURL, (Exception)pe);
        }
        final ProxoolDataSource ds = new ProxoolDataSource(alias);
        return new ProxoolConnectionPool(ds, poolURL);
    }
    
    static {
        ProxoolConnectionPoolFactory.poolNumber = 0;
    }
    
    public class ProxoolConnectionPool implements ConnectionPool
    {
        final String poolURL;
        final ProxoolDataSource dataSource;
        
        public ProxoolConnectionPool(final ProxoolDataSource ds, final String poolURL) {
            this.dataSource = ds;
            this.poolURL = poolURL;
        }
        
        @Override
        public void close() {
            try {
                ProxoolFacade.removeConnectionPool(this.poolURL);
            }
            catch (ProxoolException ex) {}
        }
        
        @Override
        public DataSource getDataSource() {
            return (DataSource)this.dataSource;
        }
    }
}
