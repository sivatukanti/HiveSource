// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp.managed;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.sql.SQLException;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import javax.transaction.TransactionManager;
import org.apache.commons.dbcp.ConnectionFactory;

public class LocalXAConnectionFactory implements XAConnectionFactory
{
    protected TransactionRegistry transactionRegistry;
    protected ConnectionFactory connectionFactory;
    
    public LocalXAConnectionFactory(final TransactionManager transactionManager, final ConnectionFactory connectionFactory) {
        if (transactionManager == null) {
            throw new NullPointerException("transactionManager is null");
        }
        if (connectionFactory == null) {
            throw new NullPointerException("connectionFactory is null");
        }
        this.transactionRegistry = new TransactionRegistry(transactionManager);
        this.connectionFactory = connectionFactory;
    }
    
    @Override
    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        final Connection connection = this.connectionFactory.createConnection();
        final XAResource xaResource = new LocalXAResource(connection);
        this.transactionRegistry.registerConnection(connection, xaResource);
        return connection;
    }
    
    protected static class LocalXAResource implements XAResource
    {
        private final Connection connection;
        private Xid currentXid;
        private boolean originalAutoCommit;
        
        public LocalXAResource(final Connection localTransaction) {
            this.connection = localTransaction;
        }
        
        public synchronized Xid getXid() {
            return this.currentXid;
        }
        
        @Override
        public synchronized void start(final Xid xid, final int flag) throws XAException {
            if (flag == 0) {
                if (this.currentXid != null) {
                    throw new XAException("Already enlisted in another transaction with xid " + xid);
                }
                try {
                    this.originalAutoCommit = this.connection.getAutoCommit();
                }
                catch (SQLException ignored) {
                    this.originalAutoCommit = true;
                }
                try {
                    this.connection.setAutoCommit(false);
                }
                catch (SQLException e) {
                    throw (XAException)new XAException("Count not turn off auto commit for a XA transaction").initCause(e);
                }
                this.currentXid = xid;
            }
            else {
                if (flag != 134217728) {
                    throw new XAException("Unknown start flag " + flag);
                }
                if (xid != this.currentXid) {
                    throw new XAException("Attempting to resume in different transaction: expected " + this.currentXid + ", but was " + xid);
                }
            }
        }
        
        @Override
        public synchronized void end(final Xid xid, final int flag) throws XAException {
            if (xid == null) {
                throw new NullPointerException("xid is null");
            }
            if (!this.currentXid.equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
        }
        
        @Override
        public synchronized int prepare(final Xid xid) {
            try {
                if (this.connection.isReadOnly()) {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                    return 3;
                }
            }
            catch (SQLException ex) {}
            return 0;
        }
        
        @Override
        public synchronized void commit(final Xid xid, final boolean flag) throws XAException {
            if (xid == null) {
                throw new NullPointerException("xid is null");
            }
            if (!this.currentXid.equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
            try {
                if (this.connection.isClosed()) {
                    throw new XAException("Conection is closed");
                }
                if (!this.connection.isReadOnly()) {
                    this.connection.commit();
                }
            }
            catch (SQLException e) {
                throw (XAException)new XAException().initCause(e);
            }
            finally {
                try {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                }
                catch (SQLException ex) {}
                this.currentXid = null;
            }
        }
        
        @Override
        public synchronized void rollback(final Xid xid) throws XAException {
            if (xid == null) {
                throw new NullPointerException("xid is null");
            }
            if (!this.currentXid.equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
            try {
                this.connection.rollback();
            }
            catch (SQLException e) {
                throw (XAException)new XAException().initCause(e);
            }
            finally {
                try {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                }
                catch (SQLException ex) {}
                this.currentXid = null;
            }
        }
        
        @Override
        public boolean isSameRM(final XAResource xaResource) {
            return this == xaResource;
        }
        
        @Override
        public synchronized void forget(final Xid xid) {
            if (xid != null && this.currentXid.equals(xid)) {
                this.currentXid = null;
            }
        }
        
        @Override
        public Xid[] recover(final int flag) {
            return new Xid[0];
        }
        
        @Override
        public int getTransactionTimeout() {
            return 0;
        }
        
        @Override
        public boolean setTransactionTimeout(final int transactionTimeout) {
            return false;
        }
    }
}
