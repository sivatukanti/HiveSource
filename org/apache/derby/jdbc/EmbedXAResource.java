// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.apache.derby.impl.jdbc.TransactionResourceImpl;
import java.sql.Connection;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.access.xa.XAResourceManager;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextService;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.apache.derby.iapi.store.access.xa.XAXactId;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.transaction.xa.XAResource;

class EmbedXAResource implements XAResource
{
    private EmbedPooledConnection con;
    private ResourceAdapter ra;
    private XAXactId currentXid;
    private int timeoutSeconds;
    
    EmbedXAResource(final EmbedPooledConnection con, final ResourceAdapter ra) {
        this.con = con;
        this.ra = ra;
        this.timeoutSeconds = 0;
    }
    
    public final synchronized void commit(final Xid xid, final boolean b) throws XAException {
        this.checkXAActive();
        final XAXactId xaXactId = new XAXactId(xid);
        final XATransactionState transactionState = this.getTransactionState(xaXactId);
        if (transactionState == null) {
            final XAResourceManager xaResourceManager = this.ra.getXAResourceManager();
            final ContextManager find = xaResourceManager.find(xid);
            if (find == null) {
                throw new XAException(-4);
            }
            final ContextService factory = ContextService.getFactory();
            factory.setCurrentContextManager(find);
            try {
                xaResourceManager.commit(find, xaXactId, b);
                find.cleanupOnError(StandardException.closeException(), false);
                return;
            }
            catch (StandardException ex) {
                find.cleanupOnError(ex, this.con.isActive());
                throw wrapInXAException(ex);
            }
            finally {
                factory.resetCurrentContextManager(find);
            }
        }
        synchronized (transactionState) {
            this.checkUserCredentials(transactionState.creatingResource);
            switch (transactionState.associationState) {
                case 0: {
                    if (transactionState.suspendedList != null && transactionState.suspendedList.size() != 0) {
                        throw new XAException(-6);
                    }
                    if (transactionState.isPrepared == b) {
                        throw new XAException(-6);
                    }
                    try {
                        transactionState.xa_commit(b);
                    }
                    catch (SQLException ex2) {
                        throw wrapInXAException(ex2);
                    }
                    finally {
                        this.returnConnectionToResource(transactionState, xaXactId);
                    }
                    break;
                }
                case -1: {
                    throw new XAException(transactionState.rollbackOnlyCode);
                }
                default: {
                    throw new XAException(-6);
                }
            }
        }
    }
    
    public final synchronized void end(final Xid xid, final int n) throws XAException {
        this.checkXAActive();
        try {
            if (this.con.currentConnectionHandle != null) {
                this.con.currentConnectionHandle.getIsolationUptoDate();
            }
        }
        catch (SQLException ex) {
            throw wrapInXAException(ex);
        }
        final XAXactId xaXactId = new XAXactId(xid);
        boolean b = false;
        if (this.currentXid != null) {
            if (!this.currentXid.equals(xaXactId)) {
                throw new XAException(-6);
            }
            b = true;
        }
        final XATransactionState transactionState = this.getTransactionState(xaXactId);
        if (transactionState == null) {
            throw new XAException(-4);
        }
        final boolean end = transactionState.end(this, n, b);
        if (b) {
            this.currentXid = null;
            this.con.realConnection = null;
        }
        if (end) {
            throw new XAException(transactionState.rollbackOnlyCode);
        }
    }
    
