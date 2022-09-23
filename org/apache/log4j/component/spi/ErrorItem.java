// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import java.io.PrintStream;

public class ErrorItem
{
    String message;
    int colNumber;
    int lineNumber;
    Throwable exception;
    
    public ErrorItem(final String message, final Exception e) {
        this.colNumber = -1;
        this.lineNumber = -1;
        this.message = message;
        this.exception = e;
    }
    
    public ErrorItem(final String message) {
        this(message, null);
    }
    
    public int getColNumber() {
        return this.colNumber;
    }
    
    public void setColNumber(final int colNumber) {
        this.colNumber = colNumber;
    }
    
    public Throwable getException() {
        return this.exception;
    }
    
    public void setException(final Throwable exception) {
        this.exception = exception;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public String toString() {
        String str = "Reported error: \"" + this.message + "\"";
        if (this.lineNumber != -1) {
            str = str + " at line " + this.lineNumber + " column " + this.colNumber;
        }
        if (this.exception != null) {
            str = str + " with exception " + this.exception;
        }
        return str;
    }
    
    public void dump() {
        this.dump(System.out);
    }
    
    public void dump(final PrintStream ps) {
        String str = "Reported error: \"" + this.message + "\"";
        if (this.lineNumber != -1) {
            str = str + " at line " + this.lineNumber + " column " + this.colNumber;
        }
        ps.println(str);
        if (this.exception != null) {
            this.exception.printStackTrace(ps);
        }
    }
}
