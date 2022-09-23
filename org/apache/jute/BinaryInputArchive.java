// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.DataInput;

public class BinaryInputArchive implements InputArchive
{
    public static final String UNREASONBLE_LENGTH = "Unreasonable length = ";
    private DataInput in;
    public static final int maxBuffer;
    
    public static BinaryInputArchive getArchive(final InputStream strm) {
        return new BinaryInputArchive(new DataInputStream(strm));
    }
    
    public BinaryInputArchive(final DataInput in) {
        this.in = in;
    }
    
    @Override
    public byte readByte(final String tag) throws IOException {
        return this.in.readByte();
    }
    
    @Override
    public boolean readBool(final String tag) throws IOException {
        return this.in.readBoolean();
    }
    
    @Override
    public int readInt(final String tag) throws IOException {
        return this.in.readInt();
    }
    
    @Override
    public long readLong(final String tag) throws IOException {
        return this.in.readLong();
    }
    
    @Override
    public float readFloat(final String tag) throws IOException {
        return this.in.readFloat();
    }
    
    @Override
    public double readDouble(final String tag) throws IOException {
        return this.in.readDouble();
    }
    
    @Override
    public String readString(final String tag) throws IOException {
        final int len = this.in.readInt();
        if (len == -1) {
            return null;
        }
        this.checkLength(len);
        final byte[] b = new byte[len];
        this.in.readFully(b);
        return new String(b, "UTF8");
    }
    
    @Override
    public byte[] readBuffer(final String tag) throws IOException {
        final int len = this.readInt(tag);
        if (len == -1) {
            return null;
        }
        this.checkLength(len);
        final byte[] arr = new byte[len];
        this.in.readFully(arr);
        return arr;
    }
    
    @Override
    public void readRecord(final Record r, final String tag) throws IOException {
        r.deserialize(this, tag);
    }
    
    @Override
    public void startRecord(final String tag) throws IOException {
    }
    
    @Override
    public void endRecord(final String tag) throws IOException {
    }
    
    @Override
    public Index startVector(final String tag) throws IOException {
        final int len = this.readInt(tag);
        if (len == -1) {
            return null;
        }
        return new BinaryIndex(len);
    }
    
    @Override
    public void endVector(final String tag) throws IOException {
    }
    
    @Override
    public Index startMap(final String tag) throws IOException {
        return new BinaryIndex(this.readInt(tag));
    }
    
    @Override
    public void endMap(final String tag) throws IOException {
    }
    
    private void checkLength(final int len) throws IOException {
        if (len < 0 || len > BinaryInputArchive.maxBuffer + 1024) {
            throw new IOException("Unreasonable length = " + len);
        }
    }
    
    static {
        maxBuffer = Integer.getInteger("jute.maxbuffer", 1048575);
    }
    
    private static class BinaryIndex implements Index
    {
        private int nelems;
        
        BinaryIndex(final int nelems) {
            this.nelems = nelems;
        }
        
        @Override
        public boolean done() {
            return this.nelems <= 0;
        }
        
        @Override
        public void incr() {
            --this.nelems;
        }
    }
}