    public final synchronized int prepare(final Xid xid) throws XAException {
        this.checkXAActive();
        final XAXactId xaXactId = new XAXactId(xid);
        final XATransactionState transactionState = this.getTransactionState(xaXactId);
        if (transactionState == null) {
            if (this.ra.getXAResourceManager().find(xid) == null) {
                throw new XAException(-4);
            }
            throw new XAException(-6);
        }
        else {
            synchronized (transactionState) {
                this.checkUserCredentials(transactionState.creatingResource);
                switch (transactionState.associationState) {
                    case 0: {
                        if (transactionState.suspendedList != null && transactionState.suspendedList.size() != 0) {
                            throw new XAException(-6);
                        }
                        if (transactionState.isPrepared) {
                            throw new XAException(-6);
                        }
                        try {
                            if (transactionState.xa_prepare() == 2) {
                                transactionState.isPrepared = true;
                                return 0;
                            }
                            this.returnConnectionToResource(transactionState, xaXactId);
                            return 3;
                        }
                        catch (SQLException ex) {
                            throw wrapInXAException(ex);
                        }
                        break;
                    }
                    case -1: {
                        throw new XAException(transactionState.rollbackOnlyCode);
                    }
                    default: {
                        throw new XAException(-6);
                    }
                }
            }
        }
    }
    
    public synchronized int getTransactionTimeout() {
        return this.timeoutSeconds;
    }
    
    public final synchronized boolean isSameRM(final XAResource xaResource) throws XAException {
        this.checkXAActive();
        return xaResource instanceof EmbedXAResource && this.ra == ((EmbedXAResource)xaResource).ra;
    }
    
    public final synchronized Xid[] recover(final int n) throws XAException {
        this.checkXAActive();
        try {
            return this.ra.getXAResourceManager().recover(n);
        }
        catch (StandardException ex) {
            throw wrapInXAException(ex);
        }
    }
    
    public final synchronized void forget(final Xid xid) throws XAException {
        this.checkXAActive();
        final XAXactId xaXactId = new XAXactId(xid);
        if (this.getTransactionState(xaXactId) == null) {
            final XAResourceManager xaResourceManager = this.ra.getXAResourceManager();
            final ContextManager find = xaResourceManager.find(xid);
            if (find == null) {
                throw new XAException(-4);
            }
            final ContextService factory = ContextService.getFactory();
            factory.setCurrentContextManager(find);
            try {
                xaResourceManager.forget(find, xaXactId);
                find.cleanupOnError(StandardException.closeException(), false);
                return;
            }
            catch (StandardException ex) {
                find.cleanupOnError(ex, this.con.isActive());
                throw wrapInXAException(ex);
            }
            finally {
                factory.resetCurrentContextManager(find);
            }
        }
        throw new XAException(-6);
    }
    
    public final synchronized void rollback(final Xid xid) throws XAException {
        this.checkXAActive();
        final XAXactId xaXactId = new XAXactId(xid);
        final XATransactionState transactionState = this.getTransactionState(xaXactId);
        if (transactionState == null) {
            final XAResourceManager xaResourceManager = this.ra.getXAResourceManager();
            final ContextManager find = xaResourceManager.find(xid);
            if (find == null) {
                throw new XAException(-4);
            }
            final ContextService factory = ContextService.getFactory();
            factory.setCurrentContextManager(find);
            try {
                xaResourceManager.rollback(find, xaXactId);
                find.cleanupOnError(StandardException.closeException(), false);
                return;
            }
            catch (StandardException ex) {
                find.cleanupOnError(ex, this.con.isActive());
                throw wrapInXAException(ex);
            }
            finally {
                factory.resetCurrentContextManager(find);
            }
        }
        synchronized (transactionState) {
            switch (transactionState.associationState) {
                case -1:
                case 0: {
                    if (transactionState.suspendedList != null && transactionState.suspendedList.size() != 0) {
                        throw new XAException(-6);
                    }
                    this.checkUserCredentials(transactionState.creatingResource);
                    try {
                        transactionState.xa_rollback();
                    }
                    catch (SQLException ex2) {
                        throw wrapInXAException(ex2);
                    }
                    finally {
                        this.returnConnectionToResource(transactionState, xaXactId);
                    }
                    break;
                }
                default: {
                    throw new XAException(-6);
                }
            }
        }
    }
    
    public synchronized boolean setTransactionTimeout(final int timeoutSeconds) throws XAException {
        if (timeoutSeconds < 0) {
            throw new XAException(-5);
        }
        this.timeoutSeconds = timeoutSeconds;
        return true;
    }
    
