// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;

public final class UTF32Reader extends BaseReader
{
    final boolean mBigEndian;
    char mSurrogate;
    int mCharCount;
    int mByteCount;
    
    public UTF32Reader(final IOContext ctxt, final InputStream in, final byte[] buf, final int ptr, final int len, final boolean isBigEndian) {
        super(ctxt, in, buf, ptr, len);
        this.mSurrogate = '\0';
        this.mCharCount = 0;
        this.mByteCount = 0;
        this.mBigEndian = isBigEndian;
    }
    
    @Override
    public int read(final char[] cbuf, final int start, int len) throws IOException {
        if (this._buffer == null) {
            return -1;
        }
        if (len < 1) {
            return len;
        }
        if (start < 0 || start + len > cbuf.length) {
            this.reportBounds(cbuf, start, len);
        }
        len += start;
        int outPtr = start;
        if (this.mSurrogate != '\0') {
            cbuf[outPtr++] = this.mSurrogate;
            this.mSurrogate = '\0';
        }
        else {
            final int left = this._length - this._ptr;
            if (left < 4 && !this.loadMore(left)) {
                return -1;
            }
        }
        while (outPtr < len) {
            final int ptr = this._ptr;
            int ch;
            if (this.mBigEndian) {
                ch = (this._buffer[ptr] << 24 | (this._buffer[ptr + 1] & 0xFF) << 16 | (this._buffer[ptr + 2] & 0xFF) << 8 | (this._buffer[ptr + 3] & 0xFF));
            }
            else {
                ch = ((this._buffer[ptr] & 0xFF) | (this._buffer[ptr + 1] & 0xFF) << 8 | (this._buffer[ptr + 2] & 0xFF) << 16 | this._buffer[ptr + 3] << 24);
            }
            this._ptr += 4;
            if (ch > 65535) {
                if (ch > 1114111) {
                    this.reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                }
                ch -= 65536;
                cbuf[outPtr++] = (char)(55296 + (ch >> 10));
                ch = (0xDC00 | (ch & 0x3FF));
                if (outPtr >= len) {
                    this.mSurrogate = (char)ch;
                    break;
                }
            }
            cbuf[outPtr++] = (char)ch;
            if (this._ptr >= this._length) {
                break;
            }
        }
        len = outPtr - start;
        this.mCharCount += len;
        return len;
    }
    
    private void reportUnexpectedEOF(final int gotBytes, final int needed) throws IOException {
        final int bytePos = this.mByteCount + gotBytes;
        final int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private void reportInvalid(final int value, final int offset, final String msg) throws IOException {
        final int bytePos = this.mByteCount + this._ptr - 1;
        final int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private boolean loadMore(final int available) throws IOException {
        this.mByteCount += this._length - available;
        if (available > 0) {
            if (this._ptr > 0) {
                for (int i = 0; i < available; ++i) {
                    this._buffer[i] = this._buffer[this._ptr + i];
                }
                this._ptr = 0;
            }
            this._length = available;
        }
        else {
            this._ptr = 0;
            final int count = this._in.read(this._buffer);
            if (count < 1) {
                this._length = 0;
                if (count < 0) {
                    this.freeBuffers();
                    return false;
                }
                this.reportStrangeStream();
            }
            this._length = count;
        }
        while (this._length < 4) {
            final int count = this._in.read(this._buffer, this._length, this._buffer.length - this._length);
            if (count < 1) {
                if (count < 0) {
                    this.freeBuffers();
                    this.reportUnexpectedEOF(this._length, 4);
                }
                this.reportStrangeStream();
            }
            this._length += count;
        }
        return true;
    }
}
