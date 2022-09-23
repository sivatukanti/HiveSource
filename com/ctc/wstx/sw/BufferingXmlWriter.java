// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.util.Arrays;
import com.ctc.wstx.io.CharsetNames;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.CompletelyCloseable;
import java.io.IOException;
import com.ctc.wstx.api.WriterConfig;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamConstants;

public final class BufferingXmlWriter extends XmlWriter implements XMLStreamConstants
{
    static final int DEFAULT_BUFFER_SIZE = 1000;
    static final int DEFAULT_SMALL_SIZE = 256;
    protected static final int HIGHEST_ENCODABLE_ATTR_CHAR = 60;
    protected static final int HIGHEST_ENCODABLE_TEXT_CHAR = 62;
    protected static final int[] QUOTABLE_TEXT_CHARS;
    protected final Writer mOut;
    protected char[] mOutputBuffer;
    protected final int mSmallWriteSize;
    protected int mOutputPtr;
    protected int mOutputBufLen;
    protected final OutputStream mUnderlyingStream;
    private final int mEncHighChar;
    final char mEncQuoteChar;
    final String mEncQuoteEntity;
    
    public BufferingXmlWriter(final Writer out, final WriterConfig cfg, final String enc, final boolean autoclose, final OutputStream outs, int bitsize) throws IOException {
        super(cfg, enc, autoclose);
        this.mOut = out;
        this.mOutputBuffer = cfg.allocFullCBuffer(1000);
        this.mOutputBufLen = this.mOutputBuffer.length;
        this.mSmallWriteSize = 256;
        this.mOutputPtr = 0;
        this.mUnderlyingStream = outs;
        this.mEncQuoteChar = '\"';
        this.mEncQuoteEntity = "&quot;";
        if (bitsize < 1) {
            bitsize = guessEncodingBitSize(enc);
        }
        this.mEncHighChar = ((bitsize < 16) ? (1 << bitsize) : 65534);
    }
    
    @Override
    protected int getOutputPtr() {
        return this.mOutputPtr;
    }
    
    @Override
    protected final OutputStream getOutputStream() {
        return this.mUnderlyingStream;
    }
    
    @Override
    protected final Writer getWriter() {
        return this.mOut;
    }
    
    @Override
    public void close(final boolean forceRealClose) throws IOException {
        this.flush();
        this.mTextWriter = null;
        this.mAttrValueWriter = null;
        final char[] buf = this.mOutputBuffer;
        if (buf != null) {
            this.mOutputBuffer = null;
            this.mConfig.freeFullCBuffer(buf);
        }
        if (forceRealClose || this.mAutoCloseOutput) {
            if (this.mOut instanceof CompletelyCloseable) {
                ((CompletelyCloseable)this.mOut).closeCompletely();
            }
            else {
                this.mOut.close();
            }
        }
    }
    
    @Override
    public final void flush() throws IOException {
        this.flushBuffer();
        this.mOut.flush();
    }
    