    private long getDefaultXATransactionTimeout() throws XAException {
        try {
            return 1000L * PropertyUtil.getServiceInt(this.con.getLanguageConnection().getTransactionExecute(), "derby.jdbc.xaTransactionTimeout", 0, Integer.MAX_VALUE, 0);
        }
        catch (SQLException ex) {
            throw wrapInXAException(ex);
        }
        catch (StandardException ex2) {
            throw wrapInXAException(ex2);
        }
    }
    
    public final synchronized void start(final Xid xid, final int n) throws XAException {
        this.checkXAActive();
        if (this.currentXid != null) {
            throw new XAException(-6);
        }
        final XAXactId xaXactId = new XAXactId(xid);
        final XATransactionState transactionState = this.getTransactionState(xaXactId);
        Label_0619: {
            switch (n) {
                case 0: {
                    if (transactionState != null) {
                        throw new XAException(-8);
                    }
                    try {
                        if (this.con.realConnection == null) {
                            this.con.openRealConnection();
                            if (this.con.currentConnectionHandle != null) {
                                this.con.currentConnectionHandle.setState(true);
                                this.con.realConnection.setApplicationConnection(this.con.currentConnectionHandle);
                            }
                        }
                        else {
                            if (this.con.currentConnectionHandle != null && this.con.currentConnectionHandle.getAutoCommit()) {
                                this.con.currentConnectionHandle.rollback();
                            }
                            if (!this.con.realConnection.transactionIsIdle()) {
                                throw new XAException(-9);
                            }
                            if (this.con.currentConnectionHandle != null) {
                                this.con.currentConnectionHandle.getIsolationUptoDate();
                                this.con.currentConnectionHandle.setState(true);
                                this.con.realConnection.rollback();
                            }
                            else {
                                this.con.resetRealConnection();
                            }
                        }
                        this.con.realConnection.setAutoCommit(false);
                        this.con.realConnection.setHoldability(2);
                        this.con.realConnection.getLanguageConnection().getTransactionExecute().createXATransactionFromLocalTransaction(xaXactId.getFormatId(), xaXactId.getGlobalTransactionId(), xaXactId.getBranchQualifier());
                    }
                    catch (StandardException ex) {
                        throw wrapInXAException(ex);
                    }
                    catch (SQLException ex2) {
                        throw wrapInXAException(ex2);
                    }
                    final XATransactionState xaTransactionState = new XATransactionState(this.con.realConnection.getContextManager(), this.con.realConnection, this, xaXactId);
                    if (!this.ra.addConnection(xaXactId, xaTransactionState)) {
                        throw new XAException(-8);
                    }
                    this.currentXid = xaXactId;
                    if (this.timeoutSeconds != Integer.MAX_VALUE) {
                        long defaultXATransactionTimeout;
                        if (this.timeoutSeconds > 0) {
                            defaultXATransactionTimeout = 1000 * this.timeoutSeconds;
                        }
                        else {
                            defaultXATransactionTimeout = this.getDefaultXATransactionTimeout();
                        }
                        if (defaultXATransactionTimeout > 0L) {
                            xaTransactionState.scheduleTimeoutTask(defaultXATransactionTimeout);
                        }
                    }
                    break Label_0619;
                }
                case 2097152:
                case 134217728: {
                    if (transactionState == null) {
                        throw new XAException(-4);
                    }
                    transactionState.start(this, n);
                    if (transactionState.conn == this.con.realConnection) {
                        break Label_0619;
                    }
                    if (this.con.realConnection != null) {
                        if (!this.con.realConnection.transactionIsIdle()) {
                            throw new XAException(-9);
                        }
                        try {
                            if (this.con.currentConnectionHandle != null) {
                                this.con.currentConnectionHandle.getIsolationUptoDate();
                            }
                        }
                        catch (SQLException ex3) {
                            throw wrapInXAException(ex3);
                        }
                        closeUnusedConnection(this.con.realConnection);
                    }
                    this.con.realConnection = transactionState.conn;
                    if (this.con.currentConnectionHandle != null) {
                        try {
                            this.con.currentConnectionHandle.setState(false);
                            this.con.realConnection.setApplicationConnection(this.con.currentConnectionHandle);
                            break Label_0619;
                        }
                        catch (SQLException ex4) {
                            throw wrapInXAException(ex4);
                        }
                        break;
                    }
                    break Label_0619;
                }
            }
            throw new XAException(-5);
        }
        this.currentXid = xaXactId;
    }
    
