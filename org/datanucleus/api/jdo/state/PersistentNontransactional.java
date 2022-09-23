// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.FetchPlan;
import org.datanucleus.api.jdo.exceptions.TransactionNotWritableException;
import org.datanucleus.api.jdo.exceptions.TransactionNotReadableException;
import org.datanucleus.state.IllegalStateTransitionException;
import org.datanucleus.Transaction;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class PersistentNontransactional extends LifeCycleState
{
    protected PersistentNontransactional() {
        this.isPersistent = true;
        this.isDirty = false;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = false;
        this.stateType = 9;
    }
    
    @Override
    public LifeCycleState transitionDeletePersistent(final ObjectProvider op) {
        op.clearLoadedFlags();
        return this.changeState(op, 8);
    }
    
    @Override
    public LifeCycleState transitionMakeTransactional(final ObjectProvider op, final boolean refreshFields) {
        if (refreshFields) {
            op.refreshLoadedFields();
        }
        return this.changeState(op, 2);
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
        throw new IllegalStateTransitionException(this, "commit", op);
    }
    
    @Override
    public LifeCycleState transitionRollback(final ObjectProvider op, final Transaction tx) {
        throw new IllegalStateTransitionException(this, "rollback", op);
    }
    
    @Override
    public LifeCycleState transitionRefresh(final ObjectProvider op) {
        op.refreshFieldsInFetchPlan();
        op.unloadNonFetchPlanFields();
        return this;
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
            throw new TransactionNotReadableException(PersistentNontransactional.LOCALISER.msg("027002"), op.getInternalObjectId());
        }
        if (tx.isActive() && !tx.getOptimistic()) {
            op.saveFields();
            op.refreshLoadedFields();
            return this.changeState(op, 2);
        }
        return this;
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.isActive() && !tx.getNontransactionalWrite()) {
            throw new TransactionNotWritableException(PersistentNontransactional.LOCALISER.msg("027001"), op.getInternalObjectId());
        }
        if (tx.isActive()) {
            op.saveFields();
            return this.changeState(op, 3);
        }
        op.saveFields();
        return this.changeState(op, 10);
    }
    
    @Override
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final boolean fgOnly) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive() && !tx.getOptimistic()) {
            op.saveFields();
            if (fgOnly) {
                op.loadUnloadedFieldsInFetchPlan();
            }
            else {
                op.loadUnloadedFields();
            }
            return this.changeState(op, 2);
        }
        if (tx.isActive() && tx.getOptimistic()) {
            op.saveFields();
            if (fgOnly) {
                op.loadUnloadedFieldsInFetchPlan();
            }
            else {
                op.loadUnloadedFields();
            }
            return this;
        }
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
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive() && !tx.getOptimistic()) {
            op.saveFields();
            op.loadUnloadedFieldsOfClassInFetchPlan(fetchPlan);
            return this.changeState(op, 2);
        }
        if (tx.isActive() && tx.getOptimistic()) {
            op.saveFields();
            op.loadUnloadedFieldsOfClassInFetchPlan(fetchPlan);
            return this;
        }
        op.loadUnloadedFieldsOfClassInFetchPlan(fetchPlan);
        return this;
    }
    
    @Override
    public LifeCycleState transitionSerialize(final ObjectProvider op) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (tx.isActive() && !tx.getOptimistic()) {
            return this.changeState(op, 2);
        }
        return this;
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
    }
    
    @Override
    public String toString() {
        return "P_NONTRANS";
    }
}
