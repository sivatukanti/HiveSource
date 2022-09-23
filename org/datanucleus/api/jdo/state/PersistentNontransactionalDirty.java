// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import javax.jdo.JDOUserException;
import org.datanucleus.Transaction;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentNontransactionalDirty extends LifeCycleState
{
    protected PersistentNontransactionalDirty() {
        this.isPersistent = true;
        this.isDirty = true;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = false;
        this.stateType = 10;
    }
    
    @Override
    public LifeCycleState transitionMakeTransactional(final ObjectProvider op, final boolean refreshFields) {
        return this;
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
            return this.changeState(op, 10);
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
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.isActive() && !tx.getNontransactionalRead()) {
            throw new JDOUserException(PersistentNontransactionalDirty.LOCALISER.msg("027002"), op.getInternalObjectId());
        }
        return this;
    }
    
    @Override
    public LifeCycleState transitionBegin(final ObjectProvider op, final Transaction tx) {
        op.saveFields();
        op.enlistInTransaction();
        return this;
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        return this;
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
    }
    
    @Override
    public String toString() {
        return "P_NONTRANS_DIRTY";
    }
}
