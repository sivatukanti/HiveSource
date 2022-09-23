// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.Transaction;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class TransientDirty extends LifeCycleState
{
    TransientDirty() {
        this.isPersistent = false;
        this.isTransactional = true;
        this.isDirty = true;
        this.isNew = false;
        this.isDeleted = false;
        this.stateType = 6;
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        return this;
    }
    
    @Override
    public LifeCycleState transitionMakePersistent(final ObjectProvider op) {
        op.registerTransactional();
        return this.changeState(op, 1);
    }
    
    @Override
    public LifeCycleState transitionCommit(final ObjectProvider op, final Transaction tx) {
        op.clearSavedFields();
        return this.changeTransientState(op, 5);
    }
    
    @Override
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        if (tx.getRestoreValues() || op.isRestoreValues()) {
            op.restoreFields();
        }
        return this.changeTransientState(op, 5);
    }
    
    @Override
    public String toString() {
        return "T_DIRTY";
    }
}
