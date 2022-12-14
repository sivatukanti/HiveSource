// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.collect;

import org.apache.curator.shaded.com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object>
{
    static final EmptyImmutableListMultimap INSTANCE;
    private static final long serialVersionUID = 0L;
    
    private EmptyImmutableListMultimap() {
        super(ImmutableMap.of(), 0);
    }
    
    private Object readResolve() {
        return EmptyImmutableListMultimap.INSTANCE;
    }
    
    static {
        INSTANCE = new EmptyImmutableListMultimap();
    }
}
