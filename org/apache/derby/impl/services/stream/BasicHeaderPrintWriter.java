// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.stream;

import java.io.Writer;
import java.io.OutputStream;
import org.apache.derby.iapi.services.stream.PrintWriterGetHeader;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import java.io.PrintWriter;

class BasicHeaderPrintWriter extends PrintWriter implements HeaderPrintWriter
{
    private final PrintWriterGetHeader headerGetter;
    private final boolean canClose;
    private final String name;
    
    BasicHeaderPrintWriter(final OutputStream out, final PrintWriterGetHeader headerGetter, final boolean canClose, final String name) {
        super(out, true);
        this.headerGetter = headerGetter;
        this.canClose = canClose;
        this.name = name;
    }
    
    BasicHeaderPrintWriter(final Writer out, final PrintWriterGetHeader headerGetter, final boolean canClose, final String name) {
        super(out, true);
        this.headerGetter = headerGetter;
        this.canClose = canClose;
        this.name = name;
    }
    
    public synchronized void printlnWithHeader(final String x) {
        this.print(this.headerGetter.getHeader());
        this.println(x);
    }
    
    public PrintWriterGetHeader getHeader() {
        return this.headerGetter;
    }
    
    public PrintWriter getPrintWriter() {
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    void complete() {
        this.flush();
        if (this.canClose) {
            this.close();
        }
    }
}
