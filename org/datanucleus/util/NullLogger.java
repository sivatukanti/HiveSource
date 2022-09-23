// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

public class NullLogger extends NucleusLogger
{
    public NullLogger(final String logName) {
    }
    
    @Override
    public void debug(final Object msg) {
    }
    
    @Override
    public void debug(final Object msg, final Throwable thr) {
    }
    
    @Override
    public void error(final Object msg) {
    }
    
    @Override
    public void error(final Object msg, final Throwable thr) {
    }
    
    @Override
    public void fatal(final Object msg) {
    }
    
    @Override
    public void fatal(final Object msg, final Throwable thr) {
    }
    
    @Override
    public void info(final Object msg) {
    }
    
    @Override
    public void info(final Object msg, final Throwable thr) {
    }
    
    @Override
    public boolean isDebugEnabled() {
        return false;
    }
    
    @Override
    public boolean isInfoEnabled() {
        return false;
    }
    
    @Override
    public void warn(final Object msg) {
    }
    
    @Override
    public void warn(final Object msg, final Throwable thr) {
    }
}
