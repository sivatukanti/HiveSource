// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import com.ctc.wstx.api.ReaderConfig;

public final class AsciiReader extends BaseReader
{
    boolean mXml11;
    int mCharCount;
    
    public AsciiReader(final ReaderConfig cfg, final InputStream in, final byte[] buf, final int ptr, final int len, final boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mCharCount = 0;
    }
    
    @Override
    public void setXmlCompliancy(final int xmlVersion) {
        this.mXml11 = (xmlVersion == 272);
    }
    
    @Override
    public int read(final char[] cbuf, int start, int len) throws IOException {
        if (start < 0 || start + len > cbuf.length) {
            this.reportBounds(cbuf, start, len);
        }
        if (this.mByteBuffer == null) {
            return -1;
        }
        if (len < 1) {
            return 0;
        }
        int avail = this.mByteBufferEnd - this.mBytePtr;
        if (avail <= 0) {
            this.mCharCount += this.mByteBufferEnd;
            final int count = this.readBytes();
            if (count <= 0) {
                if (count == 0) {
                    this.reportStrangeStream();
                }
                this.freeBuffers();
                return -1;
            }
            avail = count;
        }
        if (len > avail) {
            len = avail;
        }
        int i = this.mBytePtr;
        final int last = i + len;
        while (i < last) {
            final char c = (char)this.mByteBuffer[i++];
            if (c >= '\u007f') {
                if (c > '\u007f') {
                    this.reportInvalidAscii(c);
                }
                else if (this.mXml11) {
                    final int pos = this.mCharCount + this.mBytePtr;
                    this.reportInvalidXml11(c, pos, pos);
                }
            }
            cbuf[start++] = c;
        }
        this.mBytePtr = last;
        return len;
    }
    
    private void reportInvalidAscii(final char c) throws IOException {
        throw new CharConversionException("Invalid ascii byte; value above 7-bit ascii range (" + (int)c + "; at pos #" + (this.mCharCount + this.mBytePtr) + ")");
    }
}
