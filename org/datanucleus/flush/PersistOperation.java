// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.state.ObjectProvider;

public class PersistOperation implements Operation
{
    ObjectProvider op;
    
    public PersistOperation(final ObjectProvider op) {
        this.op = op;
    }
    
    @Override
    public ObjectProvider getObjectProvider() {
        return this.op;
    }
    
    @Override
    public void perform() {
    }
    
    @Override
    public String toString() {
        return "PERSIST : " + this.op;
    }
}
