// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import java.io.ByteArrayOutputStream;

public class TByteArrayOutputStream extends ByteArrayOutputStream
{
    public TByteArrayOutputStream(final int size) {
        super(size);
    }
    
    public TByteArrayOutputStream() {
    }
    
    public byte[] get() {
        return this.buf;
    }
    
    public int len() {
        return this.count;
    }
}
