// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.HashMap;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.transaction.HeuristicMixedException;
import org.datanucleus.transaction.HeuristicRollbackException;
import org.datanucleus.transaction.RollbackException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.exceptions.TransactionNotActiveException;
import org.datanucleus.transaction.NucleusTransactionException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.TransactionActiveOnBeginException;
import org.datanucleus.transaction.TransactionUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import javax.transaction.Synchronization;
import org.datanucleus.transaction.TransactionManager;
import org.datanucleus.util.Localiser;

public class TransactionImpl implements Transaction
{
    protected static final Localiser LOCALISER;
    ExecutionContext ec;
    TransactionManager txnMgr;
    boolean active;
    boolean committing;
    Synchronization sync;
    protected boolean rollbackOnly;
    protected Boolean serializeRead;
    private Set<TransactionEventListener> listenersPerTransaction;
    private TransactionEventListener ecListener;
    private List<TransactionEventListener> userListeners;
    private Map<String, Object> options;
    long beginTime;
    boolean closed;
    
    public TransactionImpl(final ExecutionContext ec) {
        this.active = false;
        this.rollbackOnly = false;
        this.serializeRead = null;
        this.listenersPerTransaction = new HashSet<TransactionEventListener>();
        this.userListeners = new ArrayList<TransactionEventListener>();
        this.options = null;
        this.closed = false;
        this.ec = ec;
        this.ecListener = (TransactionEventListener)ec;
        this.txnMgr = ec.getNucleusContext().getTransactionManager();
        final PersistenceConfiguration config = ec.getNucleusContext().getPersistenceConfiguration();
        final int isolationLevel = TransactionUtils.getTransactionIsolationLevelForName(config.getStringProperty("datanucleus.transactionIsolation"));
        this.setOption("transaction.isolation", isolationLevel);
        Boolean serialiseReadProp = config.getBooleanObjectProperty("datanucleus.SerializeRead");
        if (ec.getProperty("datanucleus.SerializeRead") != null) {
            serialiseReadProp = ec.getBooleanProperty("datanucleus.SerializeRead");
        }
        if (serialiseReadProp != null) {
            this.serializeRead = serialiseReadProp;
        }
    }
    
    @Override
    public void close() {
        this.closed = true;
    }
    
    @Override
    public void begin() {
        if (this.ec.getMultithreaded()) {
            synchronized (this) {
                this.txnMgr.begin(this.ec);
            }
        }
        else {
            this.txnMgr.begin(this.ec);
        }
        this.internalBegin();
    }
    
