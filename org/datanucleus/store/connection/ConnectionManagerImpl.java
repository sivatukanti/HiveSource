// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import org.datanucleus.store.federation.FederatedStoreManager;
import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.TransactionEventListener;
import javax.transaction.xa.XAResource;
import org.datanucleus.Transaction;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ExecutionContext;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;

public class ConnectionManagerImpl implements ConnectionManager
{
    protected static final Localiser LOCALISER;
    NucleusContext nucleusContext;
    Map<String, ConnectionFactory> factories;
    ManagedConnectionPool connectionPool;
    boolean connectionPoolEnabled;
    
    public ConnectionManagerImpl(final NucleusContext context) {
        this.factories = new HashMap<String, ConnectionFactory>();
        this.connectionPool = new ManagedConnectionPool();
        this.connectionPoolEnabled = true;
        this.nucleusContext = context;
    }
    
    @Override
    public void closeAllConnections(final ConnectionFactory factory, final ExecutionContext ec) {
        if (ec != null && this.connectionPoolEnabled) {
            final ManagedConnection mconnFromPool = this.connectionPool.getManagedConnection(factory, ec);
            if (mconnFromPool != null) {
                if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                    NucleusLogger.CONNECTION.debug("Connection found in the pool : " + mconnFromPool + " for key=" + ec + " in factory=" + factory + " but owner object closing so closing connection");
                }
                mconnFromPool.close();
            }
        }
    }
    
    @Override
    public ManagedConnection allocateConnection(final ConnectionFactory factory, final ExecutionContext ec, final Transaction transaction, final Map options) {
        if (ec != null && this.connectionPoolEnabled) {
            final ManagedConnection mconnFromPool = this.connectionPool.getManagedConnection(factory, ec);
            if (mconnFromPool != null) {
                if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                    NucleusLogger.CONNECTION.debug("Connection found in the pool : " + mconnFromPool + " for key=" + ec + " in factory=" + factory);
                }
                if (!mconnFromPool.closeAfterTransactionEnd()) {
                    if (transaction.isActive()) {
                        if (mconnFromPool.commitOnRelease()) {
                            mconnFromPool.setCommitOnRelease(false);
                        }
                        if (mconnFromPool.closeOnRelease()) {
                            mconnFromPool.setCloseOnRelease(false);
                        }
                        final XAResource res = mconnFromPool.getXAResource();
                        final org.datanucleus.transaction.Transaction tx = this.nucleusContext.getTransactionManager().getTransaction(ec);
                        if (res != null && !tx.isEnlisted(res)) {
                            boolean enlistInLocalTM = true;
                            if (options != null && options.get("resource-type") != null && ConnectionResourceType.JTA.toString().equalsIgnoreCase(options.get("resource-type"))) {
                                enlistInLocalTM = false;
                            }
                            if (enlistInLocalTM) {
                                tx.enlistResource(res);
                            }
                        }
                    }
                    else {
                        if (!mconnFromPool.commitOnRelease()) {
                            mconnFromPool.setCommitOnRelease(true);
                        }
                        if (mconnFromPool.closeOnRelease()) {
                            mconnFromPool.setCloseOnRelease(false);
                        }
                    }
                }
                return mconnFromPool;
            }
        }
        final ManagedConnection mconn = factory.createManagedConnection(ec, (options == null && transaction != null) ? transaction.getOptions() : options);
        if (ec != null) {
            if (transaction.isActive()) {
                this.configureTransactionEventListener(transaction, mconn);
                final org.datanucleus.transaction.Transaction tx2 = this.nucleusContext.getTransactionManager().getTransaction(ec);
                mconn.setCommitOnRelease(false);
                mconn.setCloseOnRelease(false);
                final XAResource res2 = mconn.getXAResource();
                if (res2 != null) {
                    boolean enlistInLocalTM = true;
                    if (options != null && options.get("resource-type") != null && ConnectionResourceType.JTA.toString().equalsIgnoreCase(options.get("resource-type"))) {
                        enlistInLocalTM = false;
                    }
                    if (enlistInLocalTM) {
                        tx2.enlistResource(res2);
                    }
                }
            }
            if (this.connectionPoolEnabled) {
                mconn.addListener(new ManagedConnectionResourceListener() {
                    @Override
                    public void transactionFlushed() {
                    }
                    
                    @Override
                    public void transactionPreClose() {
                    }
                    
                    @Override
                    public void managedConnectionPreClose() {
                    }
                    
                    @Override
                    public void managedConnectionPostClose() {
                        if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                            NucleusLogger.CONNECTION.debug("Connection removed from the pool : " + mconn + " for key=" + ec + " in factory=" + factory);
                        }
                        ConnectionManagerImpl.this.connectionPool.removeManagedConnection(factory, ec);
                        mconn.removeListener(this);
                    }
                    
                    @Override
                    public void resourcePostClose() {
                    }
                });
                if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                    NucleusLogger.CONNECTION.debug("Connection added to the pool : " + mconn + " for key=" + ec + " in factory=" + factory);
                }
                this.connectionPool.putManagedConnection(factory, ec, mconn);
            }
        }
        return mconn;
    }
    
    private void configureTransactionEventListener(final Transaction transaction, final ManagedConnection mconn) {
        if (mconn.closeAfterTransactionEnd()) {
            transaction.addTransactionEventListener(new TransactionEventListener() {
                @Override
                public void transactionStarted() {
                }
                
                @Override
                public void transactionRolledBack() {
                    try {
                        mconn.close();
                    }
                    finally {
                        transaction.removeTransactionEventListener(this);
                    }
                }
                
                @Override
                public void transactionCommitted() {
                    try {
                        mconn.close();
                    }
                    finally {
                        transaction.removeTransactionEventListener(this);
                    }
                }
                
                @Override
                public void transactionEnded() {
                    try {
                        mconn.close();
                    }
                    finally {
                        transaction.removeTransactionEventListener(this);
                    }
                }
                
                @Override
                public void transactionPreCommit() {
                    if (mconn.isLocked()) {
                        throw new NucleusUserException(ConnectionManagerImpl.LOCALISER.msg("009000"));
                    }
                    mconn.transactionPreClose();
                }
                
                @Override
                public void transactionPreRollBack() {
                    if (mconn.isLocked()) {
                        throw new NucleusUserException(ConnectionManagerImpl.LOCALISER.msg("009000"));
                    }
                    mconn.transactionPreClose();
                }
                
                @Override
                public void transactionPreFlush() {
                }
                
                @Override
                public void transactionFlushed() {
                    mconn.transactionFlushed();
                }
            });
        }
        else {
            transaction.bindTransactionEventListener(new TransactionEventListener() {
                @Override
                public void transactionStarted() {
                }
                
                @Override
                public void transactionPreFlush() {
                }
                
                @Override
                public void transactionFlushed() {
                    mconn.transactionFlushed();
                }
                
                @Override
                public void transactionPreCommit() {
                    if (mconn.isLocked()) {
                        throw new NucleusUserException(ConnectionManagerImpl.LOCALISER.msg("009000"));
                    }
                    mconn.transactionPreClose();
                }
                
                @Override
                public void transactionCommitted() {
                }
                
                @Override
                public void transactionPreRollBack() {
                    if (mconn.isLocked()) {
                        throw new NucleusUserException(ConnectionManagerImpl.LOCALISER.msg("009000"));
                    }
                    mconn.transactionPreClose();
                }
                
                @Override
                public void transactionRolledBack() {
                }
                
                @Override
                public void transactionEnded() {
                }
            });
        }
    }
    
    @Override
    public ConnectionFactory lookupConnectionFactory(final String name) {
        return this.factories.get(name);
    }
    
    @Override
    public void registerConnectionFactory(final String name, final ConnectionFactory factory) {
        this.factories.put(name, factory);
    }
    
    @Override
    public void disableConnectionPool() {
        this.connectionPoolEnabled = false;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    class ManagedConnectionPool
    {
        Map<Object, Map<ConnectionFactory, ManagedConnection>> connectionsPool;
        
        ManagedConnectionPool() {
            this.connectionsPool = new HashMap<Object, Map<ConnectionFactory, ManagedConnection>>();
        }
        
        public void removeManagedConnection(final ConnectionFactory factory, final ExecutionContext ec) {
            synchronized (this.connectionsPool) {
                final Object poolKey = this.getPoolKey(factory, ec);
                final Map connectionsForPool = this.connectionsPool.get(poolKey);
                if (connectionsForPool != null) {
                    if (connectionsForPool.remove(factory) != null && ConnectionManagerImpl.this.nucleusContext.getStatistics() != null) {
                        ConnectionManagerImpl.this.nucleusContext.getStatistics().decrementActiveConnections();
                    }
                    if (connectionsForPool.size() == 0) {
                        this.connectionsPool.remove(poolKey);
                    }
                }
            }
        }
        
        public ManagedConnection getManagedConnection(final ConnectionFactory factory, final ExecutionContext ec) {
            synchronized (this.connectionsPool) {
                final Object poolKey = this.getPoolKey(factory, ec);
                final Map<ConnectionFactory, ManagedConnection> connectionsForEC = this.connectionsPool.get(poolKey);
                if (connectionsForEC == null) {
                    return null;
                }
                final ManagedConnection mconn = connectionsForEC.get(factory);
                if (mconn != null) {
                    if (mconn.isLocked()) {
                        throw new NucleusUserException(ConnectionManagerImpl.LOCALISER.msg("009000"));
                    }
                    return mconn;
                }
            }
            return null;
        }
        
        public void putManagedConnection(final ConnectionFactory factory, final ExecutionContext ec, final ManagedConnection mconn) {
            synchronized (this.connectionsPool) {
                final Object poolKey = this.getPoolKey(factory, ec);
                Map connectionsForOM = this.connectionsPool.get(poolKey);
                if (connectionsForOM == null) {
                    connectionsForOM = new HashMap();
                    this.connectionsPool.put(poolKey, connectionsForOM);
                }
                if (connectionsForOM.put(factory, mconn) == null && ConnectionManagerImpl.this.nucleusContext.getStatistics() != null) {
                    ConnectionManagerImpl.this.nucleusContext.getStatistics().incrementActiveConnections();
                }
            }
        }
        
        Object getPoolKey(final ConnectionFactory factory, final ExecutionContext ec) {
            if (ec.getStoreManager() instanceof FederatedStoreManager) {
                return new PoolKey(factory, ec);
            }
            return ec;
        }
    }
    
    class PoolKey
    {
        ConnectionFactory factory;
        ExecutionContext ec;
        
        public PoolKey(final ConnectionFactory factory, final ExecutionContext ec) {
            this.factory = factory;
            this.ec = ec;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof PoolKey)) {
                return false;
            }
            final PoolKey other = (PoolKey)obj;
            return this.factory == other.factory && this.ec == other.ec;
        }
        
        @Override
        public int hashCode() {
            return this.factory.hashCode() ^ this.ec.hashCode();
        }
    }
}
