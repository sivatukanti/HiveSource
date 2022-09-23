// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.property;

public final class NullReturn
{
    public static final NullReturn NULL;
    
    private NullReturn() {
    }
    
    @Override
    public String toString() {
        return "null";
    }
    
    static {
        NULL = new NullReturn();
    }
}
