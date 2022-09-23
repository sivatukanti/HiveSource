// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.Transaction;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentDirty extends LifeCycleState
{
    protected PersistentDirty() {
        this.isPersistent = true;
        this.isDirty = true;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = true;
        this.stateType = 3;
    }
    
    @Override
    public LifeCycleState transitionDeletePersistent(final ObjectProvider op) {
        op.clearLoadedFlags();
        return this.changeState(op, 8);
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        throw new NucleusUserException(PersistentDirty.LOCALISER.msg("027011"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        if (detachAllOnCommit) {
            return this.changeState(op, 0);
        }
        throw new NucleusUserException(PersistentDirty.LOCALISER.msg("027012"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionCommit(final ObjectProvider op, final Transaction tx) {
        op.clearSavedFields();
        if (tx.getRetainValues()) {
            return this.changeState(op, 9);
        }
        op.clearNonPrimaryKeyFields();
        return this.changeState(op, 4);
    }
    
    @Override
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        if (tx.getRestoreValues()) {
            op.restoreFields();
            return this.changeState(op, 9);
        }
        op.clearNonPrimaryKeyFields();
        op.clearSavedFields();
        return this.changeState(op, 4);
    }
    
    @Override
    public LifeCycleState transitionRefresh(final ObjectProvider op) {
        op.clearSavedFields();
        op.refreshFieldsInFetchPlan();
        op.unloadNonFetchPlanFields();
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive() && !tx.getOptimistic()) {
            return this.changeState(op, 2);
        }
        return this.changeState(op, 9);
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
    }
    
    @Override
    public String toString() {
        return "P_DIRTY";
    }
}
