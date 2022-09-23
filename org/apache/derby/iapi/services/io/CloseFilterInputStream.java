// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class CloseFilterInputStream extends FilterInputStream
{
    private static final String MESSAGE;
    private boolean closed;
    
    public CloseFilterInputStream(final InputStream in) {
        super(in);
    }
    
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
    
    public int available() throws IOException {
        this.checkIfClosed();
        return super.available();
    }
    
    public int read() throws IOException {
        this.checkIfClosed();
        return super.read();
    }
    
    public int read(final byte[] b) throws IOException {
        this.checkIfClosed();
        return super.read(b);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.checkIfClosed();
        return super.read(b, off, len);
    }
    
    public long skip(final long n) throws IOException {
        this.checkIfClosed();
        return super.skip(n);
    }
    
    private void checkIfClosed() throws IOException {
        if (this.closed) {
            throw new IOException(CloseFilterInputStream.MESSAGE);
        }
    }
    
    static {
        MESSAGE = MessageService.getTextMessage("J104");
    }
}
