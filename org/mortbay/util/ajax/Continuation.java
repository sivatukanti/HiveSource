// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

public interface Continuation
{
    boolean suspend(final long p0);
    
    void resume();
    
    void reset();
    
    boolean isNew();
    
    boolean isPending();
    
    boolean isResumed();
    
    Object getObject();
    
    void setObject(final Object p0);
}
