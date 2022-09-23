// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.io.IOException;

public final class ExceptionUtil
{
    private ExceptionUtil() {
    }
    
    public static void throwRuntimeException(final Throwable t) {
        throwIfUnchecked(t);
        throw new RuntimeException("[was " + t.getClass() + "] " + t.getMessage(), t);
    }
    
    public static IOException constructIOException(final String msg, final Throwable src) {
        final IOException e = new IOException(msg);
        setInitCause(e, src);
        return e;
    }
    
    public static void throwAsIllegalArgument(final Throwable t) {
        throwIfUnchecked(t);
        final IllegalArgumentException rex = new IllegalArgumentException("[was " + t.getClass() + "] " + t.getMessage());
        setInitCause(rex, t);
        throw rex;
    }
    
    public static void throwIfUnchecked(final Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
    }
    
    public static void throwGenericInternal() {
        throwInternal(null);
    }
    
    public static void throwInternal(String msg) {
        if (msg == null) {
            msg = "[no description]";
        }
        throw new RuntimeException("Internal error: " + msg);
    }
    
    public static void setInitCause(final Throwable newT, final Throwable rootT) {
        if (newT.getCause() == null) {
            newT.initCause(rootT);
        }
    }
}
