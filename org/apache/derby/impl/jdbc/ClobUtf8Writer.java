// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import java.io.IOException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.Writer;

final class ClobUtf8Writer extends Writer
{
    private TemporaryClob control;
    private long pos;
    private boolean closed;
    
    ClobUtf8Writer(final TemporaryClob control, final long pos) {
        this.control = control;
        this.pos = pos;
        this.closed = false;
    }
    
    public void flush() throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
    }
    
    public void close() {
        this.closed = true;
    }
    
    public void write(final char[] data, final int offset, final int count) throws IOException {
        if (this.closed) {
            throw new IOException(MessageService.getTextMessage("J104"));
        }
        try {
            final long insertString = this.control.insertString(String.copyValueOf(data, offset, count), this.pos);
            if (insertString > 0L) {
                this.pos += insertString;
            }
        }
        catch (SQLException ex) {
            throw Util.newIOException(ex);
        }
    }
}
