// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

public class LogLevel extends EnumeratedAttribute
{
    public static final LogLevel ERR;
    public static final LogLevel WARN;
    public static final LogLevel INFO;
    public static final LogLevel VERBOSE;
    public static final LogLevel DEBUG;
    private static int[] levels;
    
    public LogLevel() {
    }
    
    private LogLevel(final String value) {
        this();
        this.setValue(value);
    }
    
    @Override
    public String[] getValues() {
        return new String[] { "error", "warn", "warning", "info", "verbose", "debug" };
    }
    
    public int getLevel() {
        return LogLevel.levels[this.getIndex()];
    }
    
    static {
        ERR = new LogLevel("error");
        WARN = new LogLevel("warn");
        INFO = new LogLevel("info");
        VERBOSE = new LogLevel("verbose");
        DEBUG = new LogLevel("debug");
        LogLevel.levels = new int[] { 0, 1, 1, 2, 3, 4 };
    }
}
