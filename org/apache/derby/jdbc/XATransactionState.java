// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.sql.SQLException;
import java.util.TimerTask;
import java.sql.Connection;
import javax.transaction.xa.XAException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.timer.TimerFactory;
import java.util.HashMap;
import org.apache.derby.iapi.store.access.xa.XAXactId;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.apache.derby.iapi.services.context.ContextImpl;

final class XATransactionState extends ContextImpl
{
    static final int TRO_TIMEOUT = -3;
    static final int TRO_DEADLOCK = -2;
    static final int TRO_FAIL = -1;
    static final int T0_NOT_ASSOCIATED = 0;
    static final int T1_ASSOCIATED = 1;
    static final int TC_COMPLETED = 3;
    final EmbedConnection conn;
    final EmbedXAResource creatingResource;
    private EmbedXAResource associatedResource;
    final XAXactId xid;
    HashMap<EmbedXAResource, XATransactionState> suspendedList;
    int associationState;
    int rollbackOnlyCode;
    boolean isPrepared;
    boolean performTimeoutRollback;
    CancelXATransactionTask timeoutTask;
    
    private static TimerFactory getTimerFactory() {
        return Monitor.getMonitor().getTimerFactory();
    }
    
    XATransactionState(final ContextManager contextManager, final EmbedConnection conn, final EmbedXAResource embedXAResource, final XAXactId xid) {
        super(contextManager, "XATransactionState");
        this.timeoutTask = null;
        this.conn = conn;
        this.associatedResource = embedXAResource;
        this.creatingResource = embedXAResource;
        this.associationState = 1;
        this.xid = xid;
        this.performTimeoutRollback = false;
    }
    
    public void cleanupOnError(final Throwable t) {
        if (t instanceof StandardException) {
            final StandardException ex = (StandardException)t;
            if (ex.getSeverity() >= 40000) {
                this.popMe();
                return;
            }
            if (ex.getSeverity() == 30000) {
                synchronized (this) {
                    this.notifyAll();
                    this.associationState = -1;
                    if ("40001".equals(ex.getMessageId())) {
                        this.rollbackOnlyCode = 102;
                    }
                    else if (ex.isLockTimeout()) {
                        this.rollbackOnlyCode = 106;
                    }
                    else {
                        this.rollbackOnlyCode = 104;
                    }
                }
            }
        }
    }
    
    void start(final EmbedXAResource associatedResource, final int n) throws XAException {
        synchronized (this) {
            if (this.associationState == -1) {
                throw new XAException(this.rollbackOnlyCode);
            }
            final boolean b = this.suspendedList != null && this.suspendedList.get(associatedResource) != null;
            if (n == 134217728) {
                if (!b) {
                    throw new XAException(-6);
                }
            }
            else if (b) {
                throw new XAException(-6);
            }
            while (this.associationState == 1) {
                try {
                    this.wait();
                    continue;
                }
                catch (InterruptedException ex) {
                    throw new XAException(4);
                }
                break;
            }
            switch (this.associationState) {
                case 0: {
                    if (this.isPrepared) {
                        throw new XAException(-6);
                    }
                    if (b) {
                        this.suspendedList.remove(associatedResource);
                    }
                    this.associationState = 1;
                    this.associatedResource = associatedResource;
                    break;
                }
                case -3:
                case -2:
                case -1: {
                    throw new XAException(this.rollbackOnlyCode);
                }
                default: {
                    throw new XAException(-4);
                }
            }
        }
    }
    
