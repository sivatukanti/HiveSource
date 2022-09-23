// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;

class Exceptions
{
    public static RuntimeException throwCleanly(final InvocationTargetException exception) {
        Throwable cause = exception;
        if (cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        if (cause instanceof Error) {
            throw (Error)cause;
        }
        throw new UnhandledCheckedUserException(cause);
    }
    
    static class UnhandledCheckedUserException extends RuntimeException
    {
        public UnhandledCheckedUserException(final Throwable cause) {
            super(cause);
        }
    }
}
