// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.util.NucleusLogger;
import javax.naming.Context;
import javax.transaction.NotSupportedException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.transaction.SystemException;
import org.datanucleus.transaction.NucleusTransactionException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.connection.ConnectionResourceType;
import javax.transaction.UserTransaction;
import org.datanucleus.transaction.jta.JTASyncRegistry;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Synchronization;

public class JTATransactionImpl extends TransactionImpl implements Synchronization
{
    private static boolean JBOSS_SERVER;
    private TransactionManager jtaTM;
    private javax.transaction.Transaction jtaTx;
    private JTASyncRegistry jtaSyncRegistry;
    protected JoinStatus joinStatus;
    private UserTransaction userTransaction;
    protected boolean autoJoin;
    
    JTATransactionImpl(final ExecutionContext ec, final boolean autoJoin) {
        super(ec);
        this.joinStatus = JoinStatus.NO_TXN;
        this.autoJoin = true;
        this.autoJoin = autoJoin;
        final PersistenceConfiguration conf = ec.getNucleusContext().getPersistenceConfiguration();
        if (!ConnectionResourceType.JTA.toString().equalsIgnoreCase(conf.getStringProperty("datanucleus.connection.resourceType")) || !ConnectionResourceType.JTA.toString().equalsIgnoreCase(conf.getStringProperty("datanucleus.connection2.resourceType"))) {
            throw new NucleusException("Internal error: either datanucleus.connection.resourceType or datanucleus.connection2.resourceType have not been set to JTA; this should have happened automatically.");
        }
        this.txnMgr.setContainerManagedConnections(true);
        this.jtaTM = ec.getNucleusContext().getJtaTransactionManager();
        if (this.jtaTM == null) {
            throw new NucleusTransactionException(JTATransactionImpl.LOCALISER.msg("015030"));
        }
        this.jtaSyncRegistry = ec.getNucleusContext().getJtaSyncRegistry();
        if (autoJoin) {
            this.joinTransaction();
        }
    }
    
    public boolean isJoined() {
        return this.joinStatus == JoinStatus.JOINED;
    }
    
    private int getTransactionStatus() {
        try {
            return this.jtaTM.getStatus();
        }
        catch (SystemException se) {
            throw new NucleusTransactionException(JTATransactionImpl.LOCALISER.msg("015026"), se);
        }
    }
    
