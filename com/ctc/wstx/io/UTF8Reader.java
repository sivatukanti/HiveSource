// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import com.ctc.wstx.api.ReaderConfig;

public final class UTF8Reader extends BaseReader
{
    boolean mXml11;
    char mSurrogate;
    int mCharCount;
    int mByteCount;
    
    public UTF8Reader(final ReaderConfig cfg, final InputStream in, final byte[] buf, final int ptr, final int len, final boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mSurrogate = '\0';
        this.mCharCount = 0;
        this.mByteCount = 0;
    }
    
    @Override
    public void setXmlCompliancy(final int xmlVersion) {
        this.mXml11 = (xmlVersion == 272);
    }
    
    @Override
    public int read(final char[] cbuf, final int start, int len) throws IOException {
        if (start < 0 || start + len > cbuf.length) {
            this.reportBounds(cbuf, start, len);
        }
        if (this.mByteBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return 0;
        }
        len += start;
        int outPtr = start;
        if (this.mSurrogate != '\0') {
            cbuf[outPtr++] = this.mSurrogate;
            this.mSurrogate = '\0';
        }
        else {
            final int left = this.mByteBufferEnd - this.mBytePtr;
            if (left < 4 && (left < 1 || this.mByteBuffer[this.mBytePtr] < 0) && !this.loadMore(left)) {
                return -1;
            }
        }
        final byte[] buf = this.mByteBuffer;
        int inPtr = this.mBytePtr;
        final int inBufLen = this.mByteBufferEnd;
    Label_0132:
        while (outPtr < len) {
            int c = buf[inPtr++];
            Label_0354: {
                if (c >= 0) {
                    if (c == 127 && this.mXml11) {
                        final int bytePos = this.mByteCount + inPtr - 1;
                        final int charPos = this.mCharCount + (outPtr - start);
                        this.reportInvalidXml11(c, bytePos, charPos);
                    }
                    cbuf[outPtr++] = (char)c;
                    final int outMax = len - outPtr;
                    final int inMax = inBufLen - inPtr;
                    final int inEnd = inPtr + ((inMax < outMax) ? inMax : outMax);
                    while (inPtr < inEnd) {
                        c = (buf[inPtr++] & 0xFF);
                        if (c >= 127) {
                            if (c != 127) {
                                break Label_0354;
                            }
                            if (this.mXml11) {
                                final int bytePos2 = this.mByteCount + inPtr - 1;
                                final int charPos2 = this.mCharCount + (outPtr - start);
                                this.reportInvalidXml11(c, bytePos2, charPos2);
                            }
                            cbuf[outPtr++] = (char)c;
                            if (inPtr >= inEnd) {
                                break;
                            }
                            continue Label_0132;
                        }
                        else {
                            cbuf[outPtr++] = (char)c;
                        }
                    }
                    break;
                }
            }
            int needed;
            if ((c & 0xE0) == 0xC0) {
                c &= 0x1F;
                needed = 1;
            }
            else if ((c & 0xF0) == 0xE0) {
                c &= 0xF;
                needed = 2;
            }
            else if ((c & 0xF8) == 0xF0) {
                c &= 0xF;
                needed = 3;
            }
            else {
                this.reportInvalidInitial(c & 0xFF, outPtr - start);
                needed = 1;
            }
            if (inBufLen - inPtr < needed) {
                --inPtr;
                break;
            }
            int d = buf[inPtr++];
            if ((d & 0xC0) != 0x80) {
                this.reportInvalidOther(d & 0xFF, outPtr - start);
            }
            c = (c << 6 | (d & 0x3F));
            if (needed > 1) {
                d = buf[inPtr++];
                if ((d & 0xC0) != 0x80) {
                    this.reportInvalidOther(d & 0xFF, outPtr - start);
                }
                c = (c << 6 | (d & 0x3F));
                if (needed > 2) {
                    d = buf[inPtr++];
                    if ((d & 0xC0) != 0x80) {
                        this.reportInvalidOther(d & 0xFF, outPtr - start);
                    }
                    c = (c << 6 | (d & 0x3F));
                    if (c > 1114111) {
                        this.reportInvalid(c, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
                    }
                    c -= 65536;
                    cbuf[outPtr++] = (char)(55296 + (c >> 10));
                    c = (0xDC00 | (c & 0x3FF));
                    if (outPtr >= len) {
                        this.mSurrogate = (char)c;
                        break;
                    }
                }
                else if (c >= 55296) {
                    if (c < 57344) {
                        this.reportInvalid(c, outPtr - start, "(a surrogate character) ");
                    }
                    else if (c >= 65534) {
                        this.reportInvalid(c, outPtr - start, "");
                    }
                }
                else if (this.mXml11 && c == 8232) {
                    if (outPtr > start && cbuf[outPtr - 1] == '\r') {
                        cbuf[outPtr - 1] = '\n';
                    }
                    c = 10;
                }
            }
            else if (this.mXml11 && c <= 159) {
                if (c == 133) {
                    c = 10;
                }
                else if (c >= 127) {
                    final int bytePos3 = this.mByteCount + inPtr - 1;
                    final int charPos3 = this.mCharCount + (outPtr - start);
                    this.reportInvalidXml11(c, bytePos3, charPos3);
                }
            }
            cbuf[outPtr++] = (char)c;
            if (inPtr >= inBufLen) {
                break;
            }
        }
        this.mBytePtr = inPtr;
        len = outPtr - start;
        this.mCharCount += len;
        return len;
    }
    
    private void reportInvalidInitial(final int mask, final int offset) throws IOException {
        final int bytePos = this.mByteCount + this.mBytePtr - 1;
        final int charPos = this.mCharCount + offset + 1;
        throw new CharConversionException("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private void reportInvalidOther(final int mask, final int offset) throws IOException {
        final int bytePos = this.mByteCount + this.mBytePtr - 1;
        final int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private void reportUnexpectedEOF(final int gotBytes, final int needed) throws IOException {
        final int bytePos = this.mByteCount + gotBytes;
        final int charPos = this.mCharCount;
        throw new CharConversionException("Unexpected EOF in the middle of a multi-byte char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private void reportInvalid(final int value, final int offset, final String msg) throws IOException {
        final int bytePos = this.mByteCount + this.mBytePtr - 1;
        final int charPos = this.mCharCount + offset;
        throw new CharConversionException("Invalid UTF-8 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
    }
    
    private boolean loadMore(final int available) throws IOException {
        this.mByteCount += this.mByteBufferEnd - available;
        if (available > 0) {
            if (this.mBytePtr > 0 && this.canModifyBuffer()) {
                for (int i = 0; i < available; ++i) {
                    this.mByteBuffer[i] = this.mByteBuffer[this.mBytePtr + i];
                }
                this.mBytePtr = 0;
                this.mByteBufferEnd = available;
            }
        }
        else {
            final int count = this.readBytes();
            if (count < 1) {
                if (count < 0) {
                    this.freeBuffers();
                    return false;
                }
                this.reportStrangeStream();
            }
        }
        final int c = this.mByteBuffer[this.mBytePtr];
        if (c >= 0) {
            return true;
        }
        int needed;
        if ((c & 0xE0) == 0xC0) {
            needed = 2;
        }
        else if ((c & 0xF0) == 0xE0) {
            needed = 3;
        }
        else if ((c & 0xF8) == 0xF0) {
            needed = 4;
        }
        else {
            this.reportInvalidInitial(c & 0xFF, 0);
            needed = 1;
        }
        while (this.mBytePtr + needed > this.mByteBufferEnd) {
            final int count2 = this.readBytesAt(this.mByteBufferEnd);
            if (count2 < 1) {
                if (count2 < 0) {
                    this.freeBuffers();
                    this.reportUnexpectedEOF(this.mByteBufferEnd, needed);
                }
                this.reportStrangeStream();
            }
        }
        return true;
    }
}