    boolean end(final EmbedXAResource embedXAResource, int n, final boolean b) throws XAException {
        boolean b2 = false;
        synchronized (this) {
            final boolean b3 = this.suspendedList != null && this.suspendedList.get(embedXAResource) != null;
            if (!b) {
                while (this.associationState == 1) {
                    try {
                        this.wait();
                        continue;
                    }
                    catch (InterruptedException ex) {
                        throw new XAException(4);
                    }
                    break;
                }
            }
            switch (this.associationState) {
                case 3: {
                    throw new XAException(-4);
                }
                case -1: {
                    if (b) {
                        n = 536870912;
                        break;
                    }
                    throw new XAException(this.rollbackOnlyCode);
                }
            }
            boolean b4 = false;
            switch (n) {
                case 67108864: {
                    if (b3) {
                        this.suspendedList.remove(embedXAResource);
                    }
                    else {
                        if (embedXAResource != this.associatedResource) {
                            throw new XAException(-6);
                        }
                        this.associationState = 0;
                        this.associatedResource = null;
                        b4 = true;
                    }
                    this.conn.setApplicationConnection(null);
                    break;
                }
                case 536870912: {
                    if (b3) {
                        this.suspendedList.remove(embedXAResource);
                    }
                    else {
                        if (embedXAResource != this.associatedResource) {
                            throw new XAException(-6);
                        }
                        this.associatedResource = null;
                    }
                    if (this.associationState != -1) {
                        this.associationState = -1;
                        this.rollbackOnlyCode = 100;
                    }
                    this.conn.setApplicationConnection(null);
                    b4 = true;
                    b2 = true;
                    break;
                }
                case 33554432: {
                    if (b3) {
                        throw new XAException(-6);
                    }
                    if (embedXAResource != this.associatedResource) {
                        throw new XAException(-6);
                    }
                    if (this.suspendedList == null) {
                        this.suspendedList = new HashMap<EmbedXAResource, XATransactionState>();
                    }
                    this.suspendedList.put(embedXAResource, this);
                    this.associationState = 0;
                    this.associatedResource = null;
                    this.conn.setApplicationConnection(null);
                    b4 = true;
                    break;
                }
                default: {
                    throw new XAException(-5);
                }
            }
            if (b4) {
                this.notifyAll();
            }
            return b2;
        }
    }
    
    synchronized void scheduleTimeoutTask(final long n) {
        this.performTimeoutRollback = true;
        if (n > 0L) {
            this.timeoutTask = new CancelXATransactionTask(this);
            getTimerFactory().schedule(this.timeoutTask, n);
        }
        else {
            this.timeoutTask = null;
        }
    }
    
    synchronized void xa_rollback() throws SQLException {
        this.conn.xa_rollback();
        this.xa_finalize();
    }
    
    synchronized void xa_commit(final boolean b) throws SQLException {
        this.conn.xa_commit(b);
        this.xa_finalize();
    }
    
    synchronized int xa_prepare() throws SQLException {
        final int xa_prepare = this.conn.xa_prepare();
        if (xa_prepare == 1) {
            this.xa_finalize();
        }
        return xa_prepare;
    }
    
    private void xa_finalize() {
        if (this.timeoutTask != null) {
            getTimerFactory().cancel(this.timeoutTask);
            this.timeoutTask = null;
        }
        this.performTimeoutRollback = false;
    }
    
    synchronized void cancel(final String s) throws XAException {
        if (this.performTimeoutRollback) {
            if (s != null) {
                Monitor.logTextMessage(s, this.xid.toString());
            }
            if (this.associationState == 1) {
                this.conn.cancelRunningStatement();
                this.end(this.associatedResource, 536870912, true);
            }
            try {
                this.conn.xa_rollback();
            }
            catch (SQLException cause) {
                final XAException ex = new XAException(-3);
                ex.initCause(cause);
                throw ex;
            }
            this.creatingResource.returnConnectionToResource(this, this.xid);
        }
    }
    
    private static class CancelXATransactionTask extends TimerTask
    {
        private XATransactionState xaState;
        
        public CancelXATransactionTask(final XATransactionState xaState) {
            this.xaState = xaState;
        }
        
        @Override
        public boolean cancel() {
            this.xaState = null;
            return super.cancel();
        }
        
        @Override
        public void run() {
            try {
                this.xaState.cancel("J135");
            }
            catch (Throwable t) {
                Monitor.logThrowable(t);
            }
        }
    }
}
