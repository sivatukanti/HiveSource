// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.tools.i18n;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;

public class LocalizedOutput extends PrintWriter
{
    private OutputStream out;
    
    public LocalizedOutput(final OutputStream outputStream) {
        super(new OutputStreamWriter(outputStream), true);
        this.out = outputStream;
    }
    
    LocalizedOutput(final OutputStream outputStream, final String charsetName) throws UnsupportedEncodingException {
        super(new OutputStreamWriter(outputStream, charsetName), true);
        this.out = outputStream;
    }
    
    public boolean isStandardOutput() {
        return this.out == System.out;
    }
    
    public void close() {
        if (!this.isStandardOutput()) {
            super.close();
        }
    }
}
