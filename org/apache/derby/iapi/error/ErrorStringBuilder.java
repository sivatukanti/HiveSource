// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import java.sql.SQLException;
import java.io.Writer;
import org.apache.derby.iapi.services.stream.PrintWriterGetHeader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorStringBuilder
{
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private PrintWriterGetHeader headerGetter;
    
    public ErrorStringBuilder(final PrintWriterGetHeader headerGetter) {
        this.headerGetter = headerGetter;
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(this.stringWriter);
    }
    
    public void append(final String s) {
        if (this.headerGetter != null) {
            this.printWriter.print(this.headerGetter.getHeader());
        }
        this.printWriter.print(s);
    }
    
    public void appendln(final String x) {
        if (this.headerGetter != null) {
            this.printWriter.print(this.headerGetter.getHeader());
        }
        this.printWriter.println(x);
    }
    
    public void stackTrace(Throwable cause) {
        int n = 0;
        while (cause != null) {
            if (n > 0) {
                this.printWriter.println("============= begin nested exception, level (" + n + ") ===========");
            }
            cause.printStackTrace(this.printWriter);
            if (cause instanceof SQLException) {
                final SQLException nextException = ((SQLException)cause).getNextException();
                cause = ((nextException == null) ? cause.getCause() : nextException);
            }
            else {
                cause = cause.getCause();
            }
            if (n > 0) {
                this.printWriter.println("============= end nested exception, level (" + n + ") ===========");
            }
            ++n;
        }
    }
    
    public void reset() {
        this.stringWriter.getBuffer().setLength(0);
    }
    
    public StringBuffer get() {
        return this.stringWriter.getBuffer();
    }
}
