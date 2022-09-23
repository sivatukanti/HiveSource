// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.xact;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Transaction;
import java.util.Observable;

public abstract class RawTransaction extends Observable implements Transaction
{
    public static final Integer COMMIT;
    public static final Integer ABORT;
    public static final Integer SAVEPOINT_ROLLBACK;
    public static final Integer LOCK_ESCALATE;
    protected StandardException observerException;
    
    public abstract LockFactory getLockFactory();
    
    public abstract DataFactory getDataFactory();
    
    public abstract LogFactory getLogFactory();
    
    public abstract DynamicByteArrayOutputStream getLogBuffer();
    
    public abstract void logAndUndo(final Compensation p0, final LogInstant p1, final LimitObjectInput p2) throws StandardException;
    
    public abstract void setTransactionId(final GlobalTransactionId p0, final TransactionId p1);
    
    public abstract void setTransactionId(final Loggable p0, final TransactionId p1);
    
    public abstract TransactionId getId();
    
    public abstract GlobalTransactionId getGlobalId();
    
    public abstract void addUpdateTransaction(final int p0);
    
    public abstract void removeUpdateTransaction();
    
    public abstract void prepareTransaction();
    
    public abstract void setFirstLogInstant(final LogInstant p0);
    
    public abstract LogInstant getFirstLogInstant();
    
    public abstract void setLastLogInstant(final LogInstant p0);
    
    public abstract LogInstant getLastLogInstant();
    
    public void checkLogicalOperationOk() throws StandardException {
    }
    
    public boolean recoveryRollbackFirst() {
        return false;
    }
    
    public abstract void reprepare() throws StandardException;
    
    public void setObserverException(final StandardException observerException) {
        if (this.observerException == null) {
            this.observerException = observerException;
        }
    }
    
    public abstract RawTransaction startNestedTopTransaction() throws StandardException;
    
    public abstract RawContainerHandle openDroppedContainer(final ContainerKey p0, final LockingPolicy p1) throws StandardException;
    
    public abstract void reCreateContainerForRedoRecovery(final long p0, final long p1, final ByteArray p2) throws StandardException;
    
    protected abstract int statusForBeginXactLog();
    
    protected abstract int statusForEndXactLog();
    
    public abstract boolean inAbort();
    
    public abstract boolean handlesPostTerminationWork();
    
    public abstract void recoveryTransaction();
    
    public void notifyObservers(final Object arg) {
        if (this.countObservers() != 0) {
            this.setChanged();
            super.notifyObservers(arg);
        }
    }
    
    public abstract boolean inRollForwardRecovery();
    
    public abstract void checkpointInRollForwardRecovery(final LogInstant p0, final long p1, final long p2) throws StandardException;
    
    public abstract boolean blockBackup(final boolean p0) throws StandardException;
    
    public abstract boolean isBlockingBackup();
    
    static {
        COMMIT = new Integer(0);
        ABORT = new Integer(1);
        SAVEPOINT_ROLLBACK = new Integer(2);
        LOCK_ESCALATE = new Integer(3);
    }
}
