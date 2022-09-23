// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import com.ctc.wstx.api.ReaderConfig;
import java.io.Reader;

abstract class BaseReader extends Reader
{
    protected static final char NULL_CHAR = '\0';
    protected static final char NULL_BYTE = '\0';
    protected static final char CONVERT_NEL_TO = '\n';
    protected static final char CONVERT_LSEP_TO = '\n';
    static final char CHAR_DEL = '\u007f';
    protected final ReaderConfig mConfig;
    private InputStream mIn;
    protected byte[] mByteBuffer;
    protected int mBytePtr;
    protected int mByteBufferEnd;
    private final boolean mRecycleBuffer;
    protected char[] mTmpBuf;
    
    protected BaseReader(final ReaderConfig cfg, final InputStream in, final byte[] buf, final int ptr, final int len, final boolean recycleBuffer) {
        this.mTmpBuf = null;
        this.mConfig = cfg;
        this.mIn = in;
        this.mByteBuffer = buf;
        this.mBytePtr = ptr;
        this.mByteBufferEnd = len;
        this.mRecycleBuffer = recycleBuffer;
    }
    
    public abstract void setXmlCompliancy(final int p0);
    
    protected final boolean canModifyBuffer() {
        return this.mRecycleBuffer;
    }
    
    @Override
    public void close() throws IOException {
        final InputStream in = this.mIn;
        if (in != null) {
            this.mIn = null;
            this.freeBuffers();
            in.close();
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.mTmpBuf == null) {
            this.mTmpBuf = new char[1];
        }
        if (this.read(this.mTmpBuf, 0, 1) < 1) {
            return -1;
        }
        return this.mTmpBuf[0];
    }
    
    protected final InputStream getStream() {
        return this.mIn;
    }
    
    protected final int readBytes() throws IOException {
        this.mBytePtr = 0;
        this.mByteBufferEnd = 0;
        if (this.mIn != null) {
            final int count = this.mIn.read(this.mByteBuffer, 0, this.mByteBuffer.length);
            if (count > 0) {
                this.mByteBufferEnd = count;
            }
            return count;
        }
        return -1;
    }
    
    protected final int readBytesAt(final int offset) throws IOException {
        if (this.mIn != null) {
            final int count = this.mIn.read(this.mByteBuffer, offset, this.mByteBuffer.length - offset);
            if (count > 0) {
                this.mByteBufferEnd += count;
            }
            return count;
        }
        return -1;
    }
    
    public final void freeBuffers() {
        if (this.mRecycleBuffer) {
            final byte[] buf = this.mByteBuffer;
            if (buf != null) {
                this.mByteBuffer = null;
                if (this.mConfig != null) {
                    this.mConfig.freeFullBBuffer(buf);
                }
            }
        }
    }
    
    protected void reportBounds(final char[] cbuf, final int start, final int len) throws IOException {
        throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
    }
    
    protected void reportStrangeStream() throws IOException {
        throw new IOException("Strange I/O stream, returned 0 bytes on read");
    }
    
    protected void reportInvalidXml11(final int value, final int bytePos, final int charPos) throws IOException {
        throw new CharConversionException("Invalid character 0x" + Integer.toHexString(value) + ", can only be included in xml 1.1 using character entities (at char #" + charPos + ", byte #" + bytePos + ")");
    }
}
