// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import java.sql.SQLException;
import javax.transaction.xa.XAResource;
import javax.sql.XAConnection;
import java.sql.Connection;
import javax.transaction.TransactionManager;
import javax.sql.XADataSource;

public class DataSourceXAConnectionFactory implements XAConnectionFactory
{
    protected TransactionRegistry transactionRegistry;
    protected XADataSource xaDataSource;
    protected String username;
    protected String password;
    
    public DataSourceXAConnectionFactory(final TransactionManager transactionManager, final XADataSource xaDataSource) {
        this(transactionManager, xaDataSource, null, null);
    }
    
    public DataSourceXAConnectionFactory(final TransactionManager transactionManager, final XADataSource xaDataSource, final String username, final String password) {
        if (transactionManager == null) {
            throw new NullPointerException("transactionManager is null");
        }
        if (xaDataSource == null) {
            throw new NullPointerException("xaDataSource is null");
        }
        this.transactionRegistry = new TransactionRegistry(transactionManager);
        this.xaDataSource = xaDataSource;
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    @Override
    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        XAConnection xaConnection;
        if (this.username == null) {
            xaConnection = this.xaDataSource.getXAConnection();
        }
        else {
            xaConnection = this.xaDataSource.getXAConnection(this.username, this.password);
        }
        final Connection connection = xaConnection.getConnection();
        final XAResource xaResource = xaConnection.getXAResource();
        this.transactionRegistry.registerConnection(connection, xaResource);
        return connection;
    }
}
