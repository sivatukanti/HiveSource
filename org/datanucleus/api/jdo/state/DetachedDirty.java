// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.state.ObjectProvider;
import org.datanucleus.state.LifeCycleState;

class DetachedDirty extends LifeCycleState
{
    protected DetachedDirty() {
        this.isPersistent = false;
        this.isDirty = true;
        this.isNew = false;
        this.isDeleted = false;
        this.isTransactional = false;
        this.stateType = 12;
    }
    
    @Override
    public String toString() {
        return "DETACHED_DIRTY";
    }
    
    @Override
    public LifeCycleState transitionAttach(final ObjectProvider op) {
        return this.changeState(op, 3);
    }
}
