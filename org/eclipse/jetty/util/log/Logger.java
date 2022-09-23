// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

public interface Logger
{
    String getName();
    
    void warn(final String p0, final Object... p1);
    
    void warn(final Throwable p0);
    
    void warn(final String p0, final Throwable p1);
    
    void info(final String p0, final Object... p1);
    
    void info(final Throwable p0);
    
    void info(final String p0, final Throwable p1);
    
    boolean isDebugEnabled();
    
    void setDebugEnabled(final boolean p0);
    
    void debug(final String p0, final Object... p1);
    
    void debug(final String p0, final long p1);
    
    void debug(final Throwable p0);
    
    void debug(final String p0, final Throwable p1);
    
    Logger getLogger(final String p0);
    
    void ignore(final Throwable p0);
}
