// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.IOException;
import java.io.OutputStream;
import com.ctc.wstx.api.WriterConfig;
import java.io.Writer;

public final class UTF8Writer extends Writer implements CompletelyCloseable
{
    private static final int DEFAULT_BUF_LEN = 4000;
    static final int SURR1_FIRST = 55296;
    static final int SURR1_LAST = 56319;
    static final int SURR2_FIRST = 56320;
    static final int SURR2_LAST = 57343;
    final WriterConfig mConfig;
    final boolean mAutoCloseOutput;
    final OutputStream mOut;
    byte[] mOutBuffer;
    final int mOutBufferLast;
    int mOutPtr;
    int mSurrogate;
    
    public UTF8Writer(final WriterConfig cfg, final OutputStream out, final boolean autoclose) {
        this.mSurrogate = 0;
        this.mConfig = cfg;
        this.mAutoCloseOutput = autoclose;
        this.mOut = out;
        this.mOutBuffer = ((this.mConfig == null) ? new byte[4000] : cfg.allocFullBBuffer(4000));
        this.mOutBufferLast = this.mOutBuffer.length - 4;
        this.mOutPtr = 0;
    }
    
    @Override
    public void closeCompletely() throws IOException {
        this._close(true);
    }
    
    @Override
    public void close() throws IOException {
        this._close(this.mAutoCloseOutput);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.mOutPtr > 0 && this.mOutBuffer != null) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        this.mOut.flush();
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }
    
    @Override
    public void write(final char[] cbuf, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                this.write(cbuf[off]);
            }
            return;
        }
        if (this.mSurrogate > 0) {
            final char second = cbuf[off++];
            --len;
            this.write(this._convertSurrogate(second));
        }
        int outPtr = this.mOutPtr;
        final byte[] outBuf = this.mOutBuffer;
        final int outBufLast = this.mOutBufferLast;
        len += off;
        while (off < len) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            int c = cbuf[off++];
            Label_0193: {
                if (c < 128) {
                    outBuf[outPtr++] = (byte)c;
                    int maxInCount = len - off;
                    final int maxOutCount = outBufLast - outPtr;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    maxInCount += off;
                    while (off < maxInCount) {
                        c = cbuf[off++];
                        if (c >= 128) {
                            break Label_0193;
                        }
                        outBuf[outPtr++] = (byte)c;
                    }
                    continue;
                }
            }
            if (c < 2048) {
                outBuf[outPtr++] = (byte)(0xC0 | c >> 6);
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
            else if (c < 55296 || c > 57343) {
                outBuf[outPtr++] = (byte)(0xE0 | c >> 12);
                outBuf[outPtr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
            else {
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len) {
                    break;
                }
                c = this._convertSurrogate(cbuf[off++]);
                if (c > 1114111) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                outBuf[outPtr++] = (byte)(0xF0 | c >> 18);
                outBuf[outPtr++] = (byte)(0x80 | (c >> 12 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
        }
        this.mOutPtr = outPtr;
    }
    
    @Override
    public void write(int c) throws IOException {
        if (this.mSurrogate > 0) {
            c = this._convertSurrogate(c);
        }
        else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
                this.throwIllegal(c);
            }
            this.mSurrogate = c;
            return;
        }
        if (this.mOutPtr >= this.mOutBufferLast) {
            this.mOut.write(this.mOutBuffer, 0, this.mOutPtr);
            this.mOutPtr = 0;
        }
        if (c < 128) {
            this.mOutBuffer[this.mOutPtr++] = (byte)c;
        }
        else {
            int ptr = this.mOutPtr;
            if (c < 2048) {
                this.mOutBuffer[ptr++] = (byte)(0xC0 | c >> 6);
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c & 0x3F));
            }
            else if (c <= 65535) {
                this.mOutBuffer[ptr++] = (byte)(0xE0 | c >> 12);
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c & 0x3F));
            }
            else {
                if (c > 1114111) {
                    this.throwIllegal(c);
                }
                this.mOutBuffer[ptr++] = (byte)(0xF0 | c >> 18);
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c >> 12 & 0x3F));
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                this.mOutBuffer[ptr++] = (byte)(0x80 | (c & 0x3F));
            }
            this.mOutPtr = ptr;
        }
    }
    
    @Override
    public void write(final String str) throws IOException {
        this.write(str, 0, str.length());
    }
    
    @Override
    public void write(final String str, int off, int len) throws IOException {
        if (len < 2) {
            if (len == 1) {
                this.write(str.charAt(off));
            }
            return;
        }
        if (this.mSurrogate > 0) {
            final char second = str.charAt(off++);
            --len;
            this.write(this._convertSurrogate(second));
        }
        int outPtr = this.mOutPtr;
        final byte[] outBuf = this.mOutBuffer;
        final int outBufLast = this.mOutBufferLast;
        len += off;
        while (off < len) {
            if (outPtr >= outBufLast) {
                this.mOut.write(outBuf, 0, outPtr);
                outPtr = 0;
            }
            int c = str.charAt(off++);
            Label_0201: {
                if (c < 128) {
                    outBuf[outPtr++] = (byte)c;
                    int maxInCount = len - off;
                    final int maxOutCount = outBufLast - outPtr;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    maxInCount += off;
                    while (off < maxInCount) {
                        c = str.charAt(off++);
                        if (c >= 128) {
                            break Label_0201;
                        }
                        outBuf[outPtr++] = (byte)c;
                    }
                    continue;
                }
            }
            if (c < 2048) {
                outBuf[outPtr++] = (byte)(0xC0 | c >> 6);
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
            else if (c < 55296 || c > 57343) {
                outBuf[outPtr++] = (byte)(0xE0 | c >> 12);
                outBuf[outPtr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
            else {
                if (c > 56319) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                this.mSurrogate = c;
                if (off >= len) {
                    break;
                }
                c = this._convertSurrogate(str.charAt(off++));
                if (c > 1114111) {
                    this.mOutPtr = outPtr;
                    this.throwIllegal(c);
                }
                outBuf[outPtr++] = (byte)(0xF0 | c >> 18);
                outBuf[outPtr++] = (byte)(0x80 | (c >> 12 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c >> 6 & 0x3F));
                outBuf[outPtr++] = (byte)(0x80 | (c & 0x3F));
            }
        }
        this.mOutPtr = outPtr;
    }
    
    private final void _close(final boolean forceClosing) throws IOException {
        final byte[] buf = this.mOutBuffer;
        if (buf != null) {
            this.mOutBuffer = null;
            if (this.mOutPtr > 0) {
                this.mOut.write(buf, 0, this.mOutPtr);
                this.mOutPtr = 0;
            }
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(buf);
            }
        }
        if (forceClosing) {
            this.mOut.close();
        }
        final int code = this.mSurrogate;
        if (code > 0) {
            this.mSurrogate = 0;
            this.throwIllegal(code);
        }
    }
    
    private final int _convertSurrogate(final int secondPart) throws IOException {
        final int firstPart = this.mSurrogate;
        this.mSurrogate = 0;
        if (secondPart < 56320 || secondPart > 57343) {
            throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
        }
        return 65536 + (firstPart - 55296 << 10) + (secondPart - 56320);
    }
    
    private void throwIllegal(final int code) throws IOException {
        if (code > 1114111) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 3629");
        }
        if (code < 55296) {
            throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
        }
        if (code <= 56319) {
            throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
        }
        throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
    }
}
