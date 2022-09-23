// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

public abstract class ConstantCPInfo extends ConstantPoolEntry
{
    private Object value;
    
    protected ConstantCPInfo(final int tagValue, final int entries) {
        super(tagValue, entries);
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object newValue) {
        this.value = newValue;
    }
}
