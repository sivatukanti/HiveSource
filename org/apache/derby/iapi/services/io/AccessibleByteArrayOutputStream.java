// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class AccessibleByteArrayOutputStream extends ByteArrayOutputStream
{
    public AccessibleByteArrayOutputStream() {
    }
    
    public AccessibleByteArrayOutputStream(final int size) {
        super(size);
    }
    
    public byte[] getInternalByteArray() {
        return this.buf;
    }
    
    public void readFrom(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[8192];
        while (true) {
            final int read = inputStream.read(array, 0, this.buf.length);
            if (read == -1) {
                break;
            }
            this.write(array, 0, read);
        }
    }
    
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }
    
    public static InputStream copyStream(final InputStream inputStream, final int n) throws IOException {
        final AccessibleByteArrayOutputStream accessibleByteArrayOutputStream = new AccessibleByteArrayOutputStream(n);
        accessibleByteArrayOutputStream.readFrom(inputStream);
        return accessibleByteArrayOutputStream.getInputStream();
    }
}
