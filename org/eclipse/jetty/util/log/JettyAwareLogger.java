// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.Logger;

class JettyAwareLogger implements Logger
{
    private static final int DEBUG = 10;
    private static final int ERROR = 40;
    private static final int INFO = 20;
    private static final int TRACE = 0;
    private static final int WARN = 30;
    private static final String FQCN;
    private final LocationAwareLogger _logger;
    
    public JettyAwareLogger(final LocationAwareLogger logger) {
        this._logger = logger;
    }
    
    @Override
    public String getName() {
        return this._logger.getName();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return this._logger.isTraceEnabled();
    }
    
    @Override
    public void trace(final String msg) {
        this.log(null, 0, msg, null, null);
    }
    
    @Override
    public void trace(final String format, final Object arg) {
        this.log(null, 0, format, new Object[] { arg }, null);
    }
    
    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        this.log(null, 0, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void trace(final String format, final Object[] argArray) {
        this.log(null, 0, format, argArray, null);
    }
    
    @Override
    public void trace(final String msg, final Throwable t) {
        this.log(null, 0, msg, null, t);
    }
    
    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return this._logger.isTraceEnabled(marker);
    }
    
    @Override
    public void trace(final Marker marker, final String msg) {
        this.log(marker, 0, msg, null, null);
    }
    
    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        this.log(marker, 0, format, new Object[] { arg }, null);
    }
    
    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.log(marker, 0, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void trace(final Marker marker, final String format, final Object[] argArray) {
        this.log(marker, 0, format, argArray, null);
    }
    
    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        this.log(marker, 0, msg, null, t);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this._logger.isDebugEnabled();
    }
    
    @Override
    public void debug(final String msg) {
        this.log(null, 10, msg, null, null);
    }
    
    @Override
    public void debug(final String format, final Object arg) {
        this.log(null, 10, format, new Object[] { arg }, null);
    }
    
    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        this.log(null, 10, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void debug(final String format, final Object[] argArray) {
        this.log(null, 10, format, argArray, null);
    }
    
    @Override
    public void debug(final String msg, final Throwable t) {
        this.log(null, 10, msg, null, t);
    }
    
    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return this._logger.isDebugEnabled(marker);
    }
    
    @Override
    public void debug(final Marker marker, final String msg) {
        this.log(marker, 10, msg, null, null);
    }
    
    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        this.log(marker, 10, format, new Object[] { arg }, null);
    }
    
    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.log(marker, 10, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void debug(final Marker marker, final String format, final Object[] argArray) {
        this.log(marker, 10, format, argArray, null);
    }
    
    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        this.log(marker, 10, msg, null, t);
    }
    
    @Override
    public boolean isInfoEnabled() {
        return this._logger.isInfoEnabled();
    }
    
    @Override
    public void info(final String msg) {
        this.log(null, 20, msg, null, null);
    }
    
    @Override
    public void info(final String format, final Object arg) {
        this.log(null, 20, format, new Object[] { arg }, null);
    }
    
    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        this.log(null, 20, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void info(final String format, final Object[] argArray) {
        this.log(null, 20, format, argArray, null);
    }
    
    @Override
    public void info(final String msg, final Throwable t) {
        this.log(null, 20, msg, null, t);
    }
    
    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return this._logger.isInfoEnabled(marker);
    }
    
    @Override
    public void info(final Marker marker, final String msg) {
        this.log(marker, 20, msg, null, null);
    }
    
    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        this.log(marker, 20, format, new Object[] { arg }, null);
    }
    
    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.log(marker, 20, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void info(final Marker marker, final String format, final Object[] argArray) {
        this.log(marker, 20, format, argArray, null);
    }
    
    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        this.log(marker, 20, msg, null, t);
    }
    
    @Override
    public boolean isWarnEnabled() {
        return this._logger.isWarnEnabled();
    }
    
    @Override
    public void warn(final String msg) {
        this.log(null, 30, msg, null, null);
    }
    
    @Override
    public void warn(final String format, final Object arg) {
        this.log(null, 30, format, new Object[] { arg }, null);
    }
    
    @Override
    public void warn(final String format, final Object[] argArray) {
        this.log(null, 30, format, argArray, null);
    }
    
    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        this.log(null, 30, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void warn(final String msg, final Throwable t) {
        this.log(null, 30, msg, null, t);
    }
    
    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return this._logger.isWarnEnabled(marker);
    }
    
    @Override
    public void warn(final Marker marker, final String msg) {
        this.log(marker, 30, msg, null, null);
    }
    
    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        this.log(marker, 30, format, new Object[] { arg }, null);
    }
    
    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.log(marker, 30, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void warn(final Marker marker, final String format, final Object[] argArray) {
        this.log(marker, 30, format, argArray, null);
    }
    
    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        this.log(marker, 30, msg, null, t);
    }
    
    @Override
    public boolean isErrorEnabled() {
        return this._logger.isErrorEnabled();
    }
    
    @Override
    public void error(final String msg) {
        this.log(null, 40, msg, null, null);
    }
    
    @Override
    public void error(final String format, final Object arg) {
        this.log(null, 40, format, new Object[] { arg }, null);
    }
    
    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        this.log(null, 40, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void error(final String format, final Object[] argArray) {
        this.log(null, 40, format, argArray, null);
    }
    
    @Override
    public void error(final String msg, final Throwable t) {
        this.log(null, 40, msg, null, t);
    }
    
    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return this._logger.isErrorEnabled(marker);
    }
    
    @Override
    public void error(final Marker marker, final String msg) {
        this.log(marker, 40, msg, null, null);
    }
    
    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        this.log(marker, 40, format, new Object[] { arg }, null);
    }
    
    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.log(marker, 40, format, new Object[] { arg1, arg2 }, null);
    }
    
    @Override
    public void error(final Marker marker, final String format, final Object[] argArray) {
        this.log(marker, 40, format, argArray, null);
    }
    
    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        this.log(marker, 40, msg, null, t);
    }
    
    @Override
    public String toString() {
        return this._logger.toString();
    }
    
    private void log(final Marker marker, final int level, final String msg, final Object[] argArray, final Throwable t) {
        if (argArray == null) {
            this._logger.log(marker, JettyAwareLogger.FQCN, level, msg, null, t);
        }
        else {
            final int loggerLevel = this._logger.isTraceEnabled() ? 0 : (this._logger.isDebugEnabled() ? 10 : (this._logger.isInfoEnabled() ? 20 : (this._logger.isWarnEnabled() ? 30 : 40)));
            if (loggerLevel <= level) {
                final FormattingTuple ft = MessageFormatter.arrayFormat(msg, argArray);
                this._logger.log(marker, JettyAwareLogger.FQCN, level, ft.getMessage(), null, t);
            }
        }
    }
    
    static {
        FQCN = Slf4jLog.class.getName();
    }
}
