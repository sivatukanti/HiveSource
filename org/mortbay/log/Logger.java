// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.log;

public interface Logger
{
    boolean isDebugEnabled();
    
    void setDebugEnabled(final boolean p0);
    
    void info(final String p0, final Object p1, final Object p2);
    
    void debug(final String p0, final Throwable p1);
    
    void debug(final String p0, final Object p1, final Object p2);
    
    void warn(final String p0, final Object p1, final Object p2);
    
    void warn(final String p0, final Throwable p1);
    
    Logger getLogger(final String p0);
}
