// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.OutputStream;

public class Iso88591HttpWriter extends HttpWriter
{
    public Iso88591HttpWriter(final HttpOutput out) {
        super(out);
    }
    
    @Override
    public void write(final char[] s, int offset, int length) throws IOException {
        final HttpOutput out = this._out;
        if (length == 0 && out.isAllContentWritten()) {
            this.close();
            return;
        }
        if (length == 1) {
            final int c = s[offset];
            out.write((c < 256) ? c : 63);
            return;
        }
        while (length > 0) {
            this._bytes.reset();
            int chars = (length > 512) ? 512 : length;
            final byte[] buffer = this._bytes.getBuf();
            int bytes = this._bytes.getCount();
            if (chars > buffer.length - bytes) {
                chars = buffer.length - bytes;
            }
            for (int i = 0; i < chars; ++i) {
                final int c2 = s[offset + i];
                buffer[bytes++] = (byte)((c2 < 256) ? c2 : 63);
            }
            if (bytes >= 0) {
                this._bytes.setCount(bytes);
            }
            this._bytes.writeTo(out);
            length -= chars;
            offset += chars;
        }
    }
}
