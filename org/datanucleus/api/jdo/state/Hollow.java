// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.FetchPlan;
import org.datanucleus.api.jdo.exceptions.TransactionNotWritableException;
import org.datanucleus.Transaction;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.api.jdo.exceptions.TransactionNotReadableException;
import org.datanucleus.state.IllegalStateTransitionException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class Hollow extends LifeCycleState
{
    protected Hollow() {
        this.isPersistent = true;
        this.isDirty = false;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = false;
        this.stateType = 4;
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
    
    public LifeCycleState transitionCommit(final ObjectProvider op) {
        throw new IllegalStateTransitionException(this, "commit", op);
    }
    
    public LifeCycleState transitionRollback(final ObjectProvider op) {
        throw new IllegalStateTransitionException(this, "rollback", op);
    }
    
    @Override
    public LifeCycleState transitionReadField(final ObjectProvider op, final boolean isLoaded) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.isActive() && !tx.getNontransactionalRead()) {
            throw new TransactionNotReadableException(Hollow.LOCALISER.msg("027000"), op.getInternalObjectId());
        }
        if (!tx.isActive() && op.getClassMetaData().getIdentityType() == IdentityType.NONDURABLE) {
            throw new NucleusUserException("Not able to read fields of nondurable object when in HOLLOW state");
        }
        if (!tx.getOptimistic() && tx.isActive()) {
            return this.changeState(op, 2);
        }
        return this.changeState(op, 9);
    }
    
    @Override
    public LifeCycleState transitionWriteField(final ObjectProvider op) {
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.isActive() && !tx.getNontransactionalWrite()) {
            throw new TransactionNotWritableException(Hollow.LOCALISER.msg("027001"), op.getInternalObjectId());
        }
        return this.changeState(op, tx.isActive() ? 3 : 9);
    }
    
    @Override
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final boolean fgOnly) {
        if (fgOnly) {
            op.loadUnloadedFieldsInFetchPlan();
        }
        else {
            op.loadUnloadedFields();
        }
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.getOptimistic() && tx.isActive()) {
            return this.changeState(op, 2);
        }
        if (tx.getOptimistic()) {
            return this.changeState(op, 9);
        }
        return super.transitionRetrieve(op, fgOnly);
    }
    
    @Override
    public LifeCycleState transitionRetrieve(final ObjectProvider op, final FetchPlan fetchPlan) {
        op.loadUnloadedFieldsOfClassInFetchPlan(fetchPlan);
        final Transaction tx = op.getExecutionContext().getTransaction();
        if (!tx.getOptimistic() && tx.isActive()) {
            return this.changeState(op, 2);
        }
        if (tx.getOptimistic()) {
            return this.changeState(op, 9);
        }
        return super.transitionRetrieve(op, fetchPlan);
    }
    
    @Override
    public LifeCycleState transitionRefresh(final ObjectProvider op) {
        op.clearSavedFields();
        op.refreshFieldsInFetchPlan();
        op.unloadNonFetchPlanFields();
        return this;
    }
    
    @Override
    public LifeCycleState transitionDetach(final ObjectProvider op) {
        return this.changeState(op, 11);
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
    public String toString() {
        return "HOLLOW";
    }
}