    protected void internalBegin() {
        if (this.active) {
            throw new TransactionActiveOnBeginException(this.ec);
        }
        this.active = true;
        this.beginTime = System.currentTimeMillis();
        if (this.ec.getStatistics() != null) {
            this.ec.getStatistics().transactionStarted();
        }
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015000", this.ec, "" + this.ec.getBooleanProperty("datanucleus.Optimistic")));
        }
        final TransactionEventListener[] arr$;
        final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
        for (final TransactionEventListener tel : arr$) {
            tel.transactionStarted();
        }
    }
    
    @Override
    public void preFlush() {
        try {
            final TransactionEventListener[] arr$;
            final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
            for (final TransactionEventListener tel : arr$) {
                tel.transactionPreFlush();
            }
        }
        catch (Throwable ex) {
            if (ex instanceof NucleusException) {
                throw (NucleusException)ex;
            }
            throw new NucleusTransactionException(TransactionImpl.LOCALISER.msg("015005"), ex);
        }
    }
    
    @Override
    public void flush() {
        try {
            final TransactionEventListener[] arr$;
            final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
            for (final TransactionEventListener tel : arr$) {
                tel.transactionFlushed();
            }
        }
        catch (Throwable ex) {
            if (ex instanceof NucleusException) {
                throw (NucleusException)ex;
            }
            throw new NucleusTransactionException(TransactionImpl.LOCALISER.msg("015005"), ex);
        }
    }
    
    @Override
    public void end() {
        try {
            this.flush();
        }
        finally {
            final TransactionEventListener[] arr$;
            final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
            for (final TransactionEventListener tel : arr$) {
                tel.transactionEnded();
            }
        }
    }
    
    @Override
    public void commit() {
        if (!this.isActive()) {
            throw new TransactionNotActiveException();
        }
        if (this.rollbackOnly) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015020"));
            }
            throw new NucleusDataStoreException(TransactionImpl.LOCALISER.msg("015020")).setFatal();
        }
        final long startTime = System.currentTimeMillis();
        boolean success = false;
        boolean canComplete = true;
        final List errors = new ArrayList();
        try {
            this.flush();
            this.internalPreCommit();
            this.internalCommit();
            success = true;
        }
        catch (RollbackException e) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e));
            }
            errors.add(e);
        }
        catch (HeuristicRollbackException e2) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e2));
            }
            errors.add(e2);
        }
        catch (HeuristicMixedException e3) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e3));
            }
            errors.add(e3);
        }
        catch (NucleusUserException e4) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e4));
            }
            canComplete = false;
            throw e4;
        }
        catch (NucleusException e5) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e5));
            }
            errors.add(e5);
        }
        finally {
            if (canComplete) {
                try {
                    if (!success) {
                        this.rollback();
                    }
                    else {
                        this.internalPostCommit();
                    }
                }
                catch (Throwable e6) {
                    errors.add(e6);
                }
            }
        }
        if (errors.size() > 0) {
            throw new NucleusTransactionException(TransactionImpl.LOCALISER.msg("015007"), errors.toArray(new Throwable[errors.size()]));
        }
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015022", System.currentTimeMillis() - startTime));
        }
    }
    
    protected void internalPreCommit() {
        this.committing = true;
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015001", this.ec));
        }
        if (this.sync != null) {
            this.sync.beforeCompletion();
        }
        final TransactionEventListener[] arr$;
        final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
        for (final TransactionEventListener tel : arr$) {
            tel.transactionPreCommit();
        }
    }
    
    protected void internalCommit() {
        if (this.ec.getMultithreaded()) {
            synchronized (this) {
                this.txnMgr.commit(this.ec);
            }
        }
        else {
            this.txnMgr.commit(this.ec);
        }
    }
    
    @Override
    public void rollback() {
        if (!this.isActive()) {
            throw new TransactionNotActiveException();
        }
        final long startTime = System.currentTimeMillis();
        try {
            boolean canComplete = true;
            this.committing = true;
            try {
                this.flush();
            }
            finally {
                try {
                    this.internalPreRollback();
                }
                catch (NucleusUserException e) {
                    if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                        NucleusLogger.TRANSACTION.debug(StringUtils.getStringFromStackTrace(e));
                    }
                    canComplete = false;
                    throw e;
                }
                finally {
                    if (canComplete) {
                        try {
                            this.internalRollback();
                        }
                        finally {
                            try {
                                this.active = false;
                                if (this.ec.getStatistics() != null) {
                                    this.ec.getStatistics().transactionRolledBack(System.currentTimeMillis() - this.beginTime);
                                }
                            }
                            finally {
                                this.listenersPerTransaction.clear();
                                this.rollbackOnly = false;
                                if (this.sync != null) {
                                    this.sync.afterCompletion(4);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (NucleusUserException e2) {
            throw e2;
        }
        catch (NucleusException e3) {
            throw new NucleusDataStoreException(TransactionImpl.LOCALISER.msg("015009"), e3);
        }
        finally {
            this.committing = false;
        }
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015023", System.currentTimeMillis() - startTime));
        }
    }
    
    protected void internalPreRollback() {
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(TransactionImpl.LOCALISER.msg("015002", this.ec));
        }
        final TransactionEventListener[] arr$;
        final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
        for (final TransactionEventListener tel : arr$) {
            tel.transactionPreRollBack();
        }
    }
    
    protected void internalRollback() {
        final org.datanucleus.transaction.Transaction tx = this.txnMgr.getTransaction(this.ec);
        if (tx != null) {
            if (this.ec.getMultithreaded()) {
                synchronized (this) {
                    this.txnMgr.rollback(this.ec);
                }
            }
            else {
                this.txnMgr.rollback(this.ec);
            }
        }
        final TransactionEventListener[] arr$;
        final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
        for (final TransactionEventListener tel : arr$) {
            tel.transactionRolledBack();
        }
    }
    
    protected void internalPostCommit() {
        try {
            this.active = false;
            if (this.ec.getStatistics() != null) {
                this.ec.getStatistics().transactionCommitted(System.currentTimeMillis() - this.beginTime);
            }
        }
        finally {
            try {
                final TransactionEventListener[] arr$;
                final TransactionEventListener[] ls = arr$ = this.getListenersForEvent();
                for (final TransactionEventListener tel : arr$) {
                    tel.transactionCommitted();
                }
            }
            finally {
                this.committing = false;
                this.listenersPerTransaction.clear();
                if (this.sync != null) {
                    this.sync.afterCompletion(3);
                }
            }
        }
    }
    
    private TransactionEventListener[] getListenersForEvent() {
        final TransactionEventListener[] ls = new TransactionEventListener[this.userListeners.size() + this.listenersPerTransaction.size() + 1];
        System.arraycopy(this.listenersPerTransaction.toArray(), 0, ls, 0, this.listenersPerTransaction.size());
        System.arraycopy(this.userListeners.toArray(), 0, ls, this.listenersPerTransaction.size(), this.userListeners.size());
        ls[ls.length - 1] = this.ecListener;
        return ls;
    }
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public boolean getIsActive() {
        return this.active;
    }
    
    @Override
    public boolean isCommitting() {
        return this.committing;
    }
    
    @Override
    public boolean getNontransactionalRead() {
        return this.ec.getBooleanProperty("datanucleus.NontransactionalRead");
    }
    
    @Override
    public boolean getNontransactionalWrite() {
        return this.ec.getBooleanProperty("datanucleus.NontransactionalWrite");
    }
    
    @Override
    public boolean getOptimistic() {
        return this.ec.getBooleanProperty("datanucleus.Optimistic");
    }
    
    @Override
    public boolean getRestoreValues() {
        return this.ec.getBooleanProperty("datanucleus.RestoreValues");
    }
    
    @Override
    public boolean getRetainValues() {
        return this.ec.getBooleanProperty("datanucleus.RetainValues");
    }
    
    @Override
    public boolean getRollbackOnly() {
        return this.rollbackOnly;
    }
    
    @Override
    public Synchronization getSynchronization() {
        return this.sync;
    }
    
    @Override
    public void setNontransactionalRead(final boolean nontransactionalRead) {
        this.ec.setProperty("datanucleus.NontransactionalRead", nontransactionalRead);
    }
    
    @Override
    public void setNontransactionalWrite(final boolean nontransactionalWrite) {
        this.ec.setProperty("datanucleus.NontransactionalWrite", nontransactionalWrite);
    }
    
    @Override
    public void setOptimistic(final boolean optimistic) {
        this.ec.setProperty("datanucleus.Optimistic", optimistic);
    }
    
    @Override
    public void setRestoreValues(final boolean restoreValues) {
        this.ec.setProperty("datanucleus.RestoreValues", restoreValues);
    }
    
    @Override
    public void setRetainValues(final boolean retainValues) {
        this.ec.setProperty("datanucleus.RetainValues", retainValues);
        if (retainValues) {
            this.setNontransactionalRead(true);
        }
    }
    
    @Override
    public void setRollbackOnly() {
        if (this.active) {
            this.rollbackOnly = true;
        }
    }
    
    @Override
    public void setSynchronization(final Synchronization sync) {
        this.sync = sync;
    }
    
    @Override
    public void addTransactionEventListener(final TransactionEventListener listener) {
        this.listenersPerTransaction.add(listener);
    }
    
    @Override
    public void removeTransactionEventListener(final TransactionEventListener listener) {
        this.listenersPerTransaction.remove(listener);
        this.userListeners.remove(listener);
    }
    
    @Override
    public void bindTransactionEventListener(final TransactionEventListener listener) {
        this.userListeners.add(listener);
    }
    
    @Override
    public Boolean getSerializeRead() {
        return this.serializeRead;
    }
    
    @Override
    public void setSerializeRead(final Boolean serializeRead) {
        this.serializeRead = serializeRead;
    }
    
    @Override
    public Map<String, Object> getOptions() {
        return this.options;
    }
    
    @Override
    public void setOption(final String option, final int value) {
        if (this.options == null) {
            this.options = new HashMap<String, Object>();
        }
        this.options.put(option, value);
    }
    
    @Override
    public void setOption(final String option, final boolean value) {
        if (this.options == null) {
            this.options = new HashMap<String, Object>();
        }
        this.options.put(option, value);
    }
    
    @Override
    public void setOption(final String option, final String value) {
        if (this.options == null) {
            this.options = new HashMap<String, Object>();
        }
        this.options.put(option, value);
    }
    
    @Override
    public void setOption(final String option, final Object value) {
        if (this.options == null) {
            this.options = new HashMap<String, Object>();
        }
        this.options.put(option, value);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
