// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Http1FieldPreEncoder implements HttpFieldPreEncoder
{
    @Override
    public HttpVersion getHttpVersion() {
        return HttpVersion.HTTP_1_0;
    }
    
    @Override
    public byte[] getEncodedField(final HttpHeader header, final String headerString, final String value) {
        if (header != null) {
            final int cbl = header.getBytesColonSpace().length;
            final byte[] bytes = Arrays.copyOf(header.getBytesColonSpace(), cbl + value.length() + 2);
            System.arraycopy(value.getBytes(StandardCharsets.ISO_8859_1), 0, bytes, cbl, value.length());
            bytes[bytes.length - 2] = 13;
            bytes[bytes.length - 1] = 10;
            return bytes;
        }
        final byte[] n = headerString.getBytes(StandardCharsets.ISO_8859_1);
        final byte[] v = value.getBytes(StandardCharsets.ISO_8859_1);
        final byte[] bytes2 = Arrays.copyOf(n, n.length + 2 + v.length + 2);
        bytes2[n.length] = 58;
        bytes2[n.length] = 32;
        bytes2[bytes2.length - 2] = 13;
        bytes2[bytes2.length - 1] = 10;
        return bytes2;
    }
}
