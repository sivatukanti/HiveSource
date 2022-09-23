// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.datanucleus.store.connection.ManagedConnectionResourceListener;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.transaction.TransactionUtils;
import org.datanucleus.store.connection.AbstractManagedConnection;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.JavaUtils;
import org.datanucleus.store.connection.ManagedConnection;
import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusException;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.store.rdbms.connectionpool.ConnectionPoolFactory;
import javax.naming.NamingException;
import org.datanucleus.exceptions.ConnectionFactoryNotFoundException;
import javax.naming.InitialContext;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.UnsupportedConnectionFactoryException;
import javax.sql.XADataSource;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.HashMap;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.connectionpool.ConnectionPool;
import javax.sql.DataSource;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.connection.AbstractConnectionFactory;

public class ConnectionFactoryImpl extends AbstractConnectionFactory
{
    protected static final Localiser LOCALISER_RDBMS;
    DataSource[] dataSources;
    ConnectionPool pool;
    
    public ConnectionFactoryImpl(final StoreManager storeMgr, final String resourceType) {
        super(storeMgr, resourceType);
        this.pool = null;
        if (resourceType.equals("tx")) {
            this.initialiseDataSources();
        }
    }
    
    @Override
    public void close() {
        if (this.pool != null) {
            if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047010", this.resourceType));
            }
            this.pool.close();
        }
        super.close();
    }
    
    protected synchronized void initialiseDataSources() {
        if (this.resourceType.equals("tx")) {
            final String configuredResourceTypeProperty = this.storeMgr.getStringProperty("datanucleus.connection.resourceType");
            if (configuredResourceTypeProperty != null) {
                if (this.options == null) {
                    this.options = new HashMap();
                }
                this.options.put("resource-type", configuredResourceTypeProperty);
            }
            final String requiredPoolingType = this.storeMgr.getStringProperty("datanucleus.connectionPoolingType");
            final Object connDS = this.storeMgr.getConnectionFactory();
            final String connJNDI = this.storeMgr.getConnectionFactoryName();
            final String connURL = this.storeMgr.getConnectionURL();
            this.dataSources = this.generateDataSources(this.storeMgr, connDS, connJNDI, this.resourceType, requiredPoolingType, connURL);
            if (this.dataSources == null) {
                throw new NucleusUserException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047009", "transactional")).setFatal();
            }
        }
        else {
            final String configuredResourceTypeProperty = this.storeMgr.getStringProperty("datanucleus.connection2.resourceType");
            if (configuredResourceTypeProperty != null) {
                if (this.options == null) {
                    this.options = new HashMap();
                }
                this.options.put("resource-type", configuredResourceTypeProperty);
            }
            String requiredPoolingType = this.storeMgr.getStringProperty("datanucleus.connectionPoolingType.nontx");
            if (requiredPoolingType == null) {
                requiredPoolingType = this.storeMgr.getStringProperty("datanucleus.connectionPoolingType");
            }
            Object connDS = this.storeMgr.getConnectionFactory2();
            String connJNDI = this.storeMgr.getConnectionFactory2Name();
            final String connURL = this.storeMgr.getConnectionURL();
            this.dataSources = this.generateDataSources(this.storeMgr, connDS, connJNDI, this.resourceType, requiredPoolingType, connURL);
            if (this.dataSources == null) {
                connDS = this.storeMgr.getConnectionFactory();
                connJNDI = this.storeMgr.getConnectionFactoryName();
                this.dataSources = this.generateDataSources(this.storeMgr, connDS, connJNDI, this.resourceType, requiredPoolingType, connURL);
            }
            if (this.dataSources == null) {
                throw new NucleusUserException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047009", "non-transactional")).setFatal();
            }
        }
    }
    
    private DataSource[] generateDataSources(final StoreManager storeMgr, final Object connDS, final String connJNDI, final String resourceType, final String requiredPoolingType, final String connURL) {
        DataSource[] dataSources = null;
        if (connDS != null) {
            if (!(connDS instanceof DataSource) && !(connDS instanceof XADataSource)) {
                throw new UnsupportedConnectionFactoryException(connDS);
            }
            dataSources = new DataSource[] { (DataSource)connDS };
        }
        else if (connJNDI != null) {
            final String[] connectionFactoryNames = StringUtils.split(connJNDI, ",");
            dataSources = new DataSource[connectionFactoryNames.length];
            for (int i = 0; i < connectionFactoryNames.length; ++i) {
                Object obj;
                try {
                    obj = new InitialContext().lookup(connectionFactoryNames[i]);
                }
                catch (NamingException e) {
                    throw new ConnectionFactoryNotFoundException(connectionFactoryNames[i], e);
                }
                if (!(obj instanceof DataSource) && !(obj instanceof XADataSource)) {
                    throw new UnsupportedConnectionFactoryException(obj);
                }
                dataSources[i] = (DataSource)obj;
            }
        }
        else if (connURL != null) {
            dataSources = new DataSource[] { null };
            final String poolingType = calculatePoolingType(storeMgr, requiredPoolingType);
            try {
                final ConnectionPoolFactory connPoolFactory = (ConnectionPoolFactory)storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.connectionpool", "name", poolingType, "class-name", null, null);
                if (connPoolFactory == null) {
                    throw new NucleusUserException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047003", poolingType)).setFatal();
                }
                this.pool = connPoolFactory.createConnectionPool(storeMgr);
                dataSources[0] = this.pool.getDataSource();
                if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                    NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047008", resourceType, poolingType));
                }
            }
            catch (ClassNotFoundException cnfe) {
                throw new NucleusUserException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047003", poolingType), cnfe).setFatal();
            }
            catch (Exception e2) {
                if (e2 instanceof InvocationTargetException) {
                    final InvocationTargetException ite = (InvocationTargetException)e2;
                    throw new NucleusException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047004", poolingType, ite.getTargetException().getMessage()), ite.getTargetException()).setFatal();
                }
                throw new NucleusException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("047004", poolingType, e2.getMessage()), e2).setFatal();
            }
        }
        return dataSources;
    }
    
    @Override
    public ManagedConnection createManagedConnection(final ExecutionContext ec, final Map txnOptions) {
        if (this.dataSources == null) {
            this.initialiseDataSources();
        }
        final ManagedConnection mconn = new ManagedConnectionImpl(txnOptions);
        if (this.resourceType.equalsIgnoreCase("nontx")) {
            final boolean releaseAfterUse = this.storeMgr.getBooleanProperty("datanucleus.connection.nontx.releaseAfterUse");
            if (!releaseAfterUse) {
                mconn.setCloseOnRelease(false);
            }
        }
        return mconn;
    }
    
    protected static String calculatePoolingType(final StoreManager storeMgr, final String requiredPoolingType) {
        String poolingType = requiredPoolingType;
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        if (poolingType != null) {
            if (poolingType.equalsIgnoreCase("DBCP") && !dbcpPresent(clr)) {
                NucleusLogger.CONNECTION.warn("DBCP specified but not present in CLASSPATH (or one of dependencies)");
                poolingType = null;
            }
            else if (poolingType.equalsIgnoreCase("C3P0") && !c3p0Present(clr)) {
                NucleusLogger.CONNECTION.warn("C3P0 specified but not present in CLASSPATH (or one of dependencies)");
                poolingType = null;
            }
            else if (poolingType.equalsIgnoreCase("Proxool") && !proxoolPresent(clr)) {
                NucleusLogger.CONNECTION.warn("Proxool specified but not present in CLASSPATH (or one of dependencies)");
                poolingType = null;
            }
            else if (poolingType.equalsIgnoreCase("BoneCP") && !bonecpPresent(clr)) {
                NucleusLogger.CONNECTION.warn("BoneCP specified but not present in CLASSPATH (or one of dependencies)");
                poolingType = null;
            }
        }
        if (poolingType == null && dbcpPresent(clr)) {
            poolingType = "DBCP";
        }
        if (poolingType == null && c3p0Present(clr)) {
            poolingType = "C3P0";
        }
        if (poolingType == null && proxoolPresent(clr)) {
            poolingType = "Proxool";
        }
        if (poolingType == null && bonecpPresent(clr)) {
            poolingType = "BoneCP";
        }
        if (poolingType == null) {
            if (JavaUtils.isJRE1_6OrAbove()) {
                poolingType = "dbcp-builtin";
            }
            else {
                poolingType = "None";
            }
        }
        return poolingType;
    }
    
    protected static boolean dbcpPresent(final ClassLoaderResolver clr) {
        try {
            clr.classForName("org.apache.commons.pool.ObjectPool");
            clr.classForName("org.apache.commons.dbcp.ConnectionFactory");
            return true;
        }
        catch (ClassNotResolvedException cnre) {
            return false;
        }
    }
    
    protected static boolean c3p0Present(final ClassLoaderResolver clr) {
        try {
            clr.classForName("com.mchange.v2.c3p0.ComboPooledDataSource");
            return true;
        }
        catch (ClassNotResolvedException cnre) {
            return false;
        }
    }
    
    protected static boolean proxoolPresent(final ClassLoaderResolver clr) {
        try {
            clr.classForName("org.logicalcobwebs.proxool.ProxoolDriver");
            clr.classForName("org.apache.commons.logging.Log");
            return true;
        }
        catch (ClassNotResolvedException cnre) {
            return false;
        }
    }
    
    protected static boolean bonecpPresent(final ClassLoaderResolver clr) {
        try {
            clr.classForName("com.jolbox.bonecp.BoneCPDataSource");
            clr.classForName("org.slf4j.Logger");
            clr.classForName("com.google.common.collect.Multiset");
            return true;
        }
        catch (ClassNotResolvedException cnre) {
            return false;
        }
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    class ManagedConnectionImpl extends AbstractManagedConnection
    {
        int isolation;
        boolean needsCommitting;
        ConnectionProvider connProvider;
        
        ManagedConnectionImpl(final Map txnOptions) {
            this.needsCommitting = false;
            this.connProvider = null;
            if (txnOptions != null && txnOptions.get("transaction.isolation") != null) {
                this.isolation = txnOptions.get("transaction.isolation").intValue();
            }
            else {
                this.isolation = TransactionUtils.getTransactionIsolationLevelForName(ConnectionFactoryImpl.this.storeMgr.getStringProperty("datanucleus.transactionIsolation"));
            }
            try {
                this.connProvider = (ConnectionProvider)ConnectionFactoryImpl.this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.connectionprovider", "name", ConnectionFactoryImpl.this.storeMgr.getStringProperty("datanucleus.rdbms.connectionProviderName"), "class-name", null, null);
                if (this.connProvider == null) {
                    throw new NucleusException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("050000", ConnectionFactoryImpl.this.storeMgr.getStringProperty("datanucleus.rdbms.connectionProviderName"))).setFatal();
                }
                this.connProvider.setFailOnError(ConnectionFactoryImpl.this.storeMgr.getBooleanProperty("datanucleus.rdbms.connectionProviderFailOnError"));
            }
            catch (Exception e) {
                throw new NucleusException(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("050001", ConnectionFactoryImpl.this.storeMgr.getStringProperty("datanucleus.rdbms.connectionProviderName"), e.getMessage()), e).setFatal();
            }
        }
        
        @Override
        public void release() {
            if (this.commitOnRelease) {
                try {
                    final Connection conn = this.getSqlConnection();
                    if (conn != null && !conn.isClosed() && !conn.getAutoCommit()) {
                        ((RDBMSStoreManager)ConnectionFactoryImpl.this.storeMgr).getSQLController().processConnectionStatement(this);
                        this.needsCommitting = false;
                        conn.commit();
                        if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                            NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052005", StringUtils.toJVMIDString(conn)));
                        }
                    }
                }
                catch (SQLException sqle) {
                    throw new NucleusDataStoreException(sqle.getMessage(), sqle);
                }
            }
            super.release();
        }
        
        @Override
        public XAResource getXAResource() {
            if (this.getConnection() instanceof Connection) {
                return new EmulatedXAResource((Connection)this.getConnection());
            }
            try {
                return ((XAConnection)this.getConnection()).getXAResource();
            }
            catch (SQLException e) {
                throw new NucleusDataStoreException(e.getMessage(), e);
            }
        }
        
        @Override
        public Object getConnection() {
            if (this.conn == null) {
                Connection cnx = null;
                try {
                    final RDBMSStoreManager rdbmsMgr = (RDBMSStoreManager)ConnectionFactoryImpl.this.storeMgr;
                    final boolean readOnly = ConnectionFactoryImpl.this.storeMgr.getBooleanProperty("datanucleus.readOnlyDatastore");
                    if (rdbmsMgr.getDatastoreAdapter() != null) {
                        final DatastoreAdapter rdba = rdbmsMgr.getDatastoreAdapter();
                        int reqdIsolationLevel = this.isolation;
                        if (rdba.getRequiredTransactionIsolationLevel() >= 0) {
                            reqdIsolationLevel = rdba.getRequiredTransactionIsolationLevel();
                        }
                        cnx = this.connProvider.getConnection(ConnectionFactoryImpl.this.dataSources);
                        boolean succeeded = false;
                        try {
                            if (cnx.isReadOnly() != readOnly) {
                                NucleusLogger.CONNECTION.debug("Setting readonly=" + readOnly + " to connection: " + cnx.toString());
                                cnx.setReadOnly(readOnly);
                            }
                            if (reqdIsolationLevel == 0) {
                                if (!cnx.getAutoCommit()) {
                                    cnx.setAutoCommit(true);
                                }
                            }
                            else {
                                if (cnx.getAutoCommit()) {
                                    cnx.setAutoCommit(false);
                                }
                                if (rdba.supportsTransactionIsolation(reqdIsolationLevel)) {
                                    final int currentIsolationLevel = cnx.getTransactionIsolation();
                                    if (currentIsolationLevel != reqdIsolationLevel) {
                                        cnx.setTransactionIsolation(reqdIsolationLevel);
                                    }
                                }
                                else {
                                    NucleusLogger.CONNECTION.warn(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("051008", reqdIsolationLevel));
                                }
                            }
                            if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                                NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052002", StringUtils.toJVMIDString(cnx), TransactionUtils.getNameForTransactionIsolationLevel(reqdIsolationLevel), cnx.getAutoCommit()));
                            }
                            if (reqdIsolationLevel != this.isolation && this.isolation == 0 && !cnx.getAutoCommit()) {
                                NucleusLogger.CONNECTION.debug("Setting autocommit=true for connection: " + StringUtils.toJVMIDString(cnx));
                                cnx.setAutoCommit(true);
                            }
                            succeeded = true;
                        }
                        catch (SQLException e) {
                            throw new NucleusDataStoreException(e.getMessage(), e);
                        }
                        finally {
                            if (!succeeded) {
                                try {
                                    cnx.close();
                                }
                                catch (SQLException ex) {}
                                if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                                    NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052003", StringUtils.toJVMIDString(cnx)));
                                }
                            }
                        }
                    }
                    else {
                        cnx = ConnectionFactoryImpl.this.dataSources[0].getConnection();
                        if (cnx == null) {
                            final String msg = ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052000", ConnectionFactoryImpl.this.dataSources[0]);
                            NucleusLogger.CONNECTION.error(msg);
                            throw new NucleusDataStoreException(msg);
                        }
                        if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                            NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052001", StringUtils.toJVMIDString(cnx)));
                        }
                    }
                }
                catch (SQLException e2) {
                    throw new NucleusDataStoreException(e2.getMessage(), e2);
                }
                this.conn = cnx;
            }
            this.needsCommitting = true;
            return this.conn;
        }
        
        @Override
        public void close() {
            for (int i = 0; i < this.listeners.size(); ++i) {
                this.listeners.get(i).managedConnectionPreClose();
            }
            final Connection conn = this.getSqlConnection();
            if (conn != null) {
                try {
                    if (this.commitOnRelease && this.needsCommitting && !conn.isClosed() && !conn.getAutoCommit()) {
                        final SQLController sqlController = ((RDBMSStoreManager)ConnectionFactoryImpl.this.storeMgr).getSQLController();
                        if (sqlController != null) {
                            sqlController.processConnectionStatement(this);
                        }
                        conn.commit();
                        this.needsCommitting = false;
                        if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                            NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052005", StringUtils.toJVMIDString(conn)));
                        }
                    }
                }
                catch (SQLException sqle) {
                    throw new NucleusDataStoreException(sqle.getMessage(), sqle);
                }
                finally {
                    try {
                        if (!conn.isClosed()) {
                            conn.close();
                            if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                                NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052003", StringUtils.toJVMIDString(conn)));
                            }
                        }
                        else if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                            NucleusLogger.CONNECTION.debug(ConnectionFactoryImpl.LOCALISER_RDBMS.msg("052004", StringUtils.toJVMIDString(conn)));
                        }
                    }
                    catch (SQLException sqle2) {
                        throw new NucleusDataStoreException(sqle2.getMessage(), sqle2);
                    }
                }
            }
            try {
                for (int j = 0; j < this.listeners.size(); ++j) {
                    this.listeners.get(j).managedConnectionPostClose();
                }
            }
            finally {
                this.listeners.clear();
            }
            this.conn = null;
        }
        
        private Connection getSqlConnection() {
            if (this.conn != null && this.conn instanceof Connection) {
                return (Connection)this.conn;
            }
            if (this.conn != null && this.conn instanceof XAConnection) {
                try {
                    return ((XAConnection)this.conn).getConnection();
                }
                catch (SQLException e) {
                    throw new NucleusDataStoreException(e.getMessage(), e);
                }
            }
            return null;
        }
    }
    
    static class EmulatedXAResource implements XAResource
    {
        Connection conn;
        
        EmulatedXAResource(final Connection conn) {
            this.conn = conn;
        }
        
        @Override
        public void commit(final Xid xid, final boolean onePhase) throws XAException {
            NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " is committing for transaction " + xid.toString() + " with onePhase=" + onePhase);
            try {
                this.conn.commit();
                NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " committed connection for transaction " + xid.toString() + " with onePhase=" + onePhase);
            }
            catch (SQLException e) {
                NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " failed to commit connection for transaction " + xid.toString() + " with onePhase=" + onePhase);
                final XAException xe = new XAException(StringUtils.getStringFromStackTrace(e));
                xe.initCause(e);
                throw xe;
            }
        }
        
        @Override
        public void end(final Xid xid, final int flags) throws XAException {
            NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " is ending for transaction " + xid.toString() + " with flags " + flags);
        }
        
        @Override
        public void forget(final Xid arg0) throws XAException {
        }
        
        @Override
        public int getTransactionTimeout() throws XAException {
            return 0;
        }
        
        @Override
        public boolean isSameRM(final XAResource xares) throws XAException {
            return this == xares;
        }
        
        @Override
        public int prepare(final Xid xid) throws XAException {
            NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " is preparing for transaction " + xid.toString());
            return 0;
        }
        
        @Override
        public Xid[] recover(final int flags) throws XAException {
            throw new XAException("Unsupported operation");
        }
        
        @Override
        public void rollback(final Xid xid) throws XAException {
            NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " is rolling back for transaction " + xid.toString());
            try {
                this.conn.rollback();
                NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " rolled back connection for transaction " + xid.toString());
            }
            catch (SQLException e) {
                NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " failed to rollback connection for transaction " + xid.toString());
                final XAException xe = new XAException(StringUtils.getStringFromStackTrace(e));
                xe.initCause(e);
                throw xe;
            }
        }
        
        @Override
        public boolean setTransactionTimeout(final int arg0) throws XAException {
            return false;
        }
        
        @Override
        public void start(final Xid xid, final int flags) throws XAException {
            NucleusLogger.CONNECTION.debug("Managed connection " + this.toString() + " is starting for transaction " + xid.toString() + " with flags " + flags);
        }
    }
}
