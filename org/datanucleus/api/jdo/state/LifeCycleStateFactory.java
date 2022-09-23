// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.state;

import org.datanucleus.state.LifeCycleState;

public abstract class LifeCycleStateFactory
{
    private static LifeCycleState[] states;
    
    public static final LifeCycleState getLifeCycleState(final int stateType) {
        return LifeCycleStateFactory.states[stateType];
    }
    
    static {
        (LifeCycleStateFactory.states = new LifeCycleState[13])[4] = new Hollow();
        LifeCycleStateFactory.states[2] = new PersistentClean();
        LifeCycleStateFactory.states[3] = new PersistentDirty();
        LifeCycleStateFactory.states[1] = new PersistentNew();
        LifeCycleStateFactory.states[7] = new PersistentNewDeleted();
        LifeCycleStateFactory.states[8] = new PersistentDeleted();
        LifeCycleStateFactory.states[9] = new PersistentNontransactional();
        LifeCycleStateFactory.states[5] = new TransientClean();
        LifeCycleStateFactory.states[6] = new TransientDirty();
        LifeCycleStateFactory.states[10] = new PersistentNontransactionalDirty();
        LifeCycleStateFactory.states[11] = new DetachedClean();
        LifeCycleStateFactory.states[12] = new DetachedDirty();
        LifeCycleStateFactory.states[0] = null;
    }
}
