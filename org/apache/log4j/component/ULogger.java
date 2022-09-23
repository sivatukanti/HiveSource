// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component;

public interface ULogger
{
    boolean isDebugEnabled();
    
    void debug(final Object p0);
    
    void debug(final Object p0, final Object p1);
    
    void debug(final String p0, final Object p1, final Object p2);
    
    void debug(final Object p0, final Throwable p1);
    
    boolean isInfoEnabled();
    
    void info(final Object p0);
    
    void info(final Object p0, final Object p1);
    
    void info(final String p0, final Object p1, final Object p2);
    
    void info(final Object p0, final Throwable p1);
    
    boolean isWarnEnabled();
    
    void warn(final Object p0);
    
    void warn(final Object p0, final Object p1);
    
    void warn(final String p0, final Object p1, final Object p2);
    
    void warn(final Object p0, final Throwable p1);
    
    boolean isErrorEnabled();
    
    void error(final Object p0);
    
    void error(final Object p0, final Object p1);
    
    void error(final String p0, final Object p1, final Object p2);
    
    void error(final Object p0, final Throwable p1);
}
