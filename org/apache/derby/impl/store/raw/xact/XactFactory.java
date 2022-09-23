// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;

public class XactFactory implements TransactionFactory, ModuleControl, ModuleSupportable
{
    protected static final String USER_CONTEXT_ID = "UserTransaction";
    protected static final String NESTED_READONLY_USER_CONTEXT_ID = "NestedRawReadOnlyUserTransaction";
    protected static final String NESTED_UPDATE_USER_CONTEXT_ID = "NestedRawUpdateUserTransaction";
    protected static final String INTERNAL_CONTEXT_ID = "InternalTransaction";
    protected static final String NTT_CONTEXT_ID = "NestedTransaction";
    protected DaemonService rawStoreDaemon;
    private UUIDFactory uuidFactory;
    protected ContextService contextFactory;
    protected LockFactory lockFactory;
    protected LogFactory logFactory;
    protected DataFactory dataFactory;
    protected DataValueFactory dataValueFactory;
    protected RawStoreFactory rawStoreFactory;
    public TransactionTable ttab;
    private long tranId;
    private LockingPolicy[][] lockingPolicies;
    private boolean inCreateNoLog;
    private Object xa_resource;
    private Object backupSemaphore;
    private long backupBlockingOperations;
    private boolean inBackup;
    private static TransactionMapFactory mapFactory;
    static /* synthetic */ Class class$org$apache$derby$impl$store$raw$xact$XactFactory;
    
    public XactFactory() {
        this.lockingPolicies = new LockingPolicy[3][6];
        this.inCreateNoLog = false;
        this.backupSemaphore = new Object();
        this.backupBlockingOperations = 0L;
        this.inBackup = false;
        this.setMapFactory();
    }
    
    TransactionMapFactory createMapFactory() {
        return new TransactionMapFactory();
    }
    
    private void setMapFactory() {
        Class class$;
        Class class$org$apache$derby$impl$store$raw$xact$XactFactory;
        if (XactFactory.class$org$apache$derby$impl$store$raw$xact$XactFactory == null) {
            class$org$apache$derby$impl$store$raw$xact$XactFactory = (XactFactory.class$org$apache$derby$impl$store$raw$xact$XactFactory = (class$ = class$("org.apache.derby.impl.store.raw.xact.XactFactory")));
        }
        else {
            class$ = (class$org$apache$derby$impl$store$raw$xact$XactFactory = XactFactory.class$org$apache$derby$impl$store$raw$xact$XactFactory);
        }
        final Class clazz = class$org$apache$derby$impl$store$raw$xact$XactFactory;
        synchronized (class$) {
            if (XactFactory.mapFactory == null) {
                XactFactory.mapFactory = this.createMapFactory();
            }
        }
    }
    
    static synchronized TransactionMapFactory getMapFactory() {
        return XactFactory.mapFactory;
    }
    