    Xid getCurrentXid() {
        return this.currentXid;
    }
    
    private XATransactionState getTransactionState(final XAXactId xaXactId) {
        return (XATransactionState)this.ra.findConnection(xaXactId);
    }
    
    private void checkUserCredentials(final EmbedXAResource embedXAResource) throws XAException {
        if (embedXAResource == this) {
            return;
        }
        if (embedXAResource.con.getPassword().equals(this.con.getPassword()) && embedXAResource.con.getUsername().equals(this.con.getUsername())) {
            return;
        }
        throw new XAException(103);
    }
    
    private void checkXAActive() throws XAException {
        try {
            this.con.checkActive();
        }
        catch (SQLException ex) {
            throw wrapInXAException(ex);
        }
    }
    
    private static XAException wrapInXAException(final SQLException cause) {
        final String sqlState = cause.getSQLState();
        final String message = cause.getMessage();
        final int errorCode = cause.getErrorCode();
        int errorCode2;
        if (sqlState.equals(StandardException.getSQLStateFromIdentifier("XSAX1.S"))) {
            errorCode2 = -8;
        }
        else if (sqlState.equals(StandardException.getSQLStateFromIdentifier("XSAX0.S"))) {
            errorCode2 = 105;
        }
        else if (sqlState.equals("40001")) {
            errorCode2 = 102;
        }
        else if (sqlState.equals("40XL1")) {
            errorCode2 = 106;
        }
        else if (errorCode >= 40000) {
            errorCode2 = -7;
        }
        else {
            errorCode2 = -3;
        }
        final XAException ex = new XAException(message);
        ex.errorCode = errorCode2;
        ex.initCause(cause);
        return ex;
    }
    
    private static XAException wrapInXAException(final StandardException ex) {
        return wrapInXAException(TransactionResourceImpl.wrapInSQLException(ex));
    }
    
    void returnConnectionToResource(final XATransactionState xaTransactionState, final XAXactId xaXactId) {
        this.removeXATransaction(xaXactId);
        synchronized (xaTransactionState) {
            xaTransactionState.associationState = 3;
            xaTransactionState.notifyAll();
            final EmbedConnection conn = xaTransactionState.conn;
            if (xaTransactionState.creatingResource.con.realConnection == conn || xaTransactionState.creatingResource.con.realConnection == null) {
                xaTransactionState.creatingResource.con.realConnection = conn;
                final BrokeredConnection currentConnectionHandle = xaTransactionState.creatingResource.con.currentConnectionHandle;
                conn.setApplicationConnection(currentConnectionHandle);
                if (currentConnectionHandle != null) {
                    try {
                        currentConnectionHandle.setState(true);
                    }
                    catch (SQLException ex) {
                        closeUnusedConnection(xaTransactionState.conn);
                        xaTransactionState.creatingResource.con.realConnection = null;
                    }
                }
                return;
            }
        }
        closeUnusedConnection(xaTransactionState.conn);
    }
    
    private static void closeUnusedConnection(final EmbedConnection embedConnection) {
        if (embedConnection != null) {
            try {
                embedConnection.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    void removeXATransaction(final XAXactId xaXactId) {
        final XATransactionState xaTransactionState = (XATransactionState)this.ra.removeConnection(xaXactId);
        if (xaTransactionState != null) {
            xaTransactionState.popMe();
        }
    }
    
    void setCurrentXid(final XAXactId currentXid) {
        this.currentXid = currentXid;
    }
}
