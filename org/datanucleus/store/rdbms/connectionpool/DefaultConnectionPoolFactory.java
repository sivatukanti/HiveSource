// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import java.util.Properties;
import javax.sql.DataSource;
import org.datanucleus.store.rdbms.datasource.DriverManagerDataSource;
import org.datanucleus.store.StoreManager;

public class DefaultConnectionPoolFactory implements ConnectionPoolFactory
{
    @Override
    public ConnectionPool createConnectionPool(final StoreManager storeMgr) {
        Properties props = AbstractConnectionPoolFactory.getPropertiesForDriver(storeMgr);
        if (props.size() == 2) {
            props = null;
        }
        return new DefaultConnectionPool(new DriverManagerDataSource(storeMgr.getConnectionDriverName(), storeMgr.getConnectionURL(), storeMgr.getConnectionUserName(), storeMgr.getConnectionPassword(), storeMgr.getNucleusContext().getClassLoaderResolver(null), props));
    }
    
    public class DefaultConnectionPool implements ConnectionPool
    {
        final DataSource dataSource;
        
        public DefaultConnectionPool(final DataSource ds) {
            this.dataSource = ds;
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public DataSource getDataSource() {
            return this.dataSource;
        }
    }
}
