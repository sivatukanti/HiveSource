// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ServiceStateModel
{
    private static final boolean[][] statemap;
    private volatile Service.STATE state;
    private String name;
    
    public ServiceStateModel(final String name) {
        this(name, Service.STATE.NOTINITED);
    }
    
    public ServiceStateModel(final String name, final Service.STATE state) {
        this.state = state;
        this.name = name;
    }
    
    public Service.STATE getState() {
        return this.state;
    }
    
    public boolean isInState(final Service.STATE proposed) {
        return this.state.equals(proposed);
    }
    
    public void ensureCurrentState(final Service.STATE expectedState) {
        if (this.state != expectedState) {
            throw new ServiceStateException(this.name + ": for this operation, the current service state must be " + expectedState + " instead of " + this.state);
        }
    }
    
    public synchronized Service.STATE enterState(final Service.STATE proposed) {
        checkStateTransition(this.name, this.state, proposed);
        final Service.STATE oldState = this.state;
        this.state = proposed;
        return oldState;
    }
    
    public static void checkStateTransition(final String name, final Service.STATE state, final Service.STATE proposed) {
        if (!isValidStateTransition(state, proposed)) {
            throw new ServiceStateException(name + " cannot enter state " + proposed + " from state " + state);
        }
    }
    
    public static boolean isValidStateTransition(final Service.STATE current, final Service.STATE proposed) {
        final boolean[] row = ServiceStateModel.statemap[current.getValue()];
        return row[proposed.getValue()];
    }
    
    @Override
    public String toString() {
        return (this.name.isEmpty() ? "" : (this.name + ": ")) + this.state.toString();
    }
    
    static {
        statemap = new boolean[][] { { false, true, false, true }, { false, true, true, true }, { false, false, true, true }, { false, false, false, true } };
    }
}
