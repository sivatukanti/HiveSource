// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.jute.OutputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class ByteBufferOutputStream extends OutputStream
{
    ByteBuffer bb;
    
    public ByteBufferOutputStream(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.bb.put((byte)b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.bb.put(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.bb.put(b, off, len);
    }
    
    public static void record2ByteBuffer(final Record record, final ByteBuffer bb) throws IOException {
        final BinaryOutputArchive oa = BinaryOutputArchive.getArchive(new ByteBufferOutputStream(bb));
        record.serialize(oa, "request");
    }
}
