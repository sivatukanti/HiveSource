// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.io.PrintWriter;
import java.io.PrintStream;
import javax.jdo.spi.I18NHelper;

public class JDOException extends RuntimeException
{
    Throwable[] nested;
    Object failed;
    private static I18NHelper msg;
    private boolean inPrintStackTrace;
    
    public JDOException() {
        this.inPrintStackTrace = false;
    }
    
    public JDOException(final String msg) {
        super(msg);
        this.inPrintStackTrace = false;
    }
    
    public JDOException(final String msg, final Throwable[] nested) {
        super(msg);
        this.inPrintStackTrace = false;
        this.nested = nested;
    }
    
    public JDOException(final String msg, final Throwable nested) {
        super(msg);
        this.inPrintStackTrace = false;
        this.nested = new Throwable[] { nested };
    }
    
    public JDOException(final String msg, final Object failed) {
        super(msg);
        this.inPrintStackTrace = false;
        this.failed = failed;
    }
    
    public JDOException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg);
        this.inPrintStackTrace = false;
        this.nested = nested;
        this.failed = failed;
    }
    
    public JDOException(final String msg, final Throwable nested, final Object failed) {
        super(msg);
        this.inPrintStackTrace = false;
        this.nested = new Throwable[] { nested };
        this.failed = failed;
    }
    
    public Object getFailedObject() {
        return this.failed;
    }
    
    public Throwable[] getNestedExceptions() {
        return this.nested;
    }
    
    @Override
    public synchronized Throwable getCause() {
        if (this.nested == null || this.nested.length == 0 || this.inPrintStackTrace) {
            return null;
        }
        return this.nested[0];
    }
    
    @Override
    public Throwable initCause(final Throwable cause) {
        throw new JDOFatalInternalException(JDOException.msg.msg("ERR_CannotInitCause"));
    }
    
    @Override
    public synchronized String toString() {
        final int len = (this.nested == null) ? 0 : this.nested.length;
        final StringBuffer sb = new StringBuffer(10 + 100 * len);
        sb.append(super.toString());
        if (this.failed != null) {
            sb.append("\n").append(JDOException.msg.msg("MSG_FailedObject"));
            String failedToString = null;
            try {
                failedToString = this.failed.toString();
            }
            catch (Exception ex) {
                final Object objectId = JDOHelper.getObjectId(this.failed);
                if (objectId == null) {
                    failedToString = JDOException.msg.msg("MSG_ExceptionGettingFailedToString", exceptionToString(ex));
                }
                else {
                    String objectIdToString = null;
                    try {
                        objectIdToString = objectId.toString();
                    }
                    catch (Exception ex2) {
                        objectIdToString = exceptionToString(ex2);
                    }
                    failedToString = JDOException.msg.msg("MSG_ExceptionGettingFailedToStringObjectId", exceptionToString(ex), objectIdToString);
                }
            }
            sb.append(failedToString);
        }
        if (len > 0 && !this.inPrintStackTrace) {
            sb.append("\n").append(JDOException.msg.msg("MSG_NestedThrowables")).append("\n");
            Throwable exception = this.nested[0];
            sb.append((exception == null) ? "null" : exception.toString());
            for (int i = 1; i < len; ++i) {
                sb.append("\n");
                exception = this.nested[i];
                sb.append((exception == null) ? "null" : exception.toString());
            }
        }
        return sb.toString();
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    @Override
    public synchronized void printStackTrace(final PrintStream s) {
        final int len = (this.nested == null) ? 0 : this.nested.length;
        synchronized (s) {
            this.inPrintStackTrace = true;
            super.printStackTrace(s);
            if (len > 0) {
                s.println(JDOException.msg.msg("MSG_NestedThrowablesStackTrace"));
                for (int i = 0; i < len; ++i) {
                    final Throwable exception = this.nested[i];
                    if (exception != null) {
                        exception.printStackTrace(s);
                    }
                }
            }
            this.inPrintStackTrace = false;
        }
    }
    
    @Override
    public synchronized void printStackTrace(final PrintWriter s) {
        final int len = (this.nested == null) ? 0 : this.nested.length;
        synchronized (s) {
            this.inPrintStackTrace = true;
            super.printStackTrace(s);
            if (len > 0) {
                s.println(JDOException.msg.msg("MSG_NestedThrowablesStackTrace"));
                for (int i = 0; i < len; ++i) {
                    final Throwable exception = this.nested[i];
                    if (exception != null) {
                        exception.printStackTrace(s);
                    }
                }
            }
            this.inPrintStackTrace = false;
        }
    }
    
    private static String exceptionToString(final Exception ex) {
        if (ex == null) {
            return null;
        }
        final String s = ex.getClass().getName();
        final String message = ex.getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
    
    static {
        JDOException.msg = I18NHelper.getInstance("javax.jdo.Bundle");
    }
}