    public boolean canSupport(final Properties properties) {
        return true;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        this.dataValueFactory = (DataValueFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.types.DataValueFactory", properties);
        this.contextFactory = ContextService.getFactory();
        this.lockFactory = (LockFactory)Monitor.bootServiceModule(false, this, "org.apache.derby.iapi.services.locks.LockFactory", properties);
        this.lockingPolicies[0][0] = new NoLocking();
        this.lockingPolicies[1][0] = new NoLocking();
        this.lockingPolicies[1][1] = new RowLocking1(this.lockFactory);
        this.lockingPolicies[1][2] = new RowLocking2(this.lockFactory);
        this.lockingPolicies[1][3] = new RowLocking2nohold(this.lockFactory);
        this.lockingPolicies[1][4] = new RowLockingRR(this.lockFactory);
        this.lockingPolicies[1][5] = new RowLocking3(this.lockFactory);
        this.lockingPolicies[2][0] = new NoLocking();
        this.lockingPolicies[2][1] = new ContainerLocking2(this.lockFactory);
        this.lockingPolicies[2][2] = new ContainerLocking2(this.lockFactory);
        this.lockingPolicies[2][3] = new ContainerLocking2(this.lockFactory);
        this.lockingPolicies[2][4] = new ContainerLocking3(this.lockFactory);
        this.lockingPolicies[2][5] = new ContainerLocking3(this.lockFactory);
        if (b) {
            this.ttab = new TransactionTable();
            final String property = properties.getProperty("derby.__rt.storage.createWithNoLog");
            this.inCreateNoLog = (property != null && Boolean.valueOf(property));
        }
    }
    
    public void stop() {
        if (this.rawStoreDaemon != null) {
            this.rawStoreDaemon.stop();
        }
    }
    
    public LockFactory getLockFactory() {
        return this.lockFactory;
    }
    
    public void createFinished() throws StandardException {
        if (!this.inCreateNoLog) {
            throw StandardException.newException("XSTB5.M");
        }
        if (this.ttab.hasActiveUpdateTransaction()) {
            throw StandardException.newException("XSTB5.M");
        }
        this.inCreateNoLog = false;
    }
    
    private RawTransaction startCommonTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager, final boolean b, final CompatibilitySpace compatibilitySpace, final String s, final String transName, final boolean b2, final boolean b3) throws StandardException {
        final Xact xact = new Xact(this, this.logFactory, this.dataFactory, this.dataValueFactory, b, compatibilitySpace, b3);
        xact.setTransName(transName);
        this.pushTransactionContext(contextManager, s, xact, false, rawStoreFactory, b2);
        return xact;
    }
    
