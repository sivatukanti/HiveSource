// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.helpers;

import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;
import java.io.FilterWriter;

public class QuietWriter extends FilterWriter
{
    protected ErrorHandler errorHandler;
    
    public QuietWriter(final Writer writer, final ErrorHandler errorHandler) {
        super(writer);
        this.setErrorHandler(errorHandler);
    }
    
    public void write(final String string) {
        if (string != null) {
            try {
                this.out.write(string);
            }
            catch (Exception e) {
                this.errorHandler.error("Failed to write [" + string + "].", e, 1);
            }
        }
    }
    
    public void flush() {
        try {
            this.out.flush();
        }
        catch (Exception e) {
            this.errorHandler.error("Failed to flush writer,", e, 2);
        }
    }
    
    public void setErrorHandler(final ErrorHandler eh) {
        if (eh == null) {
            throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
        }
        this.errorHandler = eh;
    }
}
