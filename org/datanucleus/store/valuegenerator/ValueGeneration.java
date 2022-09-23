// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.io.Serializable;

class ValueGeneration implements Serializable
{
    private Object value;
    
    ValueGeneration(final Object val) {
        this.value = val;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object val) {
        this.value = val;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ValueGeneration && ((ValueGeneration)obj).getValue().equals(this.getValue());
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
