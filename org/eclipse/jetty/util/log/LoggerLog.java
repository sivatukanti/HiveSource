// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.lang.reflect.Method;

public class LoggerLog extends AbstractLogger
{
    private final Object _logger;
    private final Method _debugMT;
    private final Method _debugMAA;
    private final Method _infoMT;
    private final Method _infoMAA;
    private final Method _warnMT;
    private final Method _warnMAA;
    private final Method _setDebugEnabledE;
    private final Method _getLoggerN;
    private final Method _getName;
    private volatile boolean _debug;
    
    public LoggerLog(final Object logger) {
        try {
            this._logger = logger;
            final Class<?> lc = logger.getClass();
            this._debugMT = lc.getMethod("debug", String.class, Throwable.class);
            this._debugMAA = lc.getMethod("debug", String.class, Object[].class);
            this._infoMT = lc.getMethod("info", String.class, Throwable.class);
            this._infoMAA = lc.getMethod("info", String.class, Object[].class);
            this._warnMT = lc.getMethod("warn", String.class, Throwable.class);
            this._warnMAA = lc.getMethod("warn", String.class, Object[].class);
            final Method _isDebugEnabled = lc.getMethod("isDebugEnabled", (Class<?>[])new Class[0]);
            this._setDebugEnabledE = lc.getMethod("setDebugEnabled", Boolean.TYPE);
            this._getLoggerN = lc.getMethod("getLogger", String.class);
            this._getName = lc.getMethod("getName", (Class<?>[])new Class[0]);
            this._debug = (boolean)_isDebugEnabled.invoke(this._logger, new Object[0]);
        }
        catch (Exception x) {
            throw new IllegalStateException(x);
        }
    }
    
    @Override
    public String getName() {
        try {
            return (String)this._getName.invoke(this._logger, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void warn(final String msg, final Object... args) {
        try {
            this._warnMAA.invoke(this._logger, args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void warn(final Throwable thrown) {
        this.warn("", thrown);
    }
    
    @Override
    public void warn(final String msg, final Throwable thrown) {
        try {
            this._warnMT.invoke(this._logger, msg, thrown);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void info(final String msg, final Object... args) {
        try {
            this._infoMAA.invoke(this._logger, args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void info(final Throwable thrown) {
        this.info("", thrown);
    }
    
    @Override
    public void info(final String msg, final Throwable thrown) {
        try {
            this._infoMT.invoke(this._logger, msg, thrown);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this._debug;
    }
    
    @Override
    public void setDebugEnabled(final boolean enabled) {
        try {
            this._setDebugEnabledE.invoke(this._logger, enabled);
            this._debug = enabled;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void debug(final String msg, final Object... args) {
        if (!this._debug) {
            return;
        }
        try {
            this._debugMAA.invoke(this._logger, args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void debug(final Throwable thrown) {
        this.debug("", thrown);
    }
    
    @Override
    public void debug(final String msg, final Throwable th) {
        if (!this._debug) {
            return;
        }
        try {
            this._debugMT.invoke(this._logger, msg, th);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void debug(final String msg, final long value) {
        if (!this._debug) {
            return;
        }
        try {
            this._debugMAA.invoke(this._logger, new Long(value));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void ignore(final Throwable ignored) {
        if (Log.isIgnored()) {
            this.debug("IGNORED EXCEPTION ", ignored);
        }
    }
    
    @Override
    protected Logger newLogger(final String fullname) {
        try {
            final Object logger = this._getLoggerN.invoke(this._logger, fullname);
            return new LoggerLog(logger);
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
}
