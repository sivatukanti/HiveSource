// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.Transaction;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class TransientClean extends LifeCycleState
{
    TransientClean() {
        this.isPersistent = false;
        this.isTransactional = true;
        this.isDirty = false;
        this.isNew = false;
        this.isDeleted = false;
        this.stateType = 5;
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        return this;
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        try {
            return this.changeTransientState(op, 0);
        }
        finally {
            op.disconnect();
        }
    }
    
    @Override
    public LifeCycleState transitionMakePersistent(final ObjectProvider op) {
        op.registerTransactional();
        return this.changeState(op, 1);
    }
    
    @Override
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        return this;
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive()) {
            op.saveFields();
            return this.changeTransientState(op, 6);
        }
        return this;
    }
    
    @Override
    public LifeCycleState transitionCommit(final ObjectProvider op, final Transaction tx) {
        return this;
    }
    
    @Override
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        return this;
    }
    
    @Override
    public String toString() {
        return "T_CLEAN";
    }
}
