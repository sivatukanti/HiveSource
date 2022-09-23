// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import javax.transaction.RollbackException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import javax.transaction.SystemException;
import org.datanucleus.transaction.NucleusTransactionException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Synchronization;

public class JTAJCATransactionImpl extends TransactionImpl implements Synchronization
{
    private TransactionManager jtaTM;
    private javax.transaction.Transaction jtaTx;
    private boolean markedForRollback;
    
    JTAJCATransactionImpl(final ExecutionContext ec) {
        super(ec);
        this.markedForRollback = false;
        this.joinTransaction();
    }
    
    @Override
    public boolean getIsActive() {
        return this.isActive();
    }
    
    @Override
    public boolean isActive() {
        final boolean isActive = super.isActive();
        if (isActive) {
            return true;
        }
        this.joinTransaction();
        return this.active;
    }
    
    private synchronized void joinTransaction() {
        if (this.active) {
            return;
        }
        try {
            if (this.jtaTM == null) {
                this.jtaTM = this.ec.getNucleusContext().getJtaTransactionManager();
                if (this.jtaTM == null) {
                    throw new NucleusTransactionException(JTAJCATransactionImpl.LOCALISER.msg("015030"));
                }
            }
            this.jtaTx = this.jtaTM.getTransaction();
            if (this.jtaTx != null && this.jtaTx.getStatus() == 0) {
                if (!this.ec.getNucleusContext().isJcaMode()) {
                    this.jtaTx.registerSynchronization(this);
                }
                this.begin();
            }
            else if (this.markedForRollback) {
                this.rollback();
                this.markedForRollback = false;
            }
        }
        catch (SystemException se) {
            throw new NucleusTransactionException(JTAJCATransactionImpl.LOCALISER.msg("015026"), se);
        }
        catch (RollbackException e) {
            NucleusLogger.TRANSACTION.error("Exception while joining transaction: " + StringUtils.getStringFromStackTrace(e));
        }
    }
    
    @Override
    public void beforeCompletion() {
        try {
            this.internalPreCommit();
        }
        catch (Throwable th) {
            NucleusLogger.TRANSACTION.error("Exception flushing work in JTA transaction. Mark for rollback", th);
            try {
                this.jtaTx.setRollbackOnly();
            }
            catch (Exception e) {
                NucleusLogger.TRANSACTION.fatal("Cannot mark transaction for rollback after exception in beforeCompletion. PersistenceManager might be in inconsistent state", e);
            }
        }
    }
    
    @Override
    public synchronized void afterCompletion(final int status) {
        try {
            if (status == 4) {
                this.rollback();
            }
            else if (status == 3) {
                this.internalPostCommit();
            }
            else {
                NucleusLogger.TRANSACTION.fatal("Received unexpected transaction status + " + status);
            }
        }
        catch (Throwable th) {
            NucleusLogger.TRANSACTION.error("Exception during afterCompletion in JTA transaction. PersistenceManager might be in inconsistent state");
        }
        finally {
            this.jtaTx = null;
        }
    }
}
