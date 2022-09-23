// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class InterruptStatus
{
    public static final int MAX_INTERRUPT_RETRIES = 120;
    public static final int INTERRUPT_RETRY_SLEEP = 500;
    private static final ThreadLocal exception;
    
    public static void setInterrupted() {
        LanguageConnectionContext languageConnectionContext = null;
        try {
            languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        }
        catch (ShutdownException ex) {}
        Thread.interrupted();
        final StandardException exception = StandardException.newException("08000");
        if (languageConnectionContext != null) {
            languageConnectionContext.setInterruptedException(exception);
        }
        else {
            InterruptStatus.exception.set(exception);
        }
    }
    
    public static void saveInfoFromLcc(final LanguageConnectionContext languageConnectionContext) {
        final StandardException interruptedException = languageConnectionContext.getInterruptedException();
        if (interruptedException != null) {
            InterruptStatus.exception.set(interruptedException);
        }
    }
    
    public static boolean noteAndClearInterrupt(final String s, final int n, final int n2) {
        if (Thread.currentThread().isInterrupted()) {
            setInterrupted();
            Thread.interrupted();
            return true;
        }
        return false;
    }
    
    public static void restoreIntrFlagIfSeen() {
        LanguageConnectionContext languageConnectionContext = null;
        try {
            languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        }
        catch (ShutdownException ex) {}
        if (languageConnectionContext == null) {
            if (InterruptStatus.exception.get() != null) {
                InterruptStatus.exception.set(null);
                Thread.currentThread().interrupt();
            }
        }
        else if (languageConnectionContext.getInterruptedException() != null) {
            languageConnectionContext.setInterruptedException(null);
            Thread.currentThread().interrupt();
        }
    }
    
    public static void restoreIntrFlagIfSeen(final LanguageConnectionContext languageConnectionContext) {
        if (languageConnectionContext.getInterruptedException() != null) {
            languageConnectionContext.setInterruptedException(null);
            Thread.currentThread().interrupt();
        }
    }
    
    public static void throwIf(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (Thread.currentThread().isInterrupted()) {
            setInterrupted();
        }
        final StandardException interruptedException = languageConnectionContext.getInterruptedException();
        if (interruptedException != null) {
            languageConnectionContext.setInterruptedException(null);
            throw interruptedException;
        }
    }
    
    static {
        exception = new ThreadLocal();
    }
}
