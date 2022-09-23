// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

public interface InternalLogger
{
    boolean isDebugEnabled();
    
    boolean isInfoEnabled();
    
    boolean isWarnEnabled();
    
    boolean isErrorEnabled();
    
    boolean isEnabled(final InternalLogLevel p0);
    
    void debug(final String p0);
    
    void debug(final String p0, final Throwable p1);
    
    void info(final String p0);
    
    void info(final String p0, final Throwable p1);
    
    void warn(final String p0);
    
    void warn(final String p0, final Throwable p1);
    
    void error(final String p0);
    
    void error(final String p0, final Throwable p1);
    
    void log(final InternalLogLevel p0, final String p1);
    
    void log(final InternalLogLevel p0, final String p1, final Throwable p2);
}
