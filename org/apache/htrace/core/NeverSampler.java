// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

public final class NeverSampler extends Sampler
{
    public static final NeverSampler INSTANCE;
    
    public NeverSampler(final HTraceConfiguration conf) {
    }
    
    @Override
    public boolean next() {
        return false;
    }
    
    static {
        INSTANCE = new NeverSampler(null);
    }
}
