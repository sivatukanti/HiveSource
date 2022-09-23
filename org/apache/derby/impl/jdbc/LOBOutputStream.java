// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.error.ExceptionUtil;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.OutputStream;

public class LOBOutputStream extends OutputStream
{
    private boolean closed;
    private final LOBStreamControl control;
    private long pos;
    
    LOBOutputStream(final LOBStreamControl control, final long pos) {
        this.closed = false;
        this.control = control;
        this.pos = pos;
    }
    
    public void write(final int n) throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
        try {
            this.pos = this.control.write(n, this.pos);
        }
        catch (StandardException ex) {
            throw Util.newIOException(ex);
        }
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
        try {
            this.pos = this.control.write(array, n, n2, this.pos);
        }
        catch (StandardException ex) {
            if (ex.getSQLState().equals(ExceptionUtil.getSQLStateFromIdentifier("XJ078.S"))) {
                throw new ArrayIndexOutOfBoundsException(ex.getMessage());
            }
            throw Util.newIOException(ex);
        }
    }
    
    public void close() throws IOException {
        this.closed = true;
    }
}
