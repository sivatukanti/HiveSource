// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

public final class AlwaysSampler extends Sampler
{
    public static final AlwaysSampler INSTANCE;
    
    public AlwaysSampler(final HTraceConfiguration conf) {
    }
    
    @Override
    public boolean next() {
        return true;
    }
    
    static {
        INSTANCE = new AlwaysSampler(null);
    }
}
