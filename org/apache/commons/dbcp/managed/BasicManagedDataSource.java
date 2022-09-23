// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import org.apache.commons.dbcp.PoolableConnectionFactory;
import java.util.Collection;
import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import java.sql.SQLException;
import org.apache.commons.dbcp.ConnectionFactory;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import org.apache.commons.dbcp.BasicDataSource;

public class BasicManagedDataSource extends BasicDataSource
{
    private TransactionRegistry transactionRegistry;
    private transient TransactionManager transactionManager;
    private String xaDataSource;
    private XADataSource xaDataSourceInstance;
    
    public synchronized XADataSource getXaDataSourceInstance() {
        return this.xaDataSourceInstance;
    }
    
    public synchronized void setXaDataSourceInstance(final XADataSource xaDataSourceInstance) {
        this.xaDataSourceInstance = xaDataSourceInstance;
        this.xaDataSource = ((xaDataSourceInstance == null) ? null : xaDataSourceInstance.getClass().getName());
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    protected synchronized TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }
    
    public void setTransactionManager(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public synchronized String getXADataSource() {
        return this.xaDataSource;
    }
    
    public synchronized void setXADataSource(final String xaDataSource) {
        this.xaDataSource = xaDataSource;
    }
    
    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        if (this.transactionManager == null) {
            throw new SQLException("Transaction manager must be set before a connection can be created");
        }
        if (this.xaDataSource == null) {
            final ConnectionFactory connectionFactory = super.createConnectionFactory();
            final XAConnectionFactory xaConnectionFactory = new LocalXAConnectionFactory(this.getTransactionManager(), connectionFactory);
            this.transactionRegistry = xaConnectionFactory.getTransactionRegistry();
            return xaConnectionFactory;
        }
        if (this.xaDataSourceInstance == null) {
            Class xaDataSourceClass = null;
            try {
                xaDataSourceClass = Class.forName(this.xaDataSource);
            }
            catch (Throwable t) {
                final String message = "Cannot load XA data source class '" + this.xaDataSource + "'";
                throw (SQLException)new SQLException(message).initCause(t);
            }
            try {
                this.xaDataSourceInstance = xaDataSourceClass.newInstance();
            }
            catch (Throwable t) {
                final String message = "Cannot create XA data source of class '" + this.xaDataSource + "'";
                throw (SQLException)new SQLException(message).initCause(t);
            }
        }
        final XAConnectionFactory xaConnectionFactory2 = new DataSourceXAConnectionFactory(this.getTransactionManager(), this.xaDataSourceInstance, this.username, this.password);
        this.transactionRegistry = xaConnectionFactory2.getTransactionRegistry();
        return xaConnectionFactory2;
    }
    
    @Override
    protected void createDataSourceInstance() throws SQLException {
        final PoolingDataSource pds = new ManagedDataSource(this.connectionPool, this.transactionRegistry);
        pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        pds.setLogWriter(this.logWriter);
        this.dataSource = pds;
    }
    
    @Override
    protected void createPoolableConnectionFactory(final ConnectionFactory driverConnectionFactory, final KeyedObjectPoolFactory statementPoolFactory, final AbandonedConfig abandonedConfig) throws SQLException {
        PoolableConnectionFactory connectionFactory = null;
        try {
            connectionFactory = new PoolableManagedConnectionFactory((XAConnectionFactory)driverConnectionFactory, this.connectionPool, statementPoolFactory, this.validationQuery, this.validationQueryTimeout, this.connectionInitSqls, this.defaultReadOnly, this.defaultAutoCommit, this.defaultTransactionIsolation, this.defaultCatalog, abandonedConfig);
            BasicDataSource.validateConnectionFactory(connectionFactory);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw (SQLException)new SQLException("Cannot create PoolableConnectionFactory (" + e2.getMessage() + ")").initCause(e2);
        }
    }
}
