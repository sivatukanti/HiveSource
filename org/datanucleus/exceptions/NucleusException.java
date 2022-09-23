// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import java.io.PrintWriter;
import java.io.PrintStream;

public class NucleusException extends RuntimeException
{
    Throwable[] nested;
    Object failed;
    boolean fatal;
    
    public NucleusException() {
    }
    
    public NucleusException(final String msg) {
        super(msg);
    }
    
    public NucleusException(final String msg, final Throwable[] nested) {
        super(msg);
        this.nested = nested;
    }
    
    public NucleusException(final String msg, final Throwable nested) {
        super(msg);
        this.nested = new Throwable[] { nested };
    }
    
    public NucleusException(final String msg, final Object failed) {
        super(msg);
        this.failed = failed;
    }
    
    public NucleusException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg);
        this.nested = nested;
        this.failed = failed;
    }
    
    public NucleusException(final String msg, final Throwable nested, final Object failed) {
        super(msg);
        this.nested = new Throwable[] { nested };
        this.failed = failed;
    }
    
    public NucleusException setFatal() {
        this.fatal = true;
        return this;
    }
    
    public boolean isFatal() {
        return this.fatal;
    }
    
    public Object getFailedObject() {
        return this.failed;
    }
    
    public void setNestedException(final Throwable nested) {
        this.nested = new Throwable[] { nested };
    }
    
    public Throwable[] getNestedExceptions() {
        return this.nested;
    }
    
    @Override
    public synchronized Throwable getCause() {
        return (this.nested == null || this.nested.length == 0) ? null : this.nested[0];
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    @Override
    public synchronized void printStackTrace(final PrintStream s) {
        final int len = (this.nested == null) ? 0 : this.nested.length;
        synchronized (s) {
            if (this.getMessage() != null) {
                s.println(this.getMessage());
            }
            super.printStackTrace(s);
            if (len > 0) {
                s.println("Nested Throwables StackTrace:");
                for (int i = 0; i < len; ++i) {
                    final Throwable exception = this.nested[i];
                    if (exception != null) {
                        exception.printStackTrace(s);
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized void printStackTrace(final PrintWriter s) {
        final int len = (this.nested == null) ? 0 : this.nested.length;
        synchronized (s) {
            if (this.getMessage() != null) {
                s.println(this.getMessage());
            }
            super.printStackTrace(s);
            if (len > 0) {
                s.println("Nested Throwables StackTrace:");
                for (int i = 0; i < len; ++i) {
                    final Throwable exception = this.nested[i];
                    if (exception != null) {
                        exception.printStackTrace(s);
                    }
                }
            }
        }
    }
}
