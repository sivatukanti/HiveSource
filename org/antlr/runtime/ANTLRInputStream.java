// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class ANTLRInputStream extends ANTLRReaderStream
{
    public ANTLRInputStream() {
    }
    
    public ANTLRInputStream(final InputStream input) throws IOException {
        this(input, null);
    }
    
    public ANTLRInputStream(final InputStream input, final int size) throws IOException {
        this(input, size, null);
    }
    
    public ANTLRInputStream(final InputStream input, final String encoding) throws IOException {
        this(input, 1024, encoding);
    }
    
    public ANTLRInputStream(final InputStream input, final int size, final String encoding) throws IOException {
        this(input, size, 1024, encoding);
    }
    
    public ANTLRInputStream(final InputStream input, final int size, final int readBufferSize, final String encoding) throws IOException {
        InputStreamReader isr;
        if (encoding != null) {
            isr = new InputStreamReader(input, encoding);
        }
        else {
            isr = new InputStreamReader(input);
        }
        this.load(isr, size, readBufferSize);
    }
}
