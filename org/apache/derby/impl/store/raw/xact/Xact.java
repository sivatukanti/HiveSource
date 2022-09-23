// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.RecordHandle;
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;
import org.apache.derby.iapi.store.access.RowSource;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerKey;
import java.util.ArrayList;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import java.util.List;
import java.util.Stack;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.log.Logger;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.services.locks.LockOwner;
import org.apache.derby.iapi.services.locks.Limit;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public class Xact extends RawTransaction implements Limit, LockOwner
{
    protected static final int CLOSED = 0;
    protected static final int IDLE = 1;
    protected static final int ACTIVE = 2;
    protected static final int UPDATE = 3;
    protected static final int PREPARED = 4;
    public static final int END_ABORTED = 1;
    public static final int END_PREPARED = 2;
    public static final int END_COMMITTED = 4;
    public static final int RECOVERY_ROLLBACK_FIRST = 16;
    public static final int INTERNAL_TRANSACTION = 32;
    public static final int NESTED_TOP_TRANSACTION = 64;
    private static final int COMMIT_SYNC = 65536;
    private static final int COMMIT_NO_SYNC = 131072;
    private static final int COMMIT_PREPARE = 262144;
    private int savedEndStatus;
    private boolean needSync;
    private boolean justCreated;
    protected XactContext xc;
    protected final XactFactory xactFactory;
    protected final DataFactory dataFactory;
    protected final LogFactory logFactory;
    protected final DataValueFactory dataValueFactory;
    private final CompatibilitySpace compatibilitySpace;
    private LockingPolicy defaultLocking;
    private GlobalTransactionId myGlobalId;
    private volatile TransactionId myId;
    protected Logger logger;
    protected volatile int state;
    private Integer inComplete;
    private boolean seenUpdates;
    private boolean inPostCommitProcessing;
    private LogInstant logStart;
    private LogInstant logLast;
    private Stack savePoints;
    protected List postCommitWorks;
    protected List postTerminationWorks;
    private boolean recoveryTransaction;
    DynamicByteArrayOutputStream logBuffer;
    private boolean postCompleteMode;
    private boolean sanityCheck_xaclosed;
    private String transName;
    private boolean readOnly;
    private boolean flush_log_on_xact_end;
    private boolean backupBlocked;
    private boolean dontWaitForLocks;
    
    protected Xact(final XactFactory xactFactory, final LogFactory logFactory, final DataFactory dataFactory, final DataValueFactory dataValueFactory, final boolean readOnly, final CompatibilitySpace compatibilitySpace, final boolean flush_log_on_xact_end) {
        this.justCreated = true;
        this.inComplete = null;
        this.xactFactory = xactFactory;
        this.logFactory = logFactory;
        this.dataFactory = dataFactory;
        this.dataValueFactory = dataValueFactory;
        this.readOnly = readOnly;
        this.flush_log_on_xact_end = flush_log_on_xact_end;
        if (compatibilitySpace == null) {
            this.compatibilitySpace = this.getLockFactory().createCompatibilitySpace(this);
        }
        else {
            this.compatibilitySpace = compatibilitySpace;
        }
        this.resetDefaultLocking();
        xactFactory.setNewTransactionId(null, this);
        this.setIdleState();
        this.backupBlocked = false;
    }
    
    public final LockFactory getLockFactory() {
        return this.xactFactory.getLockFactory();
    }
    
    public final DataFactory getDataFactory() {
        return this.dataFactory;
    }
    
    public final LogFactory getLogFactory() {
        return this.logFactory;
    }
    
    public boolean anyoneBlocked() {
        return this.getLockFactory().anyoneBlocked();
    }
    
    public DynamicByteArrayOutputStream getLogBuffer() {
        if (this.logBuffer == null) {
            this.logBuffer = new DynamicByteArrayOutputStream(1024);
        }
        else {
            this.logBuffer.reset();
        }
        return this.logBuffer;
    }
    
    public void logAndUndo(final Compensation compensation, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        this.setActiveState();
        if (this.state == 2) {
            this.setUpdateState();
        }
        this.seenUpdates = true;
        final LogInstant logAndUndo = this.logger.logAndUndo(this, compensation, logInstant, limitObjectInput);
        this.setLastLogInstant(logAndUndo);
        if (this.savePoints != null && !this.savePoints.empty()) {
            final SavePoint savePoint = this.savePoints.peek();
            if (savePoint.getSavePoint() == null) {
                savePoint.setSavePoint(logAndUndo);
            }
        }
    }
    
    public void addUpdateTransaction(final int n) {
        if (this.myId != null) {
            this.xactFactory.addUpdateTransaction(this.myId, this, n);
        }
    }
    
    public void removeUpdateTransaction() {
        if (this.myId != null) {
            this.xactFactory.removeUpdateTransaction(this.myId);
        }
    }
    
    public void prepareTransaction() {
        if (this.myId != null) {
            this.xactFactory.prepareTransaction(this.myId);
        }
    }
    
    public void setFirstLogInstant(final LogInstant logStart) {
        this.logStart = logStart;
    }
    
    public LogInstant getFirstLogInstant() {
        return this.logStart;
    }
    
    public void setLastLogInstant(final LogInstant logLast) {
        this.logLast = logLast;
    }
    
    public LogInstant getLastLogInstant() {
        return this.logLast;
    }
    
    public void setTransactionId(final GlobalTransactionId myGlobalId, final TransactionId myId) {
        this.myGlobalId = myGlobalId;
        this.myId = myId;
    }
    
    public void setTransactionId(final Loggable loggable, final TransactionId myId) {
        this.myId = myId;
        this.myGlobalId = ((BeginXact)loggable).getGlobalId();
    }
    
    public void setup(final PersistentSet set) throws StandardException {
        this.getLockFactory().setLimit(this.compatibilitySpace, this, PropertyUtil.getServiceInt(set, "derby.locks.escalationThreshold", 100, Integer.MAX_VALUE, 5000), this);
    }
    
    public final GlobalTransactionId getGlobalId() {
        return this.myGlobalId;
    }
    
    public final ContextManager getContextManager() {
        return this.xc.getContextManager();
    }
    
    public final CompatibilitySpace getCompatibilitySpace() {
        return this.compatibilitySpace;
    }
    
    public boolean noWait() {
        return this.dontWaitForLocks;
    }
    
    public void setNoLockWait(final boolean dontWaitForLocks) {
        this.dontWaitForLocks = dontWaitForLocks;
    }
    
    public final TransactionId getId() {
        return this.myId;
    }
    
    protected final TransactionId getIdNoCheck() {
        return this.myId;
    }
    
    public final String getContextId() {
        final XactContext xc = this.xc;
        return (xc == null) ? null : xc.getIdName();
    }
    
    public LockingPolicy getDefaultLockingPolicy() {
        return this.defaultLocking;
    }
    
    public final LockingPolicy newLockingPolicy(final int n, final int n2, final boolean b) {
        return this.xactFactory.getLockingPolicy(n, n2, b);
    }
    
    public final void setDefaultLockingPolicy(LockingPolicy lockingPolicy) {
        if (lockingPolicy == null) {
            lockingPolicy = this.xactFactory.getLockingPolicy(0, 0, false);
        }
        this.defaultLocking = lockingPolicy;
    }
    
    public LogInstant commit() throws StandardException {
        return this.commit(65536);
    }
    
    public LogInstant commitNoSync(final int n) throws StandardException {
        if (this.state == 1 && this.savePoints == null && (n & 0x4) != 0x0) {
            return null;
        }
        return this.commit(0x20000 | n);
    }
    
    private LogInstant prepareCommit(final int n) throws StandardException {
        LogInstant logAndDo = null;
        if (this.state == 0) {
            throw StandardException.newException("40XT8", this.toInternalDetailString());
        }
        try {
            this.preComplete(Xact.COMMIT);
            if (this.seenUpdates) {
                logAndDo = this.logger.logAndDo(this, new EndXact(this.getGlobalId(), (((n & 0x40000) == 0x0) ? 4 : 2) | this.statusForEndXactLog()));
                if (this.flush_log_on_xact_end) {
                    if ((n & 0x10000) == 0x0) {
                        this.needSync = true;
                    }
                    else {
                        this.logger.flush(logAndDo);
                        this.needSync = false;
                    }
                }
            }
            else if (this.needSync && (n & 0x10000) != 0x0) {
                this.logger.flushAll();
                this.needSync = false;
            }
        }
        catch (StandardException ex) {
            if (ex.getSeverity() < 30000) {
                throw StandardException.newException("40XT1", ex);
            }
            throw ex;
        }
        return logAndDo;
    }
    
    private void completeCommit(final int n) throws StandardException {
        this.postComplete(n, Xact.COMMIT);
        if ((n & 0x2) == 0x0) {
            this.postTermination();
        }
        else {
            this.setActiveState();
        }
        this.myGlobalId = null;
    }
    
    private LogInstant commit(final int n) throws StandardException {
        final LogInstant prepareCommit = this.prepareCommit(n);
        this.completeCommit(n);
        return prepareCommit;
    }
    
    public void abort() throws StandardException {
        if (this.state == 0) {
            return;
        }
        try {
            this.preComplete(Xact.ABORT);
            if (this.getFirstLogInstant() != null) {
                if (this.logger == null) {
                    throw StandardException.newException("XSTB3.M");
                }
                this.logger.undo(this, this.getId(), this.getFirstLogInstant(), this.getLastLogInstant());
                this.logger.flush(this.logger.logAndDo(this, new EndXact(this.getGlobalId(), 0x1 | this.statusForEndXactLog())));
            }
            else if (this.needSync) {
                this.logger.flushAll();
            }
            this.needSync = false;
        }
        catch (StandardException ex) {
            if (ex.getSeverity() < 50000) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSTB0.M", ex));
            }
            throw ex;
        }
        this.postComplete(0, Xact.ABORT);
        if (this.postCommitWorks != null && !this.postCommitWorks.isEmpty()) {
            this.postCommitWorks.clear();
        }
        this.postTermination();
        this.myGlobalId = null;
    }
    
    public void reprepare() throws StandardException {
        if (this.state == 0) {
            throw StandardException.newException("40XT8", this.toInternalDetailString());
        }
        try {
            if (this.logger == null) {
                throw StandardException.newException("XSTB3.M");
            }
            this.state = 3;
            this.logger.reprepare(this, this.getId(), this.getFirstLogInstant(), this.getLastLogInstant());
            this.state = 4;
            this.seenUpdates = true;
        }
        catch (StandardException ex) {
            if (ex.getSeverity() < 50000) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSTB0.M", ex));
            }
            throw ex;
        }
    }
    
    public void destroy() throws StandardException {
        if (this.state != 0) {
            this.abort();
        }
        this.close();
    }
    
    public void close() throws StandardException {
        switch (this.state) {
            case 0: {}
            case 1: {
                this.getLockFactory().clearLimit(this.compatibilitySpace, this);
                if (this.myId != null) {
                    this.xactFactory.remove(this.myId);
                }
                this.xc.popMe();
                this.xc = null;
                this.myGlobalId = null;
                this.myId = null;
                this.logStart = null;
                this.logLast = null;
                this.state = 0;
            }
            default: {
                throw StandardException.newException("40XT4");
            }
        }
    }
    
    public void logAndDo(final Loggable loggable) throws StandardException {
        LogInstant logAndDo = null;
        if (this.logger == null) {
            this.getLogger();
        }
        if (this.logger == null) {
            throw StandardException.newException("XSTB2.M");
        }
        this.setActiveState();
        if (this.state == 2) {
            logAndDo = this.logger.logAndDo(this, new BeginXact(this.getGlobalId(), this.statusForBeginXactLog()));
            this.setUpdateState();
        }
        this.seenUpdates = true;
        if (loggable != null) {
            final LogInstant logAndDo2 = this.logger.logAndDo(this, loggable);
            if (logAndDo2 != null) {
                this.setLastLogInstant(logAndDo2);
                if (this.savePoints != null && !this.savePoints.empty()) {
                    for (int i = this.savePoints.size() - 1; i >= 0; --i) {
                        final SavePoint savePoint = (SavePoint)this.savePoints.elementAt(i);
                        if (savePoint.getSavePoint() != null) {
                            break;
                        }
                        savePoint.setSavePoint(logAndDo2);
                    }
                }
            }
        }
        else if (logAndDo != null) {
            this.setLastLogInstant(logAndDo);
        }
    }
    
    public void addPostCommitWork(final Serviceable serviceable) {
        if (this.recoveryTransaction) {
            return;
        }
        if (this.postCommitWorks == null) {
            this.postCommitWorks = new ArrayList(1);
        }
        this.postCommitWorks.add(serviceable);
    }
    
    public void addPostTerminationWork(final Serviceable serviceable) {
        if (this.recoveryTransaction) {
            return;
        }
        if (this.postTerminationWorks == null) {
            this.postTerminationWorks = new ArrayList(2);
        }
        this.postTerminationWorks.add(serviceable);
    }
    
    public ContainerHandle openContainer(final ContainerKey containerKey, final int n) throws StandardException {
        return this.openContainer(containerKey, this.defaultLockingPolicy(), n);
    }
    
    public ContainerHandle openContainer(final ContainerKey containerKey, LockingPolicy lockingPolicy, final int n) throws StandardException {
        this.setActiveState();
        if (lockingPolicy == null) {
            lockingPolicy = this.xactFactory.getLockingPolicy(0, 0, false);
        }
        return this.dataFactory.openContainer(this, containerKey, lockingPolicy, n);
    }
    
    public RawContainerHandle openDroppedContainer(final ContainerKey containerKey, LockingPolicy lockingPolicy) throws StandardException {
        this.setActiveState();
        if (lockingPolicy == null) {
            lockingPolicy = this.xactFactory.getLockingPolicy(0, 0, false);
        }
        RawContainerHandle rawContainerHandle;
        try {
            rawContainerHandle = this.dataFactory.openDroppedContainer(this, containerKey, lockingPolicy, 4);
        }
        catch (StandardException ex) {
            rawContainerHandle = this.dataFactory.openDroppedContainer(this, containerKey, lockingPolicy, 8);
        }
        return rawContainerHandle;
    }
    
    public long addContainer(final long n, final long n2, final int n3, final Properties properties, final int n4) throws StandardException {
        this.setActiveState();
        return this.dataFactory.addContainer(this, n, n2, n3, properties, n4);
    }
    
    public long addAndLoadStreamContainer(final long n, final Properties properties, final RowSource rowSource) throws StandardException {
        this.setActiveState();
        return this.dataFactory.addAndLoadStreamContainer(this, n, properties, rowSource);
    }
    
    public StreamContainerHandle openStreamContainer(final long n, final long n2, final boolean b) throws StandardException {
        this.setActiveState();
        return this.dataFactory.openStreamContainer(this, n, n2, b);
    }
    
    public void dropStreamContainer(final long n, final long n2) throws StandardException {
        this.setActiveState();
        this.dataFactory.dropStreamContainer(this, n, n2);
    }
    
    public void reCreateContainerForRedoRecovery(final long n, final long n2, final ByteArray byteArray) throws StandardException {
        this.setActiveState();
        this.dataFactory.reCreateContainerForRedoRecovery(this, n, n2, byteArray);
    }
    
    public void dropContainer(final ContainerKey containerKey) throws StandardException {
        this.setActiveState();
        this.dataFactory.dropContainer(this, containerKey);
    }
    
    public int setSavePoint(final String s, final Object o) throws StandardException {
        if (o != null && o instanceof String) {
            this.throwExceptionIfSQLSavepointNotAllowed(o);
        }
        if (this.getSavePointPosition(s, o, false) != -1) {
            throw StandardException.newException("3B501.S");
        }
        if (this.savePoints == null) {
            this.savePoints = new Stack();
        }
        this.savePoints.push(new SavePoint(s, o));
        return this.savePoints.size();
    }
    
    private void throwExceptionIfSQLSavepointNotAllowed(final Object o) throws StandardException {
        boolean b = false;
        if (this.savePoints != null && !this.savePoints.empty()) {
            for (int i = this.savePoints.size() - 1; i >= 0; --i) {
                if (((SavePoint)this.savePoints.elementAt(i)).isThisUserDefinedsavepoint()) {
                    b = true;
                    break;
                }
            }
        }
        if (b) {
            throw StandardException.newException("3B002.S");
        }
    }
    
    public int releaseSavePoint(String substring, final Object o) throws StandardException {
        final int savePointPosition = this.getSavePointPosition(substring, o, true);
        if (savePointPosition == -1) {
            if (o != null && !(o instanceof String)) {
                substring = substring.substring(2);
            }
            throw StandardException.newException("3B001.S", substring);
        }
        this.popSavePoints(savePointPosition, true);
        return this.savePoints.size();
    }
    
    public int rollbackToSavePoint(String substring, final Object o) throws StandardException {
        final int savePointPosition = this.getSavePointPosition(substring, o, true);
        if (savePointPosition == -1) {
            if (o != null && !(o instanceof String)) {
                substring = substring.substring(2);
            }
            throw StandardException.newException("3B001.S", substring);
        }
        this.notifyObservers(Xact.SAVEPOINT_ROLLBACK);
        this.popSavePoints(savePointPosition, false);
        return this.savePoints.size();
    }
    
    private void getLogger() {
        this.logger = this.logFactory.getLogger();
    }
    
    protected void assumeIdentity(final TransactionTableEntry transactionTableEntry) {
        if (transactionTableEntry != null) {
            transactionTableEntry.setXact(this);
            this.myId = transactionTableEntry.getXid();
            this.logStart = transactionTableEntry.getFirstLog();
            this.logLast = transactionTableEntry.getLastLog();
            this.myGlobalId = null;
            if (this.state == 1) {
                this.state = 2;
            }
            if (this.logger == null) {
                this.getLogger();
            }
            this.savedEndStatus = 0;
        }
        else {
            this.myGlobalId = null;
            this.myId = null;
            this.logStart = null;
            this.logLast = null;
            this.state = 1;
        }
    }
    
    protected void assumeGlobalXactIdentity(final TransactionTableEntry transactionTableEntry) {
        this.myId = transactionTableEntry.getXid();
        this.myGlobalId = transactionTableEntry.getGid();
        this.logStart = transactionTableEntry.getFirstLog();
        this.logLast = transactionTableEntry.getLastLog();
        if (this.state == 1) {
            this.state = 2;
        }
        if (transactionTableEntry.isPrepared()) {
            this.state = 4;
        }
        transactionTableEntry.setXact(this);
        if (this.logger == null) {
            this.getLogger();
        }
        this.savedEndStatus = 0;
    }
    
    private final void setUpdateState() throws StandardException {
        if (this.readOnly) {
            throw StandardException.newException("40XT8", this.toInternalDetailString());
        }
        this.state = 3;
    }
    
    protected void setIdleState() {
        this.state = 1;
        this.seenUpdates = false;
        this.logStart = null;
        this.logLast = null;
    }
    
    protected final void setActiveState() throws StandardException {
        if (this.state == 0 || (!this.inAbort() && this.state == 4)) {
            throw StandardException.newException("40XT8", this.toInternalDetailString());
        }
        if (this.state == 1) {
            synchronized (this) {
                this.state = 2;
            }
            if (!this.justCreated) {
                this.xactFactory.setNewTransactionId(this.myId, this);
            }
            this.justCreated = false;
        }
    }
    
    protected final void setPrepareState() throws StandardException {
        if (this.state == 4 || this.state == 0) {
            throw StandardException.newException("40XT8", this.toInternalDetailString());
        }
        this.state = 4;
    }
    
    public final LockingPolicy defaultLockingPolicy() {
        return this.defaultLocking;
    }
    
    private final void releaseAllLocks() {
        this.getLockFactory().unlockGroup(this.getCompatibilitySpace(), this);
    }
    
    void resetDefaultLocking() {
        this.setDefaultLockingPolicy(this.newLockingPolicy(1, 5, true));
    }
    
    protected void preComplete(final Integer inComplete) throws StandardException {
        if (this.inComplete == null) {
            this.inComplete = inComplete;
            if (!this.postCompleteMode) {
                this.doComplete(inComplete);
            }
            return;
        }
        if (inComplete.equals(Xact.COMMIT)) {
            throw this.logFactory.markCorrupt(StandardException.newException("40XT1"));
        }
        throw this.logFactory.markCorrupt(StandardException.newException("XSTB0.M"));
    }
    
    protected void postComplete(final int n, final Integer n2) throws StandardException {
        if (this.postCompleteMode) {
            this.doComplete(n2);
        }
        if ((n & 0x2) == 0x0) {
            this.releaseAllLocks();
        }
        this.setIdleState();
        this.inComplete = null;
    }
    
    protected void doComplete(final Integer n) throws StandardException {
        if (this.savePoints != null) {
            this.savePoints.removeAllElements();
        }
        do {
            this.notifyObservers(n);
            this.checkObserverException();
        } while (this.countObservers() > 0);
    }
    
    private void checkObserverException() throws StandardException {
        if (this.observerException != null) {
            final StandardException observerException = this.observerException;
            this.observerException = null;
            throw observerException;
        }
    }
    
    protected boolean doPostCommitWorkInTran() {
        return !this.inPostCommitProcessing && !this.recoveryTransaction && this.isUserTransaction() && this.myGlobalId == null;
    }
    
    public boolean handlesPostTerminationWork() {
        return !this.recoveryTransaction;
    }
    
    public void recoveryTransaction() {
        this.recoveryTransaction = true;
        this.xactFactory.remove(this.myId);
    }
    
    private final void postTermination() throws StandardException {
        final int n = (this.postTerminationWorks == null) ? 0 : this.postTerminationWorks.size();
        for (int i = 0; i < n; ++i) {
            this.addPostCommitWork((Serviceable)this.postTerminationWorks.get(i));
        }
        if (n > 0) {
            this.postTerminationWorks.clear();
        }
        if (this.postCommitWorks != null && !this.postCommitWorks.isEmpty()) {
            final int size = this.postCommitWorks.size();
            if (this.doPostCommitWorkInTran()) {
                try {
                    this.inPostCommitProcessing = true;
                    final Serviceable[] array = this.postCommitWorks.toArray(new Serviceable[size]);
                    this.postCommitWorks.clear();
                    int inDatabaseCreation = this.xactFactory.inDatabaseCreation() ? 1 : 0;
                    for (int j = 0; j < size; ++j) {
                        Label_0215: {
                            if (inDatabaseCreation == 0) {
                                if (!array[j].serviceImmediately()) {
                                    break Label_0215;
                                }
                            }
                            try {
                                if (array[j].performWork(this.xc.getContextManager()) == 1) {
                                    array[j] = null;
                                }
                            }
                            catch (StandardException ex) {
                                array[j] = null;
                                this.xc.cleanupOnError(ex);
                            }
                        }
                        if (array[j] != null) {
                            final boolean submitPostCommitWork = this.xactFactory.submitPostCommitWork(array[j]);
                            array[j] = null;
                            if (submitPostCommitWork) {
                                inDatabaseCreation = 1;
                            }
                        }
                    }
                }
                finally {
                    this.inPostCommitProcessing = false;
                    if (this.postCommitWorks != null) {
                        this.postCommitWorks.clear();
                    }
                }
            }
            else {
                for (int k = 0; k < size; ++k) {
                    this.xactFactory.submitPostCommitWork((Serviceable)this.postCommitWorks.get(k));
                }
            }
            this.postCommitWorks.clear();
        }
        this.unblockBackup();
    }
    
    private int getSavePointPosition(final String anObject, final Object obj, final boolean b) {
        if (this.savePoints == null || this.savePoints.empty()) {
            return -1;
        }
        for (int i = this.savePoints.size() - 1; i >= 0; --i) {
            final SavePoint savePoint = (SavePoint)this.savePoints.elementAt(i);
            if (savePoint.getName().equals(anObject)) {
                if (!b || savePoint.getKindOfSavepoint() == null) {
                    return i;
                }
                if (savePoint.getKindOfSavepoint().equals(obj)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    protected boolean popSavePoints(final int size, final boolean b) throws StandardException {
        if (b) {
            this.savePoints.setSize(size);
            return false;
        }
        LogInstant logInstant = null;
        for (int size2 = this.savePoints.size(), i = size; i < size2; ++i) {
            final LogInstant savePoint = ((SavePoint)this.savePoints.elementAt(i)).getSavePoint();
            if (savePoint != null) {
                logInstant = savePoint;
                break;
            }
        }
        this.savePoints.setSize(size + 1);
        if (logInstant == null) {
            return false;
        }
        try {
            this.logger.undo(this, this.getId(), logInstant, this.getLastLogInstant());
        }
        catch (StandardException ex) {
            if (ex.getSeverity() < 30000) {
                throw StandardException.newException("40XT2", ex);
            }
            throw ex;
        }
        return true;
    }
    
    public RawTransaction startNestedTopTransaction() throws StandardException {
        return this.xactFactory.startNestedTopTransaction(this.xc.getFactory(), this.xc.getContextManager());
    }
    
    private boolean isUserTransaction() {
        final String contextId = this.getContextId();
        return contextId == "UserTransaction" || contextId.equals("UserTransaction");
    }
    
    public final boolean isActive() {
        final int state = this.state;
        return state != 0 && state != 1;
    }
    
    public final boolean isPrepared() {
        return this.state == 4;
    }
    
    public boolean isIdle() {
        return this.state == 1;
    }
    
    public boolean isPristine() {
        return this.state == 1 || this.state == 2;
    }
    
    public boolean inAbort() {
        return Xact.ABORT.equals(this.inComplete);
    }
    
    public FileResource getFileHandler() {
        return this.dataFactory.getFileHandler();
    }
    
    protected int statusForBeginXactLog() {
        return this.recoveryRollbackFirst() ? 16 : 0;
    }
    
    protected int statusForEndXactLog() {
        return this.savedEndStatus;
    }
    
    void setPostComplete() {
        this.postCompleteMode = true;
    }
    
    public boolean blockBackup(final boolean b) throws StandardException {
        if (!this.backupBlocked) {
            this.backupBlocked = this.xactFactory.blockBackup(b);
        }
        return this.backupBlocked;
    }
    
    private void unblockBackup() {
        if (this.backupBlocked) {
            this.xactFactory.unblockBackup();
        }
        this.backupBlocked = false;
    }
    
    public boolean isBlockingBackup() {
        return this.backupBlocked;
    }
    
    public void reached(final CompatibilitySpace compatibilitySpace, final Object o, final int n, final Enumeration enumeration, final int n2) throws StandardException {
        final Hashtable<ContainerKey, LockCount> hashtable = new Hashtable<ContainerKey, LockCount>();
        while (enumeration.hasMoreElements()) {
            final RecordHandle nextElement = enumeration.nextElement();
            if (!(nextElement instanceof RecordHandle)) {
                continue;
            }
            final ContainerKey containerId = nextElement.getContainerId();
            LockCount lockCount = hashtable.get(containerId);
            if (lockCount == null) {
                lockCount = new LockCount();
                hashtable.put(containerId, lockCount);
            }
            final LockCount lockCount2 = lockCount;
            ++lockCount2.count;
        }
        int n3 = n / (hashtable.size() + 1);
        if (n3 < n / 4) {
            n3 = n / 4;
        }
        boolean b = false;
        final Enumeration<ContainerKey> keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final ContainerKey containerKey = keys.nextElement();
            if (hashtable.get(containerKey).count < n3) {
                continue;
            }
            try {
                if (this.openContainer(containerKey, new RowLocking3Escalate(this.getLockFactory()), 196) == null) {
                    continue;
                }
                b = true;
            }
            catch (StandardException ex) {
                if (!ex.isLockTimeout()) {
                    throw ex;
                }
                continue;
            }
        }
        if (b) {
            this.notifyObservers(Xact.LOCK_ESCALATE);
            this.checkObserverException();
        }
    }
    
    public void createXATransactionFromLocalTransaction(final int n, final byte[] array, final byte[] array2) throws StandardException {
        final GlobalXactId globalXactId = new GlobalXactId(n, array, array2);
        if (((TransactionTable)this.xactFactory.getTransactionTable()).findTransactionContextByGlobalId(globalXactId) != null) {
            throw StandardException.newException("XSAX1.S");
        }
        this.setTransactionId(globalXactId, this.getId());
    }
    
    public void xa_commit(final boolean b) throws StandardException {
        if (b) {
            if (this.state == 4) {
                throw StandardException.newException("40XT8", this.toInternalDetailString());
            }
            this.prepareCommit(65536);
            this.completeCommit(65536);
        }
        else {
            if (this.state != 4) {
                throw StandardException.newException("40XT8", this.toInternalDetailString());
            }
            this.prepareCommit(65536);
            this.completeCommit(65536);
        }
    }
    
    public int xa_prepare() throws StandardException {
        if (this.state == 1 || this.state == 2) {
            this.abort();
            return 1;
        }
        this.prepareCommit(327682);
        this.inComplete = null;
        this.setPrepareState();
        return 2;
    }
    
    public void xa_rollback() throws StandardException {
        this.abort();
    }
    
    public String toString() {
        try {
            return this.myId.toString();
        }
        catch (Throwable t) {
            return "null";
        }
    }
    
    public String toInternalDetailString() {
        return "savedEndStatus = " + this.savedEndStatus + "\n" + "needSync = " + this.needSync + "\n" + "justCreated = " + this.justCreated + "\n" + "myGlobalId = " + this.myGlobalId + "\n" + "myId = " + this.myId + "\n" + "state = " + this.state + "\n" + "inComplete = " + this.inComplete + "\n" + "seenUpdates = " + this.seenUpdates + "\n" + "inPostCommitProcessing = " + this.inPostCommitProcessing + "\n" + "logStart = " + this.logStart + "\n" + "logLast = " + this.logLast + "\n" + "recoveryTransaction = " + this.recoveryTransaction + "\n" + "postCompleteMode = " + this.postCompleteMode + "\n" + "sanityCheck_xaclosed = " + this.sanityCheck_xaclosed + "\n" + "transName = " + this.transName + "\n" + "readOnly = " + this.readOnly + "\n" + "flush_log_on_xact_end = " + this.flush_log_on_xact_end + "\n" + "backupBlocked = " + this.backupBlocked + "\n" + "dontWaitForLocks = " + this.dontWaitForLocks + "\n";
    }
    
    public String getActiveStateTxIdString() {
        if (!this.justCreated && this.state == 1) {
            this.xactFactory.setNewTransactionId(this.myId, this);
            this.justCreated = true;
        }
        return this.toString();
    }
    
    public DataValueFactory getDataValueFactory() throws StandardException {
        return this.dataValueFactory;
    }
    
    String getState() {
        switch (this.state) {
            case 0: {
                return "CLOSED";
            }
            case 1: {
                return "IDLE";
            }
            case 2:
            case 3: {
                return "ACTIVE";
            }
            case 4: {
                return "PREPARED";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getTransName() {
        return this.transName;
    }
    
    public void setTransName(final String transName) {
        this.transName = transName;
    }
    
    public boolean inRollForwardRecovery() {
        return this.logFactory.inRFR();
    }
    
    public void checkpointInRollForwardRecovery(final LogInstant logInstant, final long n, final long n2) throws StandardException {
        this.logFactory.checkpointInRFR(logInstant, n, n2, this.dataFactory);
    }
}
