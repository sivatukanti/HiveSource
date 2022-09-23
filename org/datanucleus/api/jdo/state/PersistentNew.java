// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.Transaction;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentNew extends LifeCycleState
{
    protected PersistentNew() {
        this.isPersistent = true;
        this.isDirty = true;
        this.isNew = true;
        this.isDeleted = false;
        this.isTransactional = true;
        this.stateType = 1;
    }
    
    @Override
    public LifeCycleState transitionDeletePersistent(final ObjectProvider op) {
        op.clearLoadedFlags();
        return this.changeState(op, 7);
    }
    
    @Override
    public LifeCycleState transitionMakeNontransactional(final ObjectProvider op) {
        throw new NucleusUserException(PersistentNew.LOCALISER.msg("027013"), op.getInternalObjectId());
    }
    
    @Override
    public LifeCycleState transitionMakeTransient(final ObjectProvider op, final boolean useFetchPlan, final boolean detachAllOnCommit) {
        if (detachAllOnCommit) {
            return this.changeState(op, 0);
        }
        throw new NucleusUserException(PersistentNew.LOCALISER.msg("027014"), op.getInternalObjectId());
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
        }
        return this.changeState(op, 0);
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
    }
    
    @Override
    public String toString() {
        return "P_NEW";
    }
}