    public RawTransaction startTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager, final String s) throws StandardException {
        return this.startCommonTransaction(rawStoreFactory, contextManager, false, null, "UserTransaction", s, true, true);
    }
    
    public RawTransaction startNestedReadOnlyUserTransaction(final RawStoreFactory rawStoreFactory, final CompatibilitySpace compatibilitySpace, final ContextManager contextManager, final String s) throws StandardException {
        return this.startCommonTransaction(rawStoreFactory, contextManager, true, compatibilitySpace, "NestedRawReadOnlyUserTransaction", s, false, true);
    }
    
    public RawTransaction startNestedUpdateUserTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager, final String s, final boolean b) throws StandardException {
        return this.startCommonTransaction(rawStoreFactory, contextManager, false, null, "NestedRawUpdateUserTransaction", s, true, b);
    }
    
    public RawTransaction startGlobalTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager, final int n, final byte[] array, final byte[] array2) throws StandardException {
        final GlobalXactId globalXactId = new GlobalXactId(n, array, array2);
        if (this.ttab.findTransactionContextByGlobalId(globalXactId) != null) {
            throw StandardException.newException("XSAX1.S");
        }
        final RawTransaction startCommonTransaction = this.startCommonTransaction(rawStoreFactory, contextManager, false, null, "UserTransaction", "UserTransaction", true, true);
        startCommonTransaction.setTransactionId(globalXactId, startCommonTransaction.getId());
        return startCommonTransaction;
    }
    
    public RawTransaction findUserTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager, final String s) throws StandardException {
        final XactContext xactContext = (XactContext)contextManager.getContext("UserTransaction");
        if (xactContext == null) {
            return this.startTransaction(rawStoreFactory, contextManager, s);
        }
        return xactContext.getTransaction();
    }
    
    public RawTransaction startNestedTopTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager) throws StandardException {
        final Xact xact = new Xact(this, this.logFactory, this.dataFactory, this.dataValueFactory, false, null, false);
        xact.setPostComplete();
        this.pushTransactionContext(contextManager, "NestedTransaction", xact, true, rawStoreFactory, true);
        return xact;
    }
    
    public RawTransaction startInternalTransaction(final RawStoreFactory rawStoreFactory, final ContextManager contextManager) throws StandardException {
        final InternalXact internalXact = new InternalXact(this, this.logFactory, this.dataFactory, this.dataValueFactory);
        this.pushTransactionContext(contextManager, "InternalTransaction", internalXact, true, rawStoreFactory, true);
        return internalXact;
    }
    
    public boolean findTransaction(final TransactionId transactionId, final RawTransaction rawTransaction) {
        return this.ttab.findAndAssumeTransaction(transactionId, rawTransaction);
    }
    
    public void rollbackAllTransactions(final RawTransaction rawTransaction, final RawStoreFactory rawStoreFactory) throws StandardException {
        int n = 0;
        if (this.ttab.hasRollbackFirstTransaction()) {
            final RawTransaction startInternalTransaction = this.startInternalTransaction(rawStoreFactory, rawTransaction.getContextManager());
            startInternalTransaction.recoveryTransaction();
            while (this.ttab.getMostRecentRollbackFirstTransaction(startInternalTransaction)) {
                ++n;
                startInternalTransaction.abort();
            }
            startInternalTransaction.close();
        }
        int n2 = 0;
        while (this.ttab.getMostRecentTransactionForRollback(rawTransaction)) {
            ++n2;
            rawTransaction.abort();
        }
    }
    
    public void handlePreparedXacts(final RawStoreFactory rawStoreFactory) throws StandardException {
        if (this.ttab.hasPreparedRecoveredXact()) {
            while (true) {
                final ContextManager contextManager = this.contextFactory.newContextManager();
                this.contextFactory.setCurrentContextManager(contextManager);
                try {
                    final RawTransaction startTransaction = this.startTransaction(this.rawStoreFactory, contextManager, "UserTransaction");
                    if (!this.ttab.getMostRecentPreparedRecoveredXact(startTransaction)) {
                        startTransaction.destroy();
                        break;
                    }
                    startTransaction.reprepare();
                }
                finally {
                    this.contextFactory.resetCurrentContextManager(contextManager);
                }
            }
        }
    }
    
    public LogInstant firstUpdateInstant() {
        return this.ttab.getFirstLogInstant();
    }
    
    public StandardException markCorrupt(final StandardException ex) {
        this.logFactory.markCorrupt(ex);
        return ex;
    }
    
    public void setNewTransactionId(final TransactionId transactionId, final Xact xact) {
        boolean remove = true;
        if (transactionId != null) {
            remove = this.remove(transactionId);
        }
        final XactId xactId;
        synchronized (this) {
            xactId = new XactId(this.tranId++);
        }
        xact.setTransactionId(xact.getGlobalId(), xactId);
        if (transactionId != null) {
            this.add(xact, remove);
        }
    }
    
    public void resetTranId() {
        final XactId xactId = (XactId)this.ttab.largestUpdateXactId();
        if (xactId != null) {
            this.tranId = xactId.getId() + 1L;
        }
        else {
            this.tranId = 1L;
        }
    }
    
    protected void pushTransactionContext(final ContextManager contextManager, final String s, final Xact xact, final boolean b, final RawStoreFactory rawStoreFactory, final boolean b2) throws StandardException {
        if (contextManager.getContext(s) != null) {
            throw StandardException.newException("XSTA2.S");
        }
        final XactContext xactContext = new XactContext(contextManager, s, xact, b, rawStoreFactory);
        this.add(xact, b2);
    }
    
    protected void addUpdateTransaction(final TransactionId transactionId, final RawTransaction rawTransaction, final int n) {
        this.ttab.addUpdateTransaction(transactionId, rawTransaction, n);
    }
    
    protected void removeUpdateTransaction(final TransactionId transactionId) {
        this.ttab.removeUpdateTransaction(transactionId);
    }
    
    protected void prepareTransaction(final TransactionId transactionId) {
        this.ttab.prepareTransaction(transactionId);
    }
    
    public boolean submitPostCommitWork(final Serviceable serviceable) {
        return this.rawStoreDaemon != null && this.rawStoreDaemon.enqueue(serviceable, serviceable.serviceASAP());
    }
    
    public void setRawStoreFactory(final RawStoreFactory rawStoreFactory) throws StandardException {
        this.rawStoreFactory = rawStoreFactory;
        this.rawStoreDaemon = rawStoreFactory.getDaemon();
        this.logFactory = (LogFactory)Monitor.findServiceModule(this, rawStoreFactory.getLogFactoryModule());
        this.dataFactory = (DataFactory)Monitor.findServiceModule(this, rawStoreFactory.getDataFactoryModule());
    }
    
    public boolean noActiveUpdateTransaction() {
        return !this.ttab.hasActiveUpdateTransaction();
    }
    
    public boolean hasPreparedXact() {
        return this.ttab.hasPreparedXact();
    }
    
    protected boolean remove(final TransactionId transactionId) {
        return this.ttab.remove(transactionId);
    }
    
    protected void add(final Xact xact, final boolean b) {
        this.ttab.add(xact, b);
    }
    
    public UUID makeNewUUID() {
        return this.uuidFactory.createUUID();
    }
    
    final LockingPolicy getLockingPolicy(int i, int n, final boolean b) {
        if (i == 0) {
            n = 0;
        }
        final LockingPolicy lockingPolicy = this.lockingPolicies[i][n];
        if (lockingPolicy != null || !b) {
            return lockingPolicy;
        }
        ++i;
        while (i <= 2) {
            for (int j = n; j <= 5; ++j) {
                final LockingPolicy lockingPolicy2 = this.lockingPolicies[i][j];
                if (lockingPolicy2 != null) {
                    return lockingPolicy2;
                }
            }
            ++i;
        }
        return null;
    }
    
    public Formatable getTransactionTable() {
        return this.ttab;
    }
    
    public void useTransactionTable(final Formatable formatable) throws StandardException {
        if (this.ttab != null && formatable != null) {
            throw StandardException.newException("XSTB6.M");
        }
        if (this.ttab == null) {
            if (formatable == null) {
                this.ttab = new TransactionTable();
            }
            else {
                this.ttab = (TransactionTable)formatable;
            }
        }
    }
    
    public TransactionInfo[] getTransactionInfo() {
        return this.ttab.getTransactionInfo();
    }
    
    public boolean inDatabaseCreation() {
        return this.inCreateNoLog;
    }
    
    public Object getXAResourceManager() throws StandardException {
        if (this.xa_resource == null) {
            this.xa_resource = new XactXAResourceManager(this.rawStoreFactory, this.ttab);
        }
        return this.xa_resource;
    }
    
    protected boolean blockBackup(final boolean b) {
        synchronized (this.backupSemaphore) {
            if (this.inBackup) {
                if (!b) {
                    return false;
                }
                while (this.inBackup) {
                    try {
                        this.backupSemaphore.wait();
                    }
                    catch (InterruptedException ex) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
            ++this.backupBlockingOperations;
            return true;
        }
    }
    
    protected void unblockBackup() {
        synchronized (this.backupSemaphore) {
            --this.backupBlockingOperations;
            if (this.inBackup) {
                this.backupSemaphore.notifyAll();
            }
        }
    }
    
    public boolean blockBackupBlockingOperations(final boolean b) {
        synchronized (this.backupSemaphore) {
            if (b) {
                this.inBackup = true;
                try {
                    while (this.backupBlockingOperations > 0L) {
                        try {
                            this.backupSemaphore.wait();
                        }
                        catch (InterruptedException ex2) {
                            InterruptStatus.setInterrupted();
                        }
                    }
                    return this.inBackup;
                }
                catch (RuntimeException ex) {
                    this.inBackup = false;
                    this.backupSemaphore.notifyAll();
                    throw ex;
                }
            }
            if (this.backupBlockingOperations == 0L) {
                this.inBackup = true;
            }
        }
        return this.inBackup;
    }
    
    public void unblockBackupBlockingOperations() {
        synchronized (this.backupSemaphore) {
            this.inBackup = false;
            this.backupSemaphore.notifyAll();
        }
    }
    
    static /* synthetic */ Class class$(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException cause) {
            throw new NoClassDefFoundError().initCause(cause);
        }
    }
}
