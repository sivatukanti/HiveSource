// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

class FallbackZipEncoding implements ZipEncoding
{
    private final String charset;
    
    public FallbackZipEncoding() {
        this.charset = null;
    }
    
    public FallbackZipEncoding(final String charset) {
        this.charset = charset;
    }
    
    public boolean canEncode(final String name) {
        return true;
    }
    
    public ByteBuffer encode(final String name) throws IOException {
        if (this.charset == null) {
            return ByteBuffer.wrap(name.getBytes());
        }
        return ByteBuffer.wrap(name.getBytes(this.charset));
    }
    
    public String decode(final byte[] data) throws IOException {
        if (this.charset == null) {
            return new String(data);
        }
        return new String(data, this.charset);
    }
}
