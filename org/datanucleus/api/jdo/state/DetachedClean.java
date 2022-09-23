// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class DetachedClean extends LifeCycleState
{
    protected DetachedClean() {
        this.isPersistent = false;
        this.isDirty = false;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = false;
        this.stateType = 11;
    }
    
    @Override
    public String toString() {
        return "DETACHED_CLEAN";
    }
    
    @Override
    public LifeCycleState transitionAttach(final ObjectProvider op) {
        return this.changeState(op, 2);
    }
}
