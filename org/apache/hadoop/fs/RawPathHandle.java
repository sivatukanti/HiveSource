// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class RawPathHandle implements PathHandle
{
    private static final long serialVersionUID = 1286953538832L;
    public static final int MAX_SIZE = 1048576;
    private transient ByteBuffer fd;
    
    public RawPathHandle(final ByteBuffer fd) {
        this.fd = ((null == fd) ? ByteBuffer.allocate(0) : fd.asReadOnlyBuffer());
    }
    
    public RawPathHandle(final PathHandle handle) {
        final ByteBuffer hb = (null == handle) ? ByteBuffer.allocate(0) : handle.bytes();
        (this.fd = ByteBuffer.allocate(hb.remaining())).put(hb);
        this.fd.flip();
    }
    
    @Override
    public ByteBuffer bytes() {
        return this.fd.asReadOnlyBuffer();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PathHandle)) {
            return false;
        }
        final PathHandle o = (PathHandle)other;
        return this.bytes().equals(o.bytes());
    }
    
    @Override
    public int hashCode() {
        return this.bytes().hashCode();
    }
    
    @Override
    public String toString() {
        return this.bytes().toString();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.fd.remaining());
        if (this.fd.hasArray()) {
            out.write(this.fd.array(), this.fd.position(), this.fd.remaining());
        }
        else {
            final byte[] x = new byte[this.fd.remaining()];
            this.fd.slice().get(x);
            out.write(x);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int len = in.readInt();
        if (len < 0 || len > 1048576) {
            throw new IOException("Illegal buffer length " + len);
        }
        final byte[] x = new byte[len];
        in.readFully(x);
        this.fd = ByteBuffer.wrap(x);
    }
    
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("Stream data required");
    }
}
