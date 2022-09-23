// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

import org.datanucleus.NucleusContext;
import org.datanucleus.ClassConstants;
import java.util.Iterator;
import javax.transaction.xa.XAException;
import org.omg.CORBA.SystemException;
import org.datanucleus.util.NucleusLogger;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import javax.transaction.xa.XAResource;
import javax.transaction.Synchronization;
import java.util.List;
import javax.transaction.xa.Xid;
import org.datanucleus.util.Localiser;

public class Transaction
{
    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_MARKED_ROLLBACK = 1;
    public static final int STATUS_PREPARED = 2;
    public static final int STATUS_COMMITTED = 3;
    public static final int STATUS_ROLLEDBACK = 4;
    public static final int STATUS_UNKNOWN = 5;
    public static final int STATUS_NO_TRANSACTION = 6;
    public static final int STATUS_PREPARING = 7;
    public static final int STATUS_COMMITTING = 8;
    public static final int STATUS_ROLLING_BACK = 9;
    protected static final Localiser LOCALISER;
    private static final int nodeId;
    private static int nextGlobalTransactionId;
    private int nextBranchId;
    private final Xid xid;
    private int status;
    private boolean completing;
    private List<Synchronization> synchronization;
    private List<XAResource> enlistedResources;
    private Map<Xid, XAResource> branches;
    private Map<XAResource, Xid> activeBranches;
    private Map<XAResource, Xid> suspendedResources;
    
