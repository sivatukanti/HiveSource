// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.pop3;

import javax.mail.util.SharedByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

class SharedByteArrayOutputStream extends ByteArrayOutputStream
{
    public SharedByteArrayOutputStream(final int size) {
        super(size);
    }
    
    public InputStream toStream() {
        return new SharedByteArrayInputStream(this.buf, 0, this.count);
    }
}
