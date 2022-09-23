// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.IOException;
import java.io.InputStream;
import com.ctc.wstx.api.ReaderConfig;

public final class ISOLatinReader extends BaseReader
{
    boolean mXml11;
    int mByteCount;
    
    public ISOLatinReader(final ReaderConfig cfg, final InputStream in, final byte[] buf, final int ptr, final int len, final boolean recycleBuffer) {
        super(cfg, in, buf, ptr, len, recycleBuffer);
        this.mXml11 = false;
        this.mByteCount = 0;
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
            this.mByteCount += this.mByteBufferEnd;
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
        if (this.mXml11) {
            while (i < last) {
                char c = (char)(this.mByteBuffer[i++] & 0xFF);
                if (c >= '\u007f' && c <= '\u009f') {
                    if (c == '\u0085') {
                        c = '\n';
                    }
                    else if (c >= '\u007f') {
                        final int pos = this.mByteCount + i;
                        this.reportInvalidXml11(c, pos, pos);
                    }
                }
                cbuf[start++] = c;
            }
        }
        else {
            while (i < last) {
                cbuf[start++] = (char)(this.mByteBuffer[i++] & 0xFF);
            }
        }
        this.mBytePtr = last;
        return len;
    }
}
