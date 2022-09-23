// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.FetchPlan;
import org.datanucleus.Transaction;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentClean extends LifeCycleState
{
    protected PersistentClean() {
        this.isPersistent = true;
        this.isDirty = false;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = true;
        this.stateType = 2;
    }
    
    @Override
    public LifeCycleState transitionDeletePersistent(final ObjectProvider op) {
        op.clearLoadedFlags();
        return this.changeState(op, 8);
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        op.clearSavedFields();
        return this.changeState(op, 9);
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        if (useFetchPlan) {
            op.loadUnloadedFieldsInFetchPlan();
        }
        return this.changeState(op, 0);
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
    public LifeCycleState transitionEvict(final ObjectProvider op) {
        op.clearNonPrimaryKeyFields();
        op.clearSavedFields();
        return this.changeState(op, 4);
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.getRestoreValues()) {
            op.saveFields();
        }
        return this.changeState(op, 3);
    }
    
    @Override
    public LifeCycleState transitionRefresh(final ObjectProvider op) {
        op.clearSavedFields();
        op.refreshFieldsInFetchPlan();
        op.unloadNonFetchPlanFields();
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive()) {
            return this.changeState(op, 2);
        }
        return this.changeState(op, 9);
    }
    
    @Override
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final boolean fgOnly) {
        if (fgOnly) {
            op.loadUnloadedFieldsInFetchPlan();
        }
        else {
            op.loadUnloadedFields();
        }
        return this;
    }
    
    @Override
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final FetchPlan fetchPlan) {
        op.loadUnloadedFieldsOfClassInFetchPlan(fetchPlan);
        return this;
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
    }
    
    @Override
    public String toString() {
        return "P_CLEAN";
    }
}
