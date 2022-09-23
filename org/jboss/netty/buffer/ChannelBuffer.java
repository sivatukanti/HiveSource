// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.charset.Charset;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.nio.channels.GatheringByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface ChannelBuffer extends Comparable<ChannelBuffer>
{
    ChannelBufferFactory factory();
    
    int capacity();
    
    ByteOrder order();
    
    boolean isDirect();
    
    int readerIndex();
    
    void readerIndex(final int p0);
    
    int writerIndex();
    
    void writerIndex(final int p0);
    
    void setIndex(final int p0, final int p1);
    
    int readableBytes();
    
    int writableBytes();
    
    boolean readable();
    
    boolean writable();
    
    void clear();
    
    void markReaderIndex();
    
    void resetReaderIndex();
    
    void markWriterIndex();
    
    void resetWriterIndex();
    
    void discardReadBytes();
    
    void ensureWritableBytes(final int p0);
    
    byte getByte(final int p0);
    
    short getUnsignedByte(final int p0);
    
    short getShort(final int p0);
    
    int getUnsignedShort(final int p0);
    
    int getMedium(final int p0);
    
    int getUnsignedMedium(final int p0);
    
    int getInt(final int p0);
    
    long getUnsignedInt(final int p0);
    
    long getLong(final int p0);
    
    char getChar(final int p0);
    
    float getFloat(final int p0);
    
    double getDouble(final int p0);
    
    void getBytes(final int p0, final ChannelBuffer p1);
    
    void getBytes(final int p0, final ChannelBuffer p1, final int p2);
    
    void getBytes(final int p0, final ChannelBuffer p1, final int p2, final int p3);
    
    void getBytes(final int p0, final byte[] p1);
    
    void getBytes(final int p0, final byte[] p1, final int p2, final int p3);
    
    void getBytes(final int p0, final ByteBuffer p1);
    
    void getBytes(final int p0, final OutputStream p1, final int p2) throws IOException;
    
    int getBytes(final int p0, final GatheringByteChannel p1, final int p2) throws IOException;
    
    void setByte(final int p0, final int p1);
    
    void setShort(final int p0, final int p1);
    
    void setMedium(final int p0, final int p1);
    
    void setInt(final int p0, final int p1);
    
    void setLong(final int p0, final long p1);
    
    void setChar(final int p0, final int p1);
    
    void setFloat(final int p0, final float p1);
    
    void setDouble(final int p0, final double p1);
    
    void setBytes(final int p0, final ChannelBuffer p1);
    
    void setBytes(final int p0, final ChannelBuffer p1, final int p2);
    
    void setBytes(final int p0, final ChannelBuffer p1, final int p2, final int p3);
    
    void setBytes(final int p0, final byte[] p1);
    
    void setBytes(final int p0, final byte[] p1, final int p2, final int p3);
    
    void setBytes(final int p0, final ByteBuffer p1);
    
    int setBytes(final int p0, final InputStream p1, final int p2) throws IOException;
    
    int setBytes(final int p0, final ScatteringByteChannel p1, final int p2) throws IOException;
    
    void setZero(final int p0, final int p1);
    
    byte readByte();
    
    short readUnsignedByte();
    
    short readShort();
    
    int readUnsignedShort();
    
    int readMedium();
    
    int readUnsignedMedium();
    
    int readInt();
    
    long readUnsignedInt();
    
    long readLong();
    
    char readChar();
    
    float readFloat();
    
    double readDouble();
    
    ChannelBuffer readBytes(final int p0);
    
    ChannelBuffer readSlice(final int p0);
    
    void readBytes(final ChannelBuffer p0);
    
    void readBytes(final ChannelBuffer p0, final int p1);
    
    void readBytes(final ChannelBuffer p0, final int p1, final int p2);
    
    void readBytes(final byte[] p0);
    
    void readBytes(final byte[] p0, final int p1, final int p2);
    
    void readBytes(final ByteBuffer p0);
    
    void readBytes(final OutputStream p0, final int p1) throws IOException;
    
    int readBytes(final GatheringByteChannel p0, final int p1) throws IOException;
    
    void skipBytes(final int p0);
    
    void writeByte(final int p0);
    
    void writeShort(final int p0);
    
    void writeMedium(final int p0);
    
    void writeInt(final int p0);
    
    void writeLong(final long p0);
    
    void writeChar(final int p0);
    
    void writeFloat(final float p0);
    
    void writeDouble(final double p0);
    
    void writeBytes(final ChannelBuffer p0);
    
    void writeBytes(final ChannelBuffer p0, final int p1);
    
    void writeBytes(final ChannelBuffer p0, final int p1, final int p2);
    
    void writeBytes(final byte[] p0);
    
    void writeBytes(final byte[] p0, final int p1, final int p2);
    
    void writeBytes(final ByteBuffer p0);
    
    int writeBytes(final InputStream p0, final int p1) throws IOException;
    
    int writeBytes(final ScatteringByteChannel p0, final int p1) throws IOException;
    
    void writeZero(final int p0);
    
    int indexOf(final int p0, final int p1, final byte p2);
    
    int indexOf(final int p0, final int p1, final ChannelBufferIndexFinder p2);
    
    int bytesBefore(final byte p0);
    
    int bytesBefore(final ChannelBufferIndexFinder p0);
    
    int bytesBefore(final int p0, final byte p1);
    
    int bytesBefore(final int p0, final ChannelBufferIndexFinder p1);
    
    int bytesBefore(final int p0, final int p1, final byte p2);
    
    int bytesBefore(final int p0, final int p1, final ChannelBufferIndexFinder p2);
    
    ChannelBuffer copy();
    
    ChannelBuffer copy(final int p0, final int p1);
    
    ChannelBuffer slice();
    
    ChannelBuffer slice(final int p0, final int p1);
    
    ChannelBuffer duplicate();
    
    ByteBuffer toByteBuffer();
    
    ByteBuffer toByteBuffer(final int p0, final int p1);
    
    ByteBuffer[] toByteBuffers();
    
    ByteBuffer[] toByteBuffers(final int p0, final int p1);
    
    boolean hasArray();
    
    byte[] array();
    
    int arrayOffset();
    
    String toString(final Charset p0);
    
    String toString(final int p0, final int p1, final Charset p2);
    
    int hashCode();
    
    boolean equals(final Object p0);
    
    int compareTo(final ChannelBuffer p0);
    
    String toString();
}
