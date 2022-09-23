// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.ctc.wstx.api.WriterConfig;
import java.io.OutputStream;

public final class AsciiXmlWriter extends EncodingXmlWriter
{
    public AsciiXmlWriter(final OutputStream out, final WriterConfig cfg, final boolean autoclose) throws IOException {
        super(out, cfg, "US-ASCII", autoclose);
    }
    
    @Override
    public void writeRaw(final char[] cbuf, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                for (int inEnd = offset + max; offset < inEnd; ++offset) {
                    int c = cbuf[offset];
                    if (c < 32) {
                        if (c != 10) {
                            if (c != 13) {
                                if (c != 9) {
                                    this.mOutputPtr = ptr;
                                    c = this.handleInvalidChar(c);
                                }
                            }
                        }
                    }
                    else if (c > 126) {
                        this.mOutputPtr = ptr;
                        if (c > 127) {
                            this.handleInvalidAsciiChar(c);
                        }
                        else if (this.mXml11) {
                            c = this.handleInvalidChar(c);
                        }
                    }
                    this.mOutputBuffer[ptr++] = (byte)c;
                }
            }
            else {
                for (int inEnd = offset + max; offset < inEnd; ++offset) {
                    this.mOutputBuffer[ptr++] = (byte)cbuf[offset];
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }
    
    @Override
    public void writeRaw(final String str, int offset, int len) throws IOException {
        if (this.mSurrogate != 0) {
            this.throwUnpairedSurrogate();
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            if (this.mCheckContent) {
                for (int inEnd = offset + max; offset < inEnd; ++offset) {
                    int c = str.charAt(offset);
                    if (c < 32) {
                        if (c != 10) {
                            if (c != 13) {
                                if (c != 9) {
                                    this.mOutputPtr = ptr;
                                    c = this.handleInvalidChar(c);
                                }
                            }
                        }
                    }
                    else if (c > 126) {
                        this.mOutputPtr = ptr;
                        if (c > 127) {
                            this.handleInvalidAsciiChar(c);
                        }
                        else if (this.mXml11) {
                            c = this.handleInvalidChar(c);
                        }
                    }
                    this.mOutputBuffer[ptr++] = (byte)c;
                }
            }
            else {
                for (int inEnd = offset + max; offset < inEnd; ++offset) {
                    this.mOutputBuffer[ptr++] = (byte)str.charAt(offset);
                }
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
    }
    
    @Override
    protected void writeAttrValue(final String data) throws IOException {
        int offset = 0;
        int len = data.length();
        int ptr = this.mOutputPtr;
    Label_0013:
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data.charAt(offset++);
                sec = this.calcSurrogate(sec);
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(sec);
                --len;
            }
            else {
                if (max > len) {
                    max = len;
                }
                final int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = data.charAt(offset++);
                    if (c < 32) {
                        if (c == 13) {
                            if (!this.mEscapeCR) {
                                this.mOutputBuffer[ptr++] = (byte)c;
                                continue;
                            }
                        }
                        else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                            c = this.handleInvalidChar(c);
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c < 127) {
                        if (c != 60 && c != 38 && c != 34) {
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c >= 55296 && c <= 57343) {
                        this.mSurrogate = c;
                        if (offset == inEnd) {
                            break;
                        }
                        c = this.calcSurrogate(data.charAt(offset++));
                    }
                    this.mOutputPtr = ptr;
                    ptr = this.writeAsEntity(c);
                    len = data.length() - offset;
                    continue Label_0013;
                }
                len -= max;
            }
        }
        this.mOutputPtr = ptr;
    }
    
    @Override
    protected void writeAttrValue(final char[] data, int offset, int len) throws IOException {
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data[offset++];
                sec = this.calcSurrogate(sec);
                this.mOutputPtr = ptr;
                ptr = this.writeAsEntity(sec);
                --len;
            }
            else {
                if (max > len) {
                    max = len;
                }
                final int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = data[offset++];
                    if (c < 32) {
                        if (c == 13) {
                            if (!this.mEscapeCR) {
                                this.mOutputBuffer[ptr++] = (byte)c;
                                continue;
                            }
                        }
                        else if (c != 10 && c != 9 && this.mCheckContent && (!this.mXml11 || c == 0)) {
                            c = this.handleInvalidChar(c);
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c < 127) {
                        if (c != 60 && c != 38 && c != 34) {
                            this.mOutputBuffer[ptr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c >= 55296 && c <= 57343) {
                        this.mSurrogate = c;
                        if (offset == inEnd) {
                            break;
                        }
                        c = this.calcSurrogate(data[offset++]);
                    }
                    this.mOutputPtr = ptr;
                    ptr = this.writeAsEntity(c);
                    max -= inEnd - offset;
                    break;
                }
                len -= max;
            }
        }
        this.mOutputPtr = ptr;
    }
    
    @Override
    protected int writeCDataContent(final String data) throws IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
    Label_0029:
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            final int inEnd = offset + max;
            while (offset < inEnd) {
                int c = data.charAt(offset++);
                if (c < 32) {
                    if (c != 10) {
                        if (c != 13) {
                            if (c != 9) {
                                this.mOutputPtr = ptr;
                                c = this.handleInvalidChar(c);
                            }
                        }
                    }
                }
                else if (c > 126) {
                    this.mOutputPtr = ptr;
                    if (c > 127) {
                        this.handleInvalidAsciiChar(c);
                    }
                    else if (this.mXml11) {
                        c = this.handleInvalidChar(c);
                    }
                }
                else if (c == 62 && offset > 2 && data.charAt(offset - 2) == ']' && data.charAt(offset - 3) == ']') {
                    if (!this.mFixContent) {
                        return offset - 3;
                    }
                    this.mOutputPtr = ptr;
                    this.writeCDataEnd();
                    this.writeCDataStart();
                    this.writeAscii((byte)62);
                    ptr = this.mOutputPtr;
                    len = data.length() - offset;
                    continue Label_0029;
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }
    
    @Override
    protected int writeCDataContent(final char[] cbuf, final int start, int len) throws IOException {
        if (!this.mCheckContent) {
            this.writeRaw(cbuf, start, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        int offset = start;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            final int inEnd = offset + max;
            while (offset < inEnd) {
                int c = cbuf[offset++];
                if (c < 32) {
                    if (c != 10) {
                        if (c != 13) {
                            if (c != 9) {
                                this.mOutputPtr = ptr;
                                c = this.handleInvalidChar(c);
                            }
                        }
                    }
                }
                else if (c > 126) {
                    this.mOutputPtr = ptr;
                    if (c > 127) {
                        this.handleInvalidAsciiChar(c);
                    }
                    else if (this.mXml11) {
                        c = this.handleInvalidChar(c);
                    }
                }
                else if (c == 62 && offset >= start + 3 && cbuf[offset - 2] == ']' && cbuf[offset - 3] == ']') {
                    if (!this.mFixContent) {
                        return offset - 3;
                    }
                    this.mOutputPtr = ptr;
                    this.writeCDataEnd();
                    this.writeCDataStart();
                    this.writeAscii((byte)62);
                    ptr = this.mOutputPtr;
                    max -= inEnd - offset;
                    break;
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }
    
    @Override
    protected int writeCommentContent(final String data) throws IOException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            final int inEnd = offset + max;
            while (offset < inEnd) {
                int c = data.charAt(offset++);
                if (c < 32) {
                    if (c != 10) {
                        if (c != 13) {
                            if (c != 9) {
                                this.mOutputPtr = ptr;
                                c = this.handleInvalidChar(c);
                            }
                        }
                    }
                }
                else if (c > 126) {
                    this.mOutputPtr = ptr;
                    if (c > 127) {
                        this.handleInvalidAsciiChar(c);
                    }
                    else if (this.mXml11) {
                        c = this.handleInvalidChar(c);
                    }
                }
                else if (c == 45 && offset > 1 && data.charAt(offset - 2) == '-') {
                    if (!this.mFixContent) {
                        return offset - 2;
                    }
                    this.mOutputBuffer[ptr++] = 32;
                    if (ptr >= this.mOutputBuffer.length) {
                        this.mOutputPtr = ptr;
                        this.flushBuffer();
                        ptr = 0;
                    }
                    this.mOutputBuffer[ptr++] = 45;
                    max -= inEnd - offset;
                    break;
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }
    
    @Override
    protected int writePIData(final String data) throws IOException, XMLStreamException {
        int offset = 0;
        int len = data.length();
        if (!this.mCheckContent) {
            this.writeRaw(data, offset, len);
            return -1;
        }
        int ptr = this.mOutputPtr;
        while (len > 0) {
            int max = this.mOutputBuffer.length - ptr;
            if (max < 1) {
                this.mOutputPtr = ptr;
                this.flushBuffer();
                ptr = 0;
                max = this.mOutputBuffer.length;
            }
            if (max > len) {
                max = len;
            }
            for (int inEnd = offset + max; offset < inEnd; ++offset) {
                int c = data.charAt(offset);
                if (c < 32) {
                    if (c != 10) {
                        if (c != 13) {
                            if (c != 9) {
                                this.mOutputPtr = ptr;
                                c = this.handleInvalidChar(c);
                            }
                        }
                    }
                }
                else if (c > 126) {
                    this.mOutputPtr = ptr;
                    if (c > 127) {
                        this.handleInvalidAsciiChar(c);
                    }
                    else if (this.mXml11) {
                        c = this.handleInvalidChar(c);
                    }
                }
                else if (c == 62 && offset > 0 && data.charAt(offset - 1) == '?') {
                    return offset - 2;
                }
                this.mOutputBuffer[ptr++] = (byte)c;
            }
            len -= max;
        }
        this.mOutputPtr = ptr;
        return -1;
    }
    
    @Override
    protected void writeTextContent(final String data) throws IOException {
        int offset = 0;
        int len = data.length();
    Label_0007:
        while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                this.flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = data.charAt(offset++);
                sec = this.calcSurrogate(sec);
                this.writeAsEntity(sec);
                --len;
            }
            else {
                if (max > len) {
                    max = len;
                }
                final int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = data.charAt(offset++);
                    if (c < 32) {
                        if (c == 10 || c == 9) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                        if (c == 13) {
                            if (!this.mEscapeCR) {
                                this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                                continue;
                            }
                        }
                        else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                            c = this.handleInvalidChar(c);
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c < 127) {
                        if (c != 60 && c != 38 && (c != 62 || (offset > 1 && data.charAt(offset - 2) != ']'))) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c >= 55296 && c <= 57343) {
                        this.mSurrogate = c;
                        if (offset == inEnd) {
                            break;
                        }
                        c = this.calcSurrogate(data.charAt(offset++));
                    }
                    this.writeAsEntity(c);
                    len = data.length() - offset;
                    continue Label_0007;
                }
                len -= max;
            }
        }
    }
    
    @Override
    protected void writeTextContent(final char[] cbuf, int offset, int len) throws IOException {
        while (len > 0) {
            int max = this.mOutputBuffer.length - this.mOutputPtr;
            if (max < 1) {
                this.flushBuffer();
                max = this.mOutputBuffer.length;
            }
            if (this.mSurrogate != 0) {
                int sec = cbuf[offset++];
                sec = this.calcSurrogate(sec);
                this.writeAsEntity(sec);
                --len;
            }
            else {
                if (max > len) {
                    max = len;
                }
                final int inEnd = offset + max;
                while (offset < inEnd) {
                    int c = cbuf[offset++];
                    if (c < 32) {
                        if (c == 10 || c == 9) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                        if (c == 13) {
                            if (!this.mEscapeCR) {
                                this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                                continue;
                            }
                        }
                        else if ((!this.mXml11 || c == 0) && this.mCheckContent) {
                            c = this.handleInvalidChar(c);
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c < 127) {
                        if (c != 60 && c != 38 && (c != 62 || (offset > 1 && cbuf[offset - 2] != ']'))) {
                            this.mOutputBuffer[this.mOutputPtr++] = (byte)c;
                            continue;
                        }
                    }
                    else if (c >= 55296 && c <= 57343) {
                        this.mSurrogate = c;
                        if (offset == inEnd) {
                            break;
                        }
                        c = this.calcSurrogate(cbuf[offset++]);
                    }
                    this.writeAsEntity(c);
                    max -= inEnd - offset;
                    break;
                }
                len -= max;
            }
        }
    }
    
    protected void handleInvalidAsciiChar(final int c) throws IOException {
        this.flush();
        throw new IOException("Invalid XML character (0x" + Integer.toHexString(c) + "); can only be output using character entity when using US-ASCII encoding");
    }
}
