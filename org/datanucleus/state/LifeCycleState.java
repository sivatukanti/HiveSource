// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.ClassConstants;
import org.datanucleus.FetchPlan;
import org.datanucleus.Transaction;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.Localiser;

public abstract class LifeCycleState
{
    protected static final Localiser LOCALISER;
    public static final int TRANSIENT = 0;
    public static final int P_NEW = 1;
    public static final int P_CLEAN = 2;
    public static final int P_DIRTY = 3;
    public static final int HOLLOW = 4;
    public static final int T_CLEAN = 5;
    public static final int T_DIRTY = 6;
    public static final int P_NEW_DELETED = 7;
    public static final int P_DELETED = 8;
    public static final int P_NONTRANS = 9;
    public static final int P_NONTRANS_DIRTY = 10;
    public static final int DETACHED_CLEAN = 11;
    public static final int DETACHED_DIRTY = 12;
    public static final int TOTAL = 13;
    public static final int ILLEGAL_STATE = 13;
    protected boolean isDirty;
    protected boolean isNew;
    protected boolean isDeleted;
    protected boolean isTransactional;
    protected boolean isPersistent;
    protected int stateType;
    
    public final int stateType() {
        return this.stateType;
    }
    
    protected final LifeCycleState changeState(final ObjectProvider op, final int newStateType) {
        final LifeCycleState newState = op.getExecutionContext().getNucleusContext().getApiAdapter().getLifeCycleState(newStateType);
        if (NucleusLogger.LIFECYCLE.isDebugEnabled()) {
            NucleusLogger.LIFECYCLE.debug(LifeCycleState.LOCALISER.msg("027016", StringUtils.toJVMIDString(op.getObject()), IdentityUtils.getIdentityAsString(op.getExecutionContext().getApiAdapter(), op.getInternalObjectId()), this, newState));
        }
        if (this.isTransactional) {
            if (newState == null || !newState.isTransactional) {
                op.evictFromTransaction();
            }
        }
        else if (newState != null && newState.isTransactional) {
            op.enlistInTransaction();
        }
        if (newState == null) {
            op.disconnect();
        }
        return newState;
    }
    
    protected final LifeCycleState changeTransientState(final ObjectProvider op, final int newStateType) {
        final LifeCycleState newState = op.getExecutionContext().getNucleusContext().getApiAdapter().getLifeCycleState(newStateType);
        try {
            op.enlistInTransaction();
        }
        catch (Exception ex) {}
        return newState;
    }
    
    public LifeCycleState transitionMakePersistent(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionDeletePersistent(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionMakeTransactional(final ObjectProvider op, final boolean refreshFields) {
        return this;
    }
    
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        return this;
    }
    
    public LifeCycleState transitionBegin(final ObjectProvider op, final Transaction tx) {
        return this;
    }
    
    public LifeCycleState transitionCommit(final ObjectProvider op, final Transaction tx) {
        return this;
    }
    
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        return this;
    }
    
    public LifeCycleState transitionRefresh(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionEvict(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        return this;
    }
    
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final boolean fgOnly) {
        return this;
    }
    
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final FetchPlan fetchPlan) {
        return this;
    }
    
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionAttach(final ObjectProvider op) {
        return this;
    }
    
    public LifeCycleState transitionSerialize(final ObjectProvider op) {
        return this;
    }
    
    public final boolean isDirty() {
        return this.isDirty;
    }
    
    public final boolean isNew() {
        return this.isNew;
    }
    
    public final boolean isDeleted() {
        return this.isDeleted;
    }
    
    public final boolean isTransactional() {
        return this.isTransactional;
    }
    
    public final boolean isPersistent() {
        return this.isPersistent;
    }
    
    @Override
    public abstract String toString();
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
