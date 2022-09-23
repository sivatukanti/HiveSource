// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.state.ObjectProvider;

public class DeleteOperation implements Operation
{
    ObjectProvider op;
    
    public DeleteOperation(final ObjectProvider op) {
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
        return "DELETE : " + this.op;
    }
}
