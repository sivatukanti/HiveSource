// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface Logger
{
    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    public static final int FATAL = 4;
    
    boolean isEnabled(final int p0);
    
    void log(final int p0, final String p1);
}
