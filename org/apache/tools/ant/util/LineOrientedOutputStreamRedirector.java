// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class LineOrientedOutputStreamRedirector extends LineOrientedOutputStream
{
    private OutputStream stream;
    private static final byte[] EOL;
    
    public LineOrientedOutputStreamRedirector(final OutputStream stream) {
        this.stream = stream;
    }
    
    @Override
    protected void processLine(final byte[] b) throws IOException {
        this.stream.write(b);
        this.stream.write(LineOrientedOutputStreamRedirector.EOL);
    }
    
    @Override
    protected void processLine(final String line) throws IOException {
        this.stream.write((line + System.getProperty("line.separator")).getBytes());
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.stream.close();
    }
    
    @Override
    public void flush() throws IOException {
        super.flush();
        this.stream.flush();
    }
    
    static {
        EOL = System.getProperty("line.separator").getBytes();
    }
}
