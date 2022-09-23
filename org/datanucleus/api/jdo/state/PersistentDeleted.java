// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.Transaction;
import javax.jdo.JDOUserException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentDeleted extends LifeCycleState
{
    protected PersistentDeleted() {
        this.isPersistent = true;
        this.isDirty = true;
        this.isNew = false;
        this.isDeleted = true;
        this.isTransactional = true;
        this.stateType = 8;
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        throw new JDOUserException(PersistentDeleted.LOCALISER.msg("027007"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        throw new JDOUserException(PersistentDeleted.LOCALISER.msg("027008"), op.getInternalObjectId());
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
        if (tx.getRetainValues()) {
            if (tx.getRestoreValues()) {
                op.restoreFields();
            }
            return this.changeState(op, 9);
        }
        op.clearNonPrimaryKeyFields();
        op.clearSavedFields();
        return this.changeState(op, 4);
    }
    
    @Override
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        throw new JDOUserException(PersistentDeleted.LOCALISER.msg("027009"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        throw new JDOUserException(PersistentDeleted.LOCALISER.msg("027010"), op.getInternalObjectId());
    }
    
    @Override
    public String toString() {
        return "P_DELETED";
    }
}
