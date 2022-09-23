// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;

public class WriterOutputStream extends OutputStream
{
    protected Writer _writer;
    protected String _encoding;
    private byte[] _buf;
    
    public WriterOutputStream(final Writer writer, final String encoding) {
        this._buf = new byte[1];
        this._writer = writer;
        this._encoding = encoding;
    }
    
    public WriterOutputStream(final Writer writer) {
        this._buf = new byte[1];
        this._writer = writer;
    }
    
    public void close() throws IOException {
        this._writer.close();
        this._writer = null;
        this._encoding = null;
    }
    
    public void flush() throws IOException {
        this._writer.flush();
    }
    
    public void write(final byte[] b) throws IOException {
        if (this._encoding == null) {
            this._writer.write(new String(b));
        }
        else {
            this._writer.write(new String(b, this._encoding));
        }
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this._encoding == null) {
            this._writer.write(new String(b, off, len));
        }
        else {
            this._writer.write(new String(b, off, len, this._encoding));
        }
    }
    
    public synchronized void write(final int b) throws IOException {
        this._buf[0] = (byte)b;
        this.write(this._buf);
    }
}
