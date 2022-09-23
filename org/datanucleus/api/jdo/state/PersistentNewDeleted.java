// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import javax.jdo.JDOUserException;
import org.datanucleus.Transaction;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentNewDeleted extends LifeCycleState
{
    protected PersistentNewDeleted() {
        this.isPersistent = true;
        this.isDirty = true;
        this.isNew = true;
        this.isDeleted = true;
        this.isTransactional = true;
        this.stateType = 7;
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        throw new NucleusUserException(PersistentNewDeleted.LOCALISER.msg("027003"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        throw new NucleusUserException(PersistentNewDeleted.LOCALISER.msg("027004"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionCommit(final ObjectProvider op, final Transaction tx) {
        if (!tx.getRetainValues()) {
            op.clearFields();
        }
        return this.changeState(op, 0);
    }
    
    @Override
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        if (tx.getRestoreValues()) {
            op.restoreFields();
        }
        return this.changeState(op, 0);
    }
    
    @Override
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        throw new JDOUserException(PersistentNewDeleted.LOCALISER.msg("027005"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        throw new JDOUserException(PersistentNewDeleted.LOCALISER.msg("027006"), op.getInternalObjectId());
    }
    
    @Override
    public String toString() {
        return "P_NEW_DELETED";
    }
}
