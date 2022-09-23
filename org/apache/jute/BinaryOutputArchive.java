// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.util.TreeMap;
import java.util.List;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.DataOutput;
import java.nio.ByteBuffer;

public class BinaryOutputArchive implements OutputArchive
{
    private ByteBuffer bb;
    private DataOutput out;
    
    public static BinaryOutputArchive getArchive(final OutputStream strm) {
        return new BinaryOutputArchive(new DataOutputStream(strm));
    }
    
    public BinaryOutputArchive(final DataOutput out) {
        this.bb = ByteBuffer.allocate(1024);
        this.out = out;
    }
    
    @Override
    public void writeByte(final byte b, final String tag) throws IOException {
        this.out.writeByte(b);
    }
    
    @Override
    public void writeBool(final boolean b, final String tag) throws IOException {
        this.out.writeBoolean(b);
    }
    
    @Override
    public void writeInt(final int i, final String tag) throws IOException {
        this.out.writeInt(i);
    }
    
    @Override
    public void writeLong(final long l, final String tag) throws IOException {
        this.out.writeLong(l);
    }
    
    @Override
    public void writeFloat(final float f, final String tag) throws IOException {
        this.out.writeFloat(f);
    }
    
    @Override
    public void writeDouble(final double d, final String tag) throws IOException {
        this.out.writeDouble(d);
    }
    
    private final ByteBuffer stringToByteBuffer(final CharSequence s) {
        this.bb.clear();
        for (int len = s.length(), i = 0; i < len; ++i) {
            if (this.bb.remaining() < 3) {
                final ByteBuffer n = ByteBuffer.allocate(this.bb.capacity() << 1);
                this.bb.flip();
                n.put(this.bb);
                this.bb = n;
            }
            final char c = s.charAt(i);
            if (c < '\u0080') {
                this.bb.put((byte)c);
            }
            else if (c < '\u0800') {
                this.bb.put((byte)(0xC0 | c >> 6));
                this.bb.put((byte)(0x80 | (c & '?')));
            }
            else {
                this.bb.put((byte)(0xE0 | c >> 12));
                this.bb.put((byte)(0x80 | (c >> 6 & 0x3F)));
                this.bb.put((byte)(0x80 | (c & '?')));
            }
        }
        this.bb.flip();
        return this.bb;
    }
    
    @Override
    public void writeString(final String s, final String tag) throws IOException {
        if (s == null) {
            this.writeInt(-1, "len");
            return;
        }
        final ByteBuffer bb = this.stringToByteBuffer(s);
        this.writeInt(bb.remaining(), "len");
        this.out.write(bb.array(), bb.position(), bb.limit());
    }
    
    @Override
    public void writeBuffer(final byte[] barr, final String tag) throws IOException {
        if (barr == null) {
            this.out.writeInt(-1);
            return;
        }
        this.out.writeInt(barr.length);
        this.out.write(barr);
    }
    
    @Override
    public void writeRecord(final Record r, final String tag) throws IOException {
        r.serialize(this, tag);
    }
    
    @Override
    public void startRecord(final Record r, final String tag) throws IOException {
    }
    
    @Override
    public void endRecord(final Record r, final String tag) throws IOException {
    }
    
    @Override
    public void startVector(final List v, final String tag) throws IOException {
        if (v == null) {
            this.writeInt(-1, tag);
            return;
        }
        this.writeInt(v.size(), tag);
    }
    
    @Override
    public void endVector(final List v, final String tag) throws IOException {
    }
    
    @Override
    public void startMap(final TreeMap v, final String tag) throws IOException {
        this.writeInt(v.size(), tag);
    }
    
    @Override
    public void endMap(final TreeMap v, final String tag) throws IOException {
    }
}