    public void joinTransaction() {
        if (this.joinStatus != JoinStatus.JOINED) {
            try {
                final javax.transaction.Transaction txn = this.jtaTM.getTransaction();
                final int txnstat = this.jtaTM.getStatus();
                if (this.jtaTx != null && !this.jtaTx.equals(txn)) {
                    if (this.joinStatus != JoinStatus.IMPOSSIBLE) {
                        throw new InternalError("JTA Transaction changed without being notified");
                    }
                    this.jtaTx = null;
                    this.joinStatus = JoinStatus.NO_TXN;
                    this.joinTransaction();
                }
                else if (this.jtaTx == null) {
                    this.jtaTx = txn;
                    final boolean allow_join = txnstat == 0;
                    if (allow_join) {
                        this.joinStatus = JoinStatus.IMPOSSIBLE;
                        try {
                            if (this.jtaSyncRegistry != null) {
                                this.jtaSyncRegistry.register(this);
                            }
                            else {
                                this.jtaTx.registerSynchronization(this);
                            }
                            final boolean was_active = super.isActive();
                            if (!was_active) {
                                this.internalBegin();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new NucleusTransactionException("Cannot register Synchronization to a valid JTA Transaction");
                        }
                        this.joinStatus = JoinStatus.JOINED;
                    }
                    else if (this.jtaTx != null) {
                        this.joinStatus = JoinStatus.IMPOSSIBLE;
                    }
                }
            }
            catch (SystemException e2) {
                throw new NucleusTransactionException(JTATransactionImpl.LOCALISER.msg("015026"), e2);
            }
        }
    }
    
    @Override
    public boolean getIsActive() {
        if (this.closed) {
            return false;
        }
        final int txnStatus = this.getTransactionStatus();
        return txnStatus != 3 && txnStatus != 4 && super.getIsActive();
    }
    
    @Override
    public boolean isActive() {
        if (!this.autoJoin) {
            return super.isActive();
        }
        if (this.joinStatus == JoinStatus.JOINED) {
            return super.isActive();
        }
        this.joinTransaction();
        return super.isActive() || this.joinStatus == JoinStatus.IMPOSSIBLE;
    }
    
    @Override
    public void begin() {
        this.joinTransaction();
        if (this.joinStatus != JoinStatus.NO_TXN) {
            throw new NucleusTransactionException("JTA Transaction is already active");
        }
        UserTransaction utx;
        try {
            final Context ctx = new InitialContext();
            if (JTATransactionImpl.JBOSS_SERVER) {
                utx = (UserTransaction)ctx.lookup("UserTransaction");
            }
            else {
                utx = (UserTransaction)ctx.lookup("java:comp/UserTransaction");
            }
        }
        catch (NamingException e) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to obtain UserTransaction", e);
        }
        try {
            utx.begin();
        }
        catch (NotSupportedException e2) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to begin UserTransaction", e2);
        }
        catch (SystemException e3) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to begin UserTransaction", e3);
        }
        this.joinTransaction();
        if (this.joinStatus != JoinStatus.JOINED) {
            throw new NucleusTransactionException("Cannot join an auto started UserTransaction");
        }
        this.userTransaction = utx;
    }
    
    @Override
    public void commit() {
        if (this.userTransaction == null) {
            throw new NucleusTransactionException("No internal UserTransaction");
        }
        try {
            this.userTransaction.commit();
        }
        catch (Exception e) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to commit UserTransaction", e);
        }
        finally {
            this.userTransaction = null;
        }
    }
    
    @Override
    public void rollback() {
        if (this.userTransaction == null) {
            throw new NucleusTransactionException("No internal UserTransaction");
        }
        try {
            this.userTransaction.rollback();
        }
        catch (Exception e) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to rollback UserTransaction", e);
        }
        finally {
            this.userTransaction = null;
        }
    }
    
    @Override
    public void setRollbackOnly() {
        if (this.userTransaction == null) {
            throw new NucleusTransactionException("No internal UserTransaction");
        }
        try {
            this.userTransaction.setRollbackOnly();
        }
        catch (Exception e) {
            throw this.ec.getApiAdapter().getUserExceptionForException("Failed to rollback-only UserTransaction", e);
        }
    }
    
    @Override
    public void beforeCompletion() {
        RuntimeException thr = null;
        boolean success = false;
        try {
            this.flush();
            this.internalPreCommit();
            this.flush();
            success = true;
        }
        catch (RuntimeException e) {
            thr = e;
            throw e;
        }
        finally {
            if (!success) {
                NucleusLogger.TRANSACTION.error(JTATransactionImpl.LOCALISER.msg("015044"), thr);
                try {
                    this.jtaTx.setRollbackOnly();
                }
                catch (Exception e2) {
                    NucleusLogger.TRANSACTION.fatal(JTATransactionImpl.LOCALISER.msg("015045"), e2);
                }
            }
        }
    }
    
    @Override
    public synchronized void afterCompletion(final int status) {
        if (this.closed) {
            NucleusLogger.TRANSACTION.warn(JTATransactionImpl.LOCALISER.msg("015048", this));
            return;
        }
        RuntimeException thr = null;
        boolean success = false;
        try {
            if (status == 4) {
                super.rollback();
            }
            else if (status == 3) {
                super.internalPostCommit();
            }
            else {
                NucleusLogger.TRANSACTION.fatal(JTATransactionImpl.LOCALISER.msg("015047", status));
            }
            success = true;
        }
        catch (RuntimeException re) {
            thr = re;
            NucleusLogger.TRANSACTION.error("Exception in afterCompletion : " + re.getMessage(), re);
            throw re;
        }
        finally {
            this.jtaTx = null;
            this.joinStatus = JoinStatus.NO_TXN;
            if (!success) {
                NucleusLogger.TRANSACTION.error(JTATransactionImpl.LOCALISER.msg("015046"), thr);
            }
        }
        if (this.active) {
            throw new NucleusTransactionException("internal error, must not be active after afterCompletion()!");
        }
    }
    
    static {
        JTATransactionImpl.JBOSS_SERVER = (System.getProperty("jboss.server.name") != null);
    }
    
    private enum JoinStatus
    {
        NO_TXN, 
        IMPOSSIBLE, 
        JOINED;
    }
}
