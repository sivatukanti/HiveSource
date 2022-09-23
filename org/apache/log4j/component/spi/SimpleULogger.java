// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.component.helpers.MessageFormatter;
import org.apache.log4j.component.ULogger;

public final class SimpleULogger implements ULogger
{
    private final String loggerName;
    private static long startTime;
    public static final String LINE_SEPARATOR;
    private static final String INFO_STR = "INFO";
    private static final String WARN_STR = "WARN";
    private static final String ERROR_STR = "ERROR";
    
    private SimpleULogger(final String name) {
        this.loggerName = name;
    }
    
    public static SimpleULogger getLogger(final String name) {
        return new SimpleULogger(name);
    }
    
    public boolean isDebugEnabled() {
        return false;
    }
    
    public void debug(final Object msg) {
    }
    
    public void debug(final Object parameterizedMsg, final Object param1) {
    }
    
    public void debug(final String parameterizedMsg, final Object param1, final Object param2) {
    }
    
    public void debug(final Object msg, final Throwable t) {
    }
    
    private void log(final String level, final String message, final Throwable t) {
        final StringBuffer buf = new StringBuffer();
        final long millis = System.currentTimeMillis();
        buf.append(millis - SimpleULogger.startTime);
        buf.append(" [");
        buf.append(Thread.currentThread().getName());
        buf.append("] ");
        buf.append(level);
        buf.append(" ");
        buf.append(this.loggerName);
        buf.append(" - ");
        buf.append(message);
        buf.append(SimpleULogger.LINE_SEPARATOR);
        System.out.print(buf.toString());
        if (t != null) {
            t.printStackTrace(System.out);
        }
        System.out.flush();
    }
    
    private void parameterizedLog(final String level, final Object parameterizedMsg, final Object param1, final Object param2) {
        if (parameterizedMsg instanceof String) {
            String msgStr = (String)parameterizedMsg;
            msgStr = MessageFormatter.format(msgStr, param1, param2);
            this.log(level, msgStr, null);
        }
        else {
            this.log(level, parameterizedMsg.toString(), null);
        }
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
    
    public void info(final Object msg) {
        this.log("INFO", msg.toString(), null);
    }
    
    public void info(final Object parameterizedMsg, final Object param1) {
        this.parameterizedLog("INFO", parameterizedMsg, param1, null);
    }
    
    public void info(final String parameterizedMsg, final Object param1, final Object param2) {
        this.parameterizedLog("INFO", parameterizedMsg, param1, param2);
    }
    
    public void info(final Object msg, final Throwable t) {
        this.log("INFO", msg.toString(), t);
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public void warn(final Object msg) {
        this.log("WARN", msg.toString(), null);
    }
    
    public void warn(final Object parameterizedMsg, final Object param1) {
        this.parameterizedLog("WARN", parameterizedMsg, param1, null);
    }
    
    public void warn(final String parameterizedMsg, final Object param1, final Object param2) {
        this.parameterizedLog("WARN", parameterizedMsg, param1, param2);
    }
    
    public void warn(final Object msg, final Throwable t) {
        this.log("WARN", msg.toString(), t);
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    public void error(final Object msg) {
        this.log("ERROR", msg.toString(), null);
    }
    
    public void error(final Object parameterizedMsg, final Object param1) {
        this.parameterizedLog("ERROR", parameterizedMsg, param1, null);
    }
    
    public void error(final String parameterizedMsg, final Object param1, final Object param2) {
        this.parameterizedLog("ERROR", parameterizedMsg, param1, param2);
    }
    
    public void error(final Object msg, final Throwable t) {
        this.log("ERROR", msg.toString(), t);
    }
    
    static {
        SimpleULogger.startTime = System.currentTimeMillis();
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
