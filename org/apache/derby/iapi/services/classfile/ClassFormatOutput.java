// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.AccessibleByteArrayOutputStream;
import java.io.DataOutputStream;

public final class ClassFormatOutput extends DataOutputStream
{
    public ClassFormatOutput() {
        this(512);
    }
    
    public ClassFormatOutput(final int n) {
        this(new AccessibleByteArrayOutputStream(n));
    }
    
    public ClassFormatOutput(final OutputStream out) {
        super(out);
    }
    
    public void putU1(final int b) throws IOException {
        if (b > 255) {
            limit("U1", 255, b);
        }
        this.write(b);
    }
    
    public void putU2(final int n) throws IOException {
        this.putU2("U2", n);
    }
    
    public void putU2(final String s, final int b) throws IOException {
        if (b > 65535) {
            limit(s, 65535, b);
        }
        this.write(b >> 8);
        this.write(b);
    }
    
    public void putU4(final int v) throws IOException {
        this.writeInt(v);
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        ((AccessibleByteArrayOutputStream)this.out).writeTo(out);
    }
    
    public byte[] getData() {
        return ((AccessibleByteArrayOutputStream)this.out).getInternalByteArray();
    }
    
    static void limit(final String str, final int i, final int j) throws IOException {
        throw new IOException(str + "(" + j + " > " + i + ")");
    }
}
