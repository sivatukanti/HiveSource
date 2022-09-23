// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import org.apache.derby.io.StorageRandomAccessFile;

public class VirtualRandomAccessFile implements StorageRandomAccessFile
{
    private final DataStoreEntry entry;
    private long fp;
    private final BlockedByteArrayInputStream bIn;
    private final DataInputStream dIs;
    private final BlockedByteArrayOutputStream bOut;
    private final DataOutputStream dOs;
    
    public VirtualRandomAccessFile(final DataStoreEntry entry, final boolean b) throws FileNotFoundException {
        this.entry = entry;
        (this.bIn = entry.getInputStream()).setPosition(0L);
        this.dIs = new DataInputStream(this.bIn);
        if (b) {
            this.bOut = null;
            this.dOs = null;
        }
        else {
            (this.bOut = entry.getOutputStream(true)).setPosition(0L);
            this.dOs = new DataOutputStream(this.bOut);
        }
    }
    
    public void close() throws IOException {
        this.dIs.close();
        if (this.dOs != null) {
            this.dOs.close();
        }
        this.fp = Long.MIN_VALUE;
    }
    
    public long getFilePointer() {
        return this.fp;
    }
    
    public long length() {
        return this.entry.length();
    }
    
    public void seek(final long n) throws IOException {
        if (n < 0L) {
            throw new IOException("Negative position: " + n);
        }
        this.fp = n;
        this.bIn.setPosition(n);
        if (this.bOut != null) {
            this.bOut.setPosition(n);
        }
    }
    
    public void setLength(final long n) {
        if (this.bOut == null) {
            throw new NullPointerException();
        }
        this.entry.setLength(n);
        if (n < this.fp) {
            this.fp = n;
        }
    }
    
    public void sync() {
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.bIn.read(array, n, n2);
        this.fp = this.bIn.getPosition();
        return read;
    }
    
    public void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.dIs.readFully(b, off, len);
        this.fp = this.bIn.getPosition();
    }
    
    public int skipBytes(final int n) {
        if (n <= 0) {
            return 0;
        }
        final long min = Math.min(n, this.entry.length() - this.fp);
        this.fp += min;
        return (int)min;
    }
    
    public boolean readBoolean() throws IOException {
        final boolean boolean1 = this.dIs.readBoolean();
        this.fp = this.bIn.getPosition();
        return boolean1;
    }
    
    public byte readByte() throws IOException {
        final byte byte1 = this.dIs.readByte();
        this.fp = this.bIn.getPosition();
        return byte1;
    }
    
    public int readUnsignedByte() throws IOException {
        final int unsignedByte = this.dIs.readUnsignedByte();
        this.fp = this.bIn.getPosition();
        return unsignedByte;
    }
    
    public short readShort() throws IOException {
        final short short1 = this.dIs.readShort();
        this.fp = this.bIn.getPosition();
        return short1;
    }
    
    public int readUnsignedShort() throws IOException {
        final int unsignedShort = this.dIs.readUnsignedShort();
        this.fp = this.bIn.getPosition();
        return unsignedShort;
    }
    
    public char readChar() throws IOException {
        final char char1 = this.dIs.readChar();
        this.fp = this.bIn.getPosition();
        return char1;
    }
    
    public int readInt() throws IOException {
        final int int1 = this.dIs.readInt();
        this.fp = this.bIn.getPosition();
        return int1;
    }
    
    public long readLong() throws IOException {
        final long long1 = this.dIs.readLong();
        this.fp = this.bIn.getPosition();
        return long1;
    }
    
    public float readFloat() throws IOException {
        final float float1 = this.dIs.readFloat();
        this.fp = this.bIn.getPosition();
        return float1;
    }
    
    public double readDouble() throws IOException {
        final double double1 = this.dIs.readDouble();
        this.fp = this.bIn.getPosition();
        return double1;
    }
    
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("readLine");
    }
    
    public String readUTF() throws IOException {
        final String utf = this.dIs.readUTF();
        this.fp = this.bIn.getPosition();
        return utf;
    }
    
    public void write(final int b) throws IOException {
        this.dOs.write(b);
        this.fp = this.bOut.getPosition();
    }
    
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.dOs.write(b, off, len);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeBoolean(final boolean v) throws IOException {
        this.dOs.writeBoolean(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeByte(final int v) throws IOException {
        this.dOs.writeByte(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeShort(final int v) throws IOException {
        this.dOs.writeShort(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeChar(final int v) throws IOException {
        this.dOs.writeChar(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeInt(final int v) throws IOException {
        this.dOs.writeInt(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeLong(final long v) throws IOException {
        this.dOs.writeLong(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeFloat(final float v) throws IOException {
        this.dOs.writeFloat(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeDouble(final double v) throws IOException {
        this.dOs.writeDouble(v);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeBytes(final String s) throws IOException {
        this.dOs.writeBytes(s);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeChars(final String s) throws IOException {
        this.dOs.writeChars(s);
        this.fp = this.bOut.getPosition();
    }
    
    public void writeUTF(final String str) throws IOException {
        this.dOs.writeUTF(str);
        this.fp = this.bOut.getPosition();
    }
}
