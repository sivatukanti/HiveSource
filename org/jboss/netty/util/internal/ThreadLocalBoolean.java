// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

public class ThreadLocalBoolean extends ThreadLocal<Boolean>
{
    private final boolean defaultValue;
    
    public ThreadLocalBoolean() {
        this(false);
    }
    
    public ThreadLocalBoolean(final boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    @Override
    protected Boolean initialValue() {
        return this.defaultValue ? Boolean.TRUE : Boolean.FALSE;
    }
}