    Transaction() {
        this.nextBranchId = 1;
        this.completing = false;
        this.synchronization = null;
        this.enlistedResources = new ArrayList<XAResource>();
        this.branches = new HashMap<Xid, XAResource>();
        this.activeBranches = new HashMap<XAResource, Xid>();
        this.suspendedResources = new HashMap<XAResource, Xid>();
        this.xid = new XidImpl(Transaction.nodeId, 0, Transaction.nextGlobalTransactionId++);
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug("Transaction created " + this.toString());
        }
    }
    
    public int getStatus() throws SystemException {
        return this.status;
    }
    
    public boolean isEnlisted(final XAResource xaRes) {
        if (xaRes == null) {
            return false;
        }
        final Xid activeXid = this.activeBranches.get(xaRes);
        if (activeXid != null) {
            return true;
        }
        final Xid branchXid = this.suspendedResources.get(xaRes);
        if (branchXid == null) {
            for (final XAResource resourceManager : this.enlistedResources) {
                try {
                    if (resourceManager.isSameRM(xaRes)) {
                        return true;
                    }
                    continue;
                }
                catch (XAException ex) {}
            }
            return false;
        }
        return true;
    }
    
    public boolean enlistResource(final XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        if (xaRes == null) {
            return false;
        }
        if (this.status == 1) {
            throw new RollbackException();
        }
        if (this.status != 0) {
            throw new IllegalStateException();
        }
        final Xid activeXid = this.activeBranches.get(xaRes);
        if (activeXid != null) {
            return false;
        }
        boolean alreadyEnlisted = false;
        int flag = 0;
        Xid branchXid = this.suspendedResources.get(xaRes);
        if (branchXid == null) {
            final Iterator<XAResource> enlistedIterator = this.enlistedResources.iterator();
            while (!alreadyEnlisted && enlistedIterator.hasNext()) {
                final XAResource resourceManager = enlistedIterator.next();
                try {
                    if (!resourceManager.isSameRM(xaRes)) {
                        continue;
                    }
                    flag = 2097152;
                    alreadyEnlisted = true;
                }
                catch (XAException ex) {}
            }
            branchXid = new XidImpl(this.nextBranchId++, this.xid.getFormatId(), this.xid.getGlobalTransactionId());
        }
        else {
            alreadyEnlisted = true;
            flag = 134217728;
            this.suspendedResources.remove(xaRes);
        }
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(Transaction.LOCALISER.msg("015039", "enlist", xaRes, getXAFlag(flag), this.toString()));
        }
        try {
            xaRes.start(branchXid, flag);
        }
        catch (XAException e) {
            NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "enlist", xaRes, getXAErrorCode(e), this.toString()));
            return false;
        }
        if (!alreadyEnlisted) {
            this.enlistedResources.add(xaRes);
        }
        this.branches.put(branchXid, xaRes);
        this.activeBranches.put(xaRes, branchXid);
        return true;
    }
    
    public boolean delistResource(final XAResource xaRes, final int flag) throws IllegalStateException, SystemException {
        if (xaRes == null) {
            return false;
        }
        if (this.status != 0) {
            throw new IllegalStateException();
        }
        final Xid xid = this.activeBranches.get(xaRes);
        if (xid == null) {
            throw new IllegalStateException();
        }
        this.activeBranches.remove(xaRes);
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(Transaction.LOCALISER.msg("015039", "delist", xaRes, getXAFlag(flag), this.toString()));
        }
        XAException exception = null;
        try {
            xaRes.end(xid, flag);
        }
        catch (XAException e) {
            exception = e;
        }
        if (exception != null) {
            NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "delist", xaRes, getXAErrorCode(exception), this.toString()));
            return false;
        }
        if (flag == 33554432) {
            this.suspendedResources.put(xaRes, xid);
        }
        return true;
    }
    
    public void registerSynchronization(final Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        if (sync == null) {
            return;
        }
        if (this.status == 1) {
            throw new RollbackException();
        }
        if (this.status != 0) {
            throw new IllegalStateException();
        }
        if (this.synchronization == null) {
            this.synchronization = new ArrayList<Synchronization>();
        }
        this.synchronization.add(sync);
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        if (this.completing) {
            return;
        }
        if (this.status == 1) {
            this.rollback();
            return;
        }
        try {
            this.completing = true;
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Committing " + this.toString());
            }
            if (this.status != 0) {
                throw new IllegalStateException();
            }
            if (this.synchronization != null) {
                final Iterator<Synchronization> syncIterator = this.synchronization.iterator();
                while (syncIterator.hasNext()) {
                    syncIterator.next().beforeCompletion();
                }
            }
            List failures = null;
            boolean failed = false;
            Iterator<Xid> branchKeys = this.branches.keySet().iterator();
            if (this.enlistedResources.size() == 1) {
                this.status = 8;
                while (branchKeys.hasNext()) {
                    final Xid key = branchKeys.next();
                    final XAResource resourceManager = this.branches.get(key);
                    try {
                        if (!failed) {
                            resourceManager.commit(key, true);
                        }
                        else {
                            resourceManager.rollback(key);
                        }
                    }
                    catch (Throwable e) {
                        if (failures == null) {
                            failures = new ArrayList();
                        }
                        failures.add(e);
                        failed = true;
                        this.status = 1;
                        NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "commit", resourceManager, getXAErrorCode(e), this.toString()));
                    }
                }
                if (!failed) {
                    this.status = 3;
                }
                else {
                    this.status = 4;
                }
            }
            else if (this.enlistedResources.size() > 0) {
                this.status = 7;
                while (!failed && branchKeys.hasNext()) {
                    final Xid key = branchKeys.next();
                    final XAResource resourceManager = this.branches.get(key);
                    try {
                        resourceManager.prepare(key);
                    }
                    catch (Throwable e) {
                        if (failures == null) {
                            failures = new ArrayList();
                        }
                        failures.add(e);
                        failed = true;
                        this.status = 1;
                        NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "prepare", resourceManager, getXAErrorCode(e), this.toString()));
                    }
                }
                if (!failed) {
                    this.status = 2;
                }
                if (failed) {
                    this.status = 9;
                    failed = false;
                    branchKeys = this.branches.keySet().iterator();
                    while (branchKeys.hasNext()) {
                        final Xid key = branchKeys.next();
                        final XAResource resourceManager = this.branches.get(key);
                        try {
                            resourceManager.rollback(key);
                        }
                        catch (Throwable e) {
                            NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "rollback", resourceManager, getXAErrorCode(e), this.toString()));
                            if (failures == null) {
                                failures = new ArrayList();
                            }
                            failures.add(e);
                            failed = true;
                        }
                    }
                    this.status = 4;
                }
                else {
                    this.status = 8;
                    branchKeys = this.branches.keySet().iterator();
                    while (branchKeys.hasNext()) {
                        final Xid key = branchKeys.next();
                        final XAResource resourceManager = this.branches.get(key);
                        try {
                            resourceManager.commit(key, false);
                        }
                        catch (Throwable e) {
                            NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "commit", resourceManager, getXAErrorCode(e), this.toString()));
                            if (failures == null) {
                                failures = new ArrayList();
                            }
                            failures.add(e);
                            failed = true;
                        }
                    }
                    this.status = 3;
                }
            }
            if (this.synchronization != null) {
                final Iterator<Synchronization> syncIterator2 = this.synchronization.iterator();
                while (syncIterator2.hasNext()) {
                    syncIterator2.next().afterCompletion(this.status);
                }
            }
            if (this.status == 4) {
                if (!failed) {
                    throw new RollbackException();
                }
                if (failures.size() == 1) {
                    throw new HeuristicRollbackException("Transaction rolled back due to failure during commit", failures.get(0));
                }
                throw new HeuristicRollbackException("Multiple failures");
            }
            else if (this.status == 3 && failed) {
                throw new HeuristicMixedException();
            }
        }
        finally {
            this.completing = false;
        }
    }
    
    public void rollback() throws IllegalStateException, SystemException {
        if (this.completing) {
            return;
        }
        try {
            this.completing = true;
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Rolling back " + this.toString());
            }
            if (this.status != 0 && this.status != 1) {
                throw new IllegalStateException();
            }
            List failures = null;
            final Iterator<Xid> branchKeys = this.branches.keySet().iterator();
            this.status = 9;
            while (branchKeys.hasNext()) {
                final Xid xid = branchKeys.next();
                final XAResource resourceManager = this.branches.get(xid);
                try {
                    resourceManager.rollback(xid);
                }
                catch (Throwable e) {
                    if (failures == null) {
                        failures = new ArrayList();
                    }
                    failures.add(e);
                    NucleusLogger.TRANSACTION.error(Transaction.LOCALISER.msg("015038", "rollback", resourceManager, getXAErrorCode(e), this.toString()));
                }
            }
            this.status = 4;
            if (this.synchronization != null) {
                final Iterator<Synchronization> syncIterator = this.synchronization.iterator();
                while (syncIterator.hasNext()) {
                    syncIterator.next().afterCompletion(this.status);
                }
            }
        }
        finally {
            this.completing = false;
        }
    }
    
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        this.status = 1;
    }
    
    public static String getXAErrorCode(final Throwable xae) {
        if (!(xae instanceof XAException)) {
            return "UNKNOWN";
        }
        switch (((XAException)xae).errorCode) {
            case 7: {
                return "XA_HEURCOM";
            }
            case 8: {
                return "XA_HEURHAZ";
            }
            case 5: {
                return "XA_HEURMIX";
            }
            case 6: {
                return "XA_HEURRB";
            }
            case 9: {
                return "XA_NOMIGRATE";
            }
            case 100: {
                return "XA_RBBASE";
            }
            case 101: {
                return "XA_RBCOMMFAIL";
            }
            case 102: {
                return "XA_RBBEADLOCK";
            }
            case 107: {
                return "XA_RBEND";
            }
            case 103: {
                return "XA_RBINTEGRITY";
            }
            case 104: {
                return "XA_RBOTHER";
            }
            case 105: {
                return "XA_RBPROTO";
            }
            case 106: {
                return "XA_RBTIMEOUT";
            }
            case 3: {
                return "XA_RDONLY";
            }
            case 4: {
                return "XA_RETRY";
            }
            case -2: {
                return "XAER_ASYNC";
            }
            case -8: {
                return "XAER_DUPID";
            }
            case -5: {
                return "XAER_INVAL";
            }
            case -4: {
                return "XAER_NOTA";
            }
            case -9: {
                return "XAER_OUTSIDE";
            }
            case -6: {
                return "XAER_PROTO";
            }
            case -3: {
                return "XAER_RMERR";
            }
            case -7: {
                return "XAER_RMFAIL";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    private static String getXAFlag(final int flag) {
        switch (flag) {
            case 8388608: {
                return "TMENDRSCAN";
            }
            case 536870912: {
                return "TMFAIL";
            }
            case 2097152: {
                return "TMJOIN";
            }
            case 0: {
                return "TMNOFLAGS";
            }
            case 1073741824: {
                return "TMONEPHASE";
            }
            case 134217728: {
                return "TMRESUME";
            }
            case 16777216: {
                return "TMSTARTRSCAN";
            }
            case 67108864: {
                return "TMSUCCESS";
            }
            case 33554432: {
                return "TMSUSPEND";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    @Override
    public String toString() {
        String resString = null;
        synchronized (this.enlistedResources) {
            resString = this.enlistedResources.toString();
        }
        return "[DataNucleus Transaction, ID=" + this.xid.toString() + ", enlisted resources=" + resString + "]";
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        nodeId = NucleusContext.random.nextInt();
        Transaction.nextGlobalTransactionId = 1;
    }
}
