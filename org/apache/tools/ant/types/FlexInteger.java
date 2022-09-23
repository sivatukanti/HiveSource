// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

public class FlexInteger
{
    private Integer value;
    
    public FlexInteger(final String value) {
        this.value = Integer.decode(value);
    }
    
    public int intValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
