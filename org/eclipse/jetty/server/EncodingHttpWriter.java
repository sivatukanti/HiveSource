// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class EncodingHttpWriter extends HttpWriter
{
    final Writer _converter;
    
    public EncodingHttpWriter(final HttpOutput out, final String encoding) {
        super(out);
        try {
            this._converter = new OutputStreamWriter(this._bytes, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(final char[] s, int offset, int length) throws IOException {
        final HttpOutput out = this._out;
        if (length == 0 && out.isAllContentWritten()) {
            out.close();
            return;
        }
        while (length > 0) {
            this._bytes.reset();
            final int chars = (length > 512) ? 512 : length;
            this._converter.write(s, offset, chars);
            this._converter.flush();
            this._bytes.writeTo(out);
            length -= chars;
            offset += chars;
        }
    }
}