    @Override
    public void writeRaw(final char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len > this.mOutputBufLen) {
                this.flushBuffer();
            }
            System.arraycopy(cbuf, offset, this.mOutputBuffer, this.mOutputPtr, len);
            this.mOutputPtr += len;
            return;
        }
        final int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                final int needed = this.mSmallWriteSize - ptr;
                System.arraycopy(cbuf, offset, this.mOutputBuffer, ptr, needed);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            this.flushBuffer();
        }
        this.mOut.write(cbuf, offset, len);
    }
    
    @Override
    public final void writeRawAscii(final char[] cbuf, final int offset, final int len) throws IOException {
        this.writeRaw(cbuf, offset, len);
    }
    
    @Override
    public void writeRaw(final String str) throws IOException {
        if (this.mOut == null) {
            return;
        }
        final int len = str.length();
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                this.flushBuffer();
            }
            str.getChars(0, len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        this.writeRaw(str, 0, len);
    }
    
    @Override
    public void writeRaw(final String str, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (len < this.mSmallWriteSize) {
            if (this.mOutputPtr + len >= this.mOutputBufLen) {
                this.flushBuffer();
            }
            str.getChars(offset, offset + len, this.mOutputBuffer, this.mOutputPtr);
            this.mOutputPtr += len;
            return;
        }
        final int ptr = this.mOutputPtr;
        if (ptr > 0) {
            if (ptr < this.mSmallWriteSize) {
                final int needed = this.mSmallWriteSize - ptr;
                str.getChars(offset, offset + needed, this.mOutputBuffer, ptr);
                this.mOutputPtr = ptr + needed;
                len -= needed;
                offset += needed;
            }
            this.flushBuffer();
        }
        this.mOut.write(str, offset, len);
    }
    
    @Override
    public final void writeCDataStart() throws IOException {
        this.fastWriteRaw("<![CDATA[");
    }
    
    @Override
    public final void writeCDataEnd() throws IOException {
        this.fastWriteRaw("]]>");
    }
    
    @Override
    public final void writeCommentStart() throws IOException {
        this.fastWriteRaw("<!--");
    }
    
    @Override
    public final void writeCommentEnd() throws IOException {
        this.fastWriteRaw("-->");
    }
    
    @Override
    public final void writePIStart(final String target, final boolean addSpace) throws IOException {
        this.fastWriteRaw('<', '?');
        this.fastWriteRaw(target);
        if (addSpace) {
            this.fastWriteRaw(' ');
        }
    }
    
    @Override
    public final void writePIEnd() throws IOException {
        this.fastWriteRaw('?', '>');
    }
    
    @Override
    public int writeCData(final String data) throws IOException {
        if (this.mCheckContent) {
            final int ix = this.verifyCDataContent(data);
            if (ix >= 0) {
                if (!this.mFixContent) {
                    return ix;
                }
                this.writeSegmentedCData(data, ix);
                return -1;
            }
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(data, 0, data.length());
        this.fastWriteRaw("]]>");
        return -1;
    }
    
    @Override
    public int writeCData(final char[] cbuf, final int offset, final int len) throws IOException {
        if (this.mCheckContent) {
            final int ix = this.verifyCDataContent(cbuf, offset, len);
            if (ix >= 0) {
                if (!this.mFixContent) {
                    return ix;
                }
                this.writeSegmentedCData(cbuf, offset, len, ix);
                return -1;
            }
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(cbuf, offset, len);
        this.fastWriteRaw("]]>");
        return -1;
    }
    
    @Override
    public void writeCharacters(final String text) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(text);
            return;
        }
        int inPtr = 0;
        final int len = text.length();
        final int[] QC = BufferingXmlWriter.QUOTABLE_TEXT_CHARS;
        final int highChar = this.mEncHighChar;
        final int MAXQC = Math.min(QC.length, highChar);
    Label_0052:
        while (true) {
            String ent = null;
            while (inPtr < len) {
                char c = text.charAt(inPtr++);
                Label_0278: {
                    if (c < MAXQC) {
                        if (QC[c] != 0) {
                            if (c < ' ') {
                                if (c != ' ' && c != '\n' && c != '\t') {
                                    if (c == '\r') {
                                        if (this.mEscapeCR) {
                                            break Label_0278;
                                        }
                                    }
                                    else {
                                        if (this.mXml11 && c != '\0') {
                                            break Label_0278;
                                        }
                                        c = this.handleInvalidChar(c);
                                        ent = String.valueOf(c);
                                    }
                                }
                            }
                            else {
                                if (c == '<') {
                                    ent = "&lt;";
                                    break Label_0278;
                                }
                                if (c == '&') {
                                    ent = "&amp;";
                                    break Label_0278;
                                }
                                if (c == '>') {
                                    if (inPtr < 2 || text.charAt(inPtr - 2) == ']') {
                                        ent = "&gt;";
                                        break Label_0278;
                                    }
                                }
                                else if (c >= '\u007f') {
                                    break Label_0278;
                                }
                            }
                        }
                    }
                    else if (c >= highChar) {
                        break Label_0278;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                }
                else {
                    this.writeAsEntity(text.charAt(inPtr - 1));
                }
                continue Label_0052;
            }
            break;
        }
    }
    
    @Override
    public void writeCharacters(final char[] cbuf, int offset, int len) throws IOException {
        if (this.mOut == null) {
            return;
        }
        if (this.mTextWriter != null) {
            this.mTextWriter.write(cbuf, offset, len);
            return;
        }
        final int[] QC = BufferingXmlWriter.QUOTABLE_TEXT_CHARS;
        final int highChar = this.mEncHighChar;
        final int MAXQC = Math.min(QC.length, highChar);
        len += offset;
        do {
            int c = 0;
            final int start = offset;
            String ent = null;
            while (offset < len) {
                c = cbuf[offset];
                if (c < MAXQC) {
                    if (QC[c] != 0) {
                        if (c == 60) {
                            ent = "&lt;";
                            break;
                        }
                        if (c == 38) {
                            ent = "&amp;";
                            break;
                        }
                        if (c == 62) {
                            if (offset == start || cbuf[offset - 1] == ']') {
                                ent = "&gt;";
                                break;
                            }
                        }
                        else if (c < 32) {
                            if (c != 10) {
                                if (c != 9) {
                                    if (c == 13) {
                                        if (this.mEscapeCR) {
                                            break;
                                        }
                                    }
                                    else {
                                        if (!this.mXml11 || c == 0) {
                                            c = this.handleInvalidChar(c);
                                            ent = String.valueOf((char)c);
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        else if (c >= 127) {
                            break;
                        }
                    }
                }
                else if (c >= highChar) {
                    break;
                }
                ++offset;
            }
            final int outLen = offset - start;
            if (outLen > 0) {
                this.writeRaw(cbuf, start, outLen);
            }
            if (ent != null) {
                this.writeRaw(ent);
                ent = null;
            }
            else {
                if (offset >= len) {
                    continue;
                }
                this.writeAsEntity(c);
            }
        } while (++offset < len);
    }
    
    @Override
    public int writeComment(final String data) throws IOException {
        if (this.mCheckContent) {
            final int ix = this.verifyCommentContent(data);
            if (ix >= 0) {
                if (!this.mFixContent) {
                    return ix;
                }
                this.writeSegmentedComment(data, ix);
                return -1;
            }
        }
        this.fastWriteRaw("<!--");
        this.writeRaw(data);
        this.fastWriteRaw("-->");
        return -1;
    }
    
    @Override
    public void writeDTD(final String data) throws IOException {
        this.writeRaw(data);
    }
    
    @Override
    public void writeDTD(final String rootName, final String systemId, final String publicId, final String internalSubset) throws IOException, XMLStreamException {
        this.fastWriteRaw("<!DOCTYPE ");
        if (this.mCheckNames) {
            this.verifyNameValidity(rootName, false);
        }
        this.fastWriteRaw(rootName);
        if (systemId != null) {
            if (publicId != null) {
                this.fastWriteRaw(" PUBLIC \"");
                this.fastWriteRaw(publicId);
                this.fastWriteRaw("\" \"");
            }
            else {
                this.fastWriteRaw(" SYSTEM \"");
            }
            this.fastWriteRaw(systemId);
            this.fastWriteRaw('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            this.fastWriteRaw(' ', '[');
            this.fastWriteRaw(internalSubset);
            this.fastWriteRaw(']');
        }
        this.fastWriteRaw('>');
    }
    
    @Override
    public void writeEntityReference(final String name) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(name, this.mNsAware);
        }
        this.fastWriteRaw('&');
        this.fastWriteRaw(name);
        this.fastWriteRaw(';');
    }
    
    @Override
    public void writeXmlDeclaration(final String version, final String encoding, final String standalone) throws IOException {
        final char chQuote = this.mUseDoubleQuotesInXmlDecl ? '\"' : '\'';
        this.fastWriteRaw("<?xml version=");
        this.fastWriteRaw(chQuote);
        this.fastWriteRaw(version);
        this.fastWriteRaw(chQuote);
        if (encoding != null && encoding.length() > 0) {
            this.fastWriteRaw(" encoding=");
            this.fastWriteRaw(chQuote);
            this.fastWriteRaw(encoding);
            this.fastWriteRaw(chQuote);
        }
        if (standalone != null) {
            this.fastWriteRaw(" standalone=");
            this.fastWriteRaw(chQuote);
            this.fastWriteRaw(standalone);
            this.fastWriteRaw(chQuote);
        }
        this.fastWriteRaw('?', '>');
    }
    
    @Override
    public int writePI(final String target, final String data) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(target, this.mNsAware);
        }
        this.fastWriteRaw('<', '?');
        this.fastWriteRaw(target);
        if (data != null && data.length() > 0) {
            if (this.mCheckContent) {
                int ix = data.indexOf(63);
                if (ix >= 0) {
                    ix = data.indexOf("?>", ix);
                    if (ix >= 0) {
                        return ix;
                    }
                }
            }
            this.fastWriteRaw(' ');
            this.writeRaw(data);
        }
        this.fastWriteRaw('?', '>');
        return -1;
    }
    
    @Override
    public void writeStartTagStart(final String localName) throws IOException, XMLStreamException {
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int ptr = this.mOutputPtr;
        final int extra = this.mOutputBufLen - ptr - (1 + localName.length());
        if (extra < 0) {
            this.fastWriteRaw('<');
            this.fastWriteRaw(localName);
        }
        else {
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = '<';
            final int len = localName.length();
            localName.getChars(0, len, buf, ptr);
            this.mOutputPtr = ptr + len;
        }
    }
    
    @Override
    public void writeStartTagStart(final String prefix, final String localName) throws IOException, XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeStartTagStart(localName);
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        final int extra = this.mOutputBufLen - ptr - (2 + localName.length() + len);
        if (extra < 0) {
            this.fastWriteRaw('<');
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
            this.fastWriteRaw(localName);
        }
        else {
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = '<';
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = ':';
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            this.mOutputPtr = ptr + len;
        }
    }
    
    @Override
    public void writeStartTagEnd() throws IOException {
        this.fastWriteRaw('>');
    }
    
    @Override
    public void writeStartTagEmptyEnd() throws IOException {
        int ptr = this.mOutputPtr;
        if (ptr + 3 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        final char[] buf = this.mOutputBuffer;
        if (this.mAddSpaceAfterEmptyElem) {
            buf[ptr++] = ' ';
        }
        buf[ptr++] = '/';
        buf[ptr++] = '>';
        this.mOutputPtr = ptr;
    }
    
    @Override
    public void writeEndTag(final String localName) throws IOException {
        int ptr = this.mOutputPtr;
        final int extra = this.mOutputBufLen - ptr - (3 + localName.length());
        if (extra < 0) {
            this.fastWriteRaw('<', '/');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('>');
        }
        else {
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = '<';
            buf[ptr++] = '/';
            final int len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '>';
            this.mOutputPtr = ptr;
        }
    }
    
    @Override
    public void writeEndTag(final String prefix, final String localName) throws IOException {
        if (prefix == null || prefix.length() == 0) {
            this.writeEndTag(localName);
            return;
        }
        int ptr = this.mOutputPtr;
        int len = prefix.length();
        final int extra = this.mOutputBufLen - ptr - (4 + localName.length() + len);
        if (extra < 0) {
            this.fastWriteRaw('<', '/');
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('>');
        }
        else {
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = '<';
            buf[ptr++] = '/';
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = ':';
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '>';
            this.mOutputPtr = ptr;
        }
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int len = localName.length();
        if (this.mOutputBufLen - this.mOutputPtr - (3 + len) < 0) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        len = ((value == null) ? 0 : value.length());
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            }
            else {
                this.writeAttrValue(value, len);
            }
        }
        this.fastWriteRaw('\"');
    }
    
    @Override
    public void writeAttribute(final String localName, final char[] value, final int offset, final int vlen) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        final int len = localName.length();
        if (this.mOutputBufLen - this.mOutputPtr - (3 + len) < 0) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            }
            else {
                this.writeAttrValue(value, offset, vlen);
            }
        }
        this.fastWriteRaw('\"');
    }
    
    @Override
    public void writeAttribute(final String prefix, final String localName, final String value) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + len) < 0) {
            this.fastWriteRaw(' ');
            if (len > 0) {
                this.fastWriteRaw(prefix);
                this.fastWriteRaw(':');
            }
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = ':';
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        len = ((value == null) ? 0 : value.length());
        if (len > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, 0, len);
            }
            else {
                this.writeAttrValue(value, len);
            }
        }
        this.fastWriteRaw('\"');
    }
    
    @Override
    public void writeAttribute(final String prefix, final String localName, final char[] value, final int offset, final int vlen) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        int len = prefix.length();
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + len) < 0) {
            this.fastWriteRaw(' ');
            if (len > 0) {
                this.fastWriteRaw(prefix);
                this.fastWriteRaw(':');
            }
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            prefix.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = ':';
            len = localName.length();
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        if (vlen > 0) {
            if (this.mAttrValueWriter != null) {
                this.mAttrValueWriter.write(value, offset, vlen);
            }
            else {
                this.writeAttrValue(value, offset, vlen);
            }
        }
        this.fastWriteRaw('\"');
    }
    
    private final void writeAttrValue(final String value, final int len) throws IOException {
        int inPtr = 0;
        final char qchar = this.mEncQuoteChar;
        final int highChar = this.mEncHighChar;
    Label_0014:
        while (true) {
            String ent = null;
            while (inPtr < len) {
                char c = value.charAt(inPtr++);
                Label_0193: {
                    if (c <= '<') {
                        if (c < ' ') {
                            if (c == '\r') {
                                if (this.mEscapeCR) {
                                    break Label_0193;
                                }
                            }
                            else {
                                if (c == '\n' || c == '\t' || (this.mXml11 && c != '\0')) {
                                    break Label_0193;
                                }
                                c = this.handleInvalidChar(c);
                            }
                        }
                        else {
                            if (c == qchar) {
                                ent = this.mEncQuoteEntity;
                                break Label_0193;
                            }
                            if (c == '<') {
                                ent = "&lt;";
                                break Label_0193;
                            }
                            if (c == '&') {
                                ent = "&amp;";
                                break Label_0193;
                            }
                        }
                    }
                    else if (c >= highChar) {
                        break Label_0193;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                }
                else {
                    this.writeAsEntity(value.charAt(inPtr - 1));
                }
                continue Label_0014;
            }
            break;
        }
    }
    
    private final void writeAttrValue(final char[] value, int offset, int len) throws IOException {
        len += offset;
        final char qchar = this.mEncQuoteChar;
        final int highChar = this.mEncHighChar;
    Label_0016:
        while (true) {
            String ent = null;
            while (offset < len) {
                char c = value[offset++];
                Label_0193: {
                    if (c <= '<') {
                        if (c < ' ') {
                            if (c == '\r') {
                                if (this.mEscapeCR) {
                                    break Label_0193;
                                }
                            }
                            else {
                                if (c == '\n' || c == '\t' || (this.mXml11 && c != '\0')) {
                                    break Label_0193;
                                }
                                c = this.handleInvalidChar(c);
                            }
                        }
                        else {
                            if (c == qchar) {
                                ent = this.mEncQuoteEntity;
                                break Label_0193;
                            }
                            if (c == '<') {
                                ent = "&lt;";
                                break Label_0193;
                            }
                            if (c == '&') {
                                ent = "&amp;";
                                break Label_0193;
                            }
                        }
                    }
                    else if (c >= highChar) {
                        break Label_0193;
                    }
                    if (this.mOutputPtr >= this.mOutputBufLen) {
                        this.flushBuffer();
                    }
                    this.mOutputBuffer[this.mOutputPtr++] = c;
                    continue;
                }
                if (ent != null) {
                    this.writeRaw(ent);
                }
                else {
                    this.writeAsEntity(value[offset - 1]);
                }
                continue Label_0016;
            }
            break;
        }
    }
    
    @Override
    public final void writeTypedElement(final AsciiValueEncoder enc) throws IOException {
        if (this.mOut == null) {
            return;
        }
        final int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) {
                break;
            }
            this.flush();
        }
    }
    
    @Override
    public final void writeTypedElement(final AsciiValueEncoder enc, final XMLValidator validator, final char[] copyBuffer) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        final int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        int start = this.mOutputPtr;
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            validator.validateText(this.mOutputBuffer, start, this.mOutputPtr, false);
            if (enc.isCompleted()) {
                break;
            }
            this.flush();
            start = this.mOutputPtr;
        }
    }
    
    @Override
    public void writeTypedAttribute(final String localName, final AsciiValueEncoder enc) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(localName, this.mNsAware);
        }
        final int len = localName.length();
        if (this.mOutputPtr + 3 + len > this.mOutputBufLen) {
            this.fastWriteRaw(' ');
            this.fastWriteRaw(localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            localName.getChars(0, len, buf, ptr);
            ptr += len;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        final int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) {
                break;
            }
            this.flush();
        }
        this.fastWriteRaw('\"');
    }
    
    @Override
    public void writeTypedAttribute(final String prefix, final String localName, final AsciiValueEncoder enc) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (this.mCheckNames) {
            this.verifyNameValidity(prefix, this.mNsAware);
            this.verifyNameValidity(localName, this.mNsAware);
        }
        final int plen = prefix.length();
        final int llen = localName.length();
        if (this.mOutputPtr + 4 + plen + llen > this.mOutputBufLen) {
            this.writePrefixedName(prefix, localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr);
                ptr += plen;
                buf[ptr++] = ':';
            }
            localName.getChars(0, llen, buf, ptr);
            ptr += llen;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        final int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        while (true) {
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            if (enc.isCompleted()) {
                break;
            }
            this.flush();
        }
        this.fastWriteRaw('\"');
    }
    
    @Override
    public void writeTypedAttribute(String prefix, final String localName, String nsURI, final AsciiValueEncoder enc, final XMLValidator validator, final char[] copyBuffer) throws IOException, XMLStreamException {
        if (this.mOut == null) {
            return;
        }
        if (prefix == null) {
            prefix = "";
        }
        if (nsURI == null) {
            nsURI = "";
        }
        final int plen = prefix.length();
        if (this.mCheckNames) {
            if (plen > 0) {
                this.verifyNameValidity(prefix, this.mNsAware);
            }
            this.verifyNameValidity(localName, this.mNsAware);
        }
        if (this.mOutputBufLen - this.mOutputPtr - (4 + localName.length() + plen) < 0) {
            this.writePrefixedName(prefix, localName);
            this.fastWriteRaw('=', '\"');
        }
        else {
            int ptr = this.mOutputPtr;
            final char[] buf = this.mOutputBuffer;
            buf[ptr++] = ' ';
            if (plen > 0) {
                prefix.getChars(0, plen, buf, ptr);
                ptr += plen;
                buf[ptr++] = ':';
            }
            final int llen = localName.length();
            localName.getChars(0, llen, buf, ptr);
            ptr += llen;
            buf[ptr++] = '=';
            buf[ptr++] = '\"';
            this.mOutputPtr = ptr;
        }
        final int free = this.mOutputBufLen - this.mOutputPtr;
        if (enc.bufferNeedsFlush(free)) {
            this.flush();
        }
        int start = this.mOutputPtr;
        this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
        if (enc.isCompleted()) {
            validator.validateAttribute(localName, nsURI, prefix, this.mOutputBuffer, start, this.mOutputPtr);
            return;
        }
        final StringBuilder sb = new StringBuilder(this.mOutputBuffer.length << 1);
        sb.append(this.mOutputBuffer, start, this.mOutputPtr - start);
        do {
            this.flush();
            start = this.mOutputPtr;
            this.mOutputPtr = enc.encodeMore(this.mOutputBuffer, this.mOutputPtr, this.mOutputBufLen);
            sb.append(this.mOutputBuffer, start, this.mOutputPtr - start);
        } while (!enc.isCompleted());
        this.fastWriteRaw('\"');
        final String valueStr = sb.toString();
        validator.validateAttribute(localName, nsURI, prefix, valueStr);
    }
    
    protected final void writePrefixedName(final String prefix, final String localName) throws IOException {
        this.fastWriteRaw(' ');
        if (prefix.length() > 0) {
            this.fastWriteRaw(prefix);
            this.fastWriteRaw(':');
        }
        this.fastWriteRaw(localName);
    }
    
    private final void flushBuffer() throws IOException {
        if (this.mOutputPtr > 0 && this.mOutputBuffer != null) {
            final int ptr = this.mOutputPtr;
            this.mLocPastChars += ptr;
            this.mLocRowStartOffset -= ptr;
            this.mOutputPtr = 0;
            this.mOut.write(this.mOutputBuffer, 0, ptr);
        }
    }
    
    private final void fastWriteRaw(final char c) throws IOException {
        if (this.mOutputPtr >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = c;
    }
    
    private final void fastWriteRaw(final char c1, final char c2) throws IOException {
        if (this.mOutputPtr + 1 >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            this.flushBuffer();
        }
        this.mOutputBuffer[this.mOutputPtr++] = c1;
        this.mOutputBuffer[this.mOutputPtr++] = c2;
    }
    
    private final void fastWriteRaw(final String str) throws IOException {
        final int len = str.length();
        int ptr = this.mOutputPtr;
        if (ptr + len >= this.mOutputBufLen) {
            if (this.mOut == null) {
                return;
            }
            if (len > this.mOutputBufLen) {
                this.writeRaw(str);
                return;
            }
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        str.getChars(0, len, this.mOutputBuffer, ptr);
        this.mOutputPtr = ptr + len;
    }
    
    protected int verifyCDataContent(final String content) {
        if (content != null && content.length() >= 3) {
            final int ix = content.indexOf(93);
            if (ix >= 0) {
                return content.indexOf("]]>", ix);
            }
        }
        return -1;
    }
    
    protected int verifyCDataContent(final char[] c, int start, final int end) {
        if (c != null) {
            start += 2;
            while (start < end) {
                final char ch = c[start];
                if (ch == ']') {
                    ++start;
                }
                else {
                    if (ch == '>' && c[start - 1] == ']' && c[start - 2] == ']') {
                        return start - 2;
                    }
                    start += 2;
                }
            }
        }
        return -1;
    }
    
    protected int verifyCommentContent(final String content) {
        int ix = content.indexOf(45);
        if (ix >= 0 && ix < content.length() - 1) {
            ix = content.indexOf("--", ix);
        }
        return ix;
    }
    
    protected void writeSegmentedCData(final String content, int index) throws IOException {
        int start;
        for (start = 0; index >= 0; index = content.indexOf("]]>", start)) {
            this.fastWriteRaw("<![CDATA[");
            this.writeRaw(content, start, index + 2 - start);
            this.fastWriteRaw("]]>");
            start = index + 2;
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(content, start, content.length() - start);
        this.fastWriteRaw("]]>");
    }
    
    protected void writeSegmentedCData(final char[] c, int start, final int len, int index) throws IOException {
        int end;
        for (end = start + len; index >= 0; index = this.verifyCDataContent(c, start, end)) {
            this.fastWriteRaw("<![CDATA[");
            this.writeRaw(c, start, index + 2 - start);
            this.fastWriteRaw("]]>");
            start = index + 2;
        }
        this.fastWriteRaw("<![CDATA[");
        this.writeRaw(c, start, end - start);
        this.fastWriteRaw("]]>");
    }
    
    protected void writeSegmentedComment(final String content, int index) throws IOException {
        final int len = content.length();
        if (index == len - 1) {
            this.fastWriteRaw("<!--");
            this.writeRaw(content);
            this.fastWriteRaw(" -->");
            return;
        }
        this.fastWriteRaw("<!--");
        int start;
        for (start = 0; index >= 0; index = content.indexOf("--", start)) {
            this.writeRaw(content, start, index + 1 - start);
            this.fastWriteRaw(' ');
            start = index + 1;
        }
        this.writeRaw(content, start, len - start);
        if (content.charAt(len - 1) == '-') {
            this.fastWriteRaw(' ');
        }
        this.fastWriteRaw("-->");
    }
    
    public static int guessEncodingBitSize(String enc) {
        if (enc == null || enc.length() == 0) {
            return 16;
        }
        enc = CharsetNames.normalize(enc);
        if (enc == "UTF-8") {
            return 16;
        }
        if (enc == "ISO-8859-1") {
            return 8;
        }
        if (enc == "US-ASCII") {
            return 7;
        }
        if (enc == "UTF-16" || enc == "UTF-16BE" || enc == "UTF-16LE" || enc == "UTF-32BE" || enc == "UTF-32LE") {
            return 16;
        }
        return 8;
    }
    
    protected final void writeAsEntity(int c) throws IOException {
        final char[] buf = this.mOutputBuffer;
        int ptr = this.mOutputPtr;
        if (ptr + 10 >= buf.length) {
            this.flushBuffer();
            ptr = this.mOutputPtr;
        }
        buf[ptr++] = '&';
        if (c < 256) {
            if (c == 38) {
                buf[ptr++] = 'a';
                buf[ptr++] = 'm';
                buf[ptr++] = 'p';
            }
            else if (c == 60) {
                buf[ptr++] = 'l';
                buf[ptr++] = 't';
            }
            else if (c == 62) {
                buf[ptr++] = 'g';
                buf[ptr++] = 't';
            }
            else if (c == 39) {
                buf[ptr++] = 'a';
                buf[ptr++] = 'p';
                buf[ptr++] = 'o';
                buf[ptr++] = 's';
            }
            else if (c == 34) {
                buf[ptr++] = 'q';
                buf[ptr++] = 'u';
                buf[ptr++] = 'o';
                buf[ptr++] = 't';
            }
            else {
                buf[ptr++] = '#';
                buf[ptr++] = 'x';
                if (c >= 16) {
                    final int digit = c >> 4;
                    buf[ptr++] = (char)((digit < 10) ? (48 + digit) : (87 + digit));
                    c &= 0xF;
                }
                buf[ptr++] = (char)((c < 10) ? (48 + c) : (87 + c));
            }
        }
        else {
            buf[ptr++] = '#';
            buf[ptr++] = 'x';
            int shift = 20;
            final int origPtr = ptr;
            do {
                final int digit2 = c >> shift & 0xF;
                if (digit2 > 0 || ptr != origPtr) {
                    buf[ptr++] = (char)((digit2 < 10) ? (48 + digit2) : (87 + digit2));
                }
                shift -= 4;
            } while (shift > 0);
            c &= 0xF;
            buf[ptr++] = (char)((c < 10) ? (48 + c) : (87 + c));
        }
        buf[ptr++] = ';';
        this.mOutputPtr = ptr;
    }
    
    static {
        final int[] q = new int[4096];
        Arrays.fill(q, 0, 32, 1);
        Arrays.fill(q, 127, 160, 1);
        q[10] = (q[9] = 0);
        q[60] = 1;
        q[38] = (q[62] = 1);
        QUOTABLE_TEXT_CHARS = q;
    }
}
