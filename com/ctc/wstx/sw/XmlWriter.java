// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import com.ctc.wstx.api.InvalidCharHandler;
import java.text.MessageFormat;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.io.IOException;
import org.codehaus.stax2.io.EscapingWriterFactory;
import java.io.Writer;
import com.ctc.wstx.api.WriterConfig;

public abstract class XmlWriter
{
    protected static final int SURR1_FIRST = 55296;
    protected static final int SURR1_LAST = 56319;
    protected static final int SURR2_FIRST = 56320;
    protected static final int SURR2_LAST = 57343;
    protected static final char DEFAULT_QUOTE_CHAR = '\"';
    protected final WriterConfig mConfig;
    protected final String mEncoding;
    protected final boolean mNsAware;
    protected final boolean mCheckStructure;
    protected final boolean mCheckContent;
    protected final boolean mCheckNames;
    protected final boolean mFixContent;
    final boolean mEscapeCR;
    final boolean mAddSpaceAfterEmptyElem;
    final boolean mUseDoubleQuotesInXmlDecl;
    protected final boolean mAutoCloseOutput;
    protected Writer mTextWriter;
    protected Writer mAttrValueWriter;
    protected boolean mXml11;
    protected XmlWriterWrapper mRawWrapper;
    protected XmlWriterWrapper mTextWrapper;
    protected int mLocPastChars;
    protected int mLocRowNr;
    protected int mLocRowStartOffset;
    
    protected XmlWriter(final WriterConfig cfg, final String encoding, final boolean autoclose) throws IOException {
        this.mXml11 = false;
        this.mRawWrapper = null;
        this.mTextWrapper = null;
        this.mLocPastChars = 0;
        this.mLocRowNr = 1;
        this.mLocRowStartOffset = 0;
        this.mConfig = cfg;
        this.mEncoding = encoding;
        this.mAutoCloseOutput = autoclose;
        final int flags = cfg.getConfigFlags();
        this.mNsAware = ((flags & 0x1) != 0x0);
        this.mCheckStructure = ((flags & 0x100) != 0x0);
        this.mCheckContent = ((flags & 0x200) != 0x0);
        this.mCheckNames = ((flags & 0x400) != 0x0);
        this.mFixContent = ((flags & 0x1000) != 0x0);
        this.mEscapeCR = ((flags & 0x20) != 0x0);
        this.mAddSpaceAfterEmptyElem = ((flags & 0x40) != 0x0);
        this.mUseDoubleQuotesInXmlDecl = ((flags & 0x4000) != 0x0);
        EscapingWriterFactory f = this.mConfig.getTextEscaperFactory();
        if (f == null) {
            this.mTextWriter = null;
        }
        else {
            final String enc = (this.mEncoding == null || this.mEncoding.length() == 0) ? "UTF-8" : this.mEncoding;
            this.mTextWriter = f.createEscapingWriterFor(this.wrapAsRawWriter(), enc);
        }
        f = this.mConfig.getAttrValueEscaperFactory();
        if (f == null) {
            this.mAttrValueWriter = null;
        }
        else {
            final String enc = (this.mEncoding == null || this.mEncoding.length() == 0) ? "UTF-8" : this.mEncoding;
            this.mAttrValueWriter = f.createEscapingWriterFor(this.wrapAsRawWriter(), enc);
        }
    }
    
    public void enableXml11() {
        this.mXml11 = true;
    }
    
    protected abstract OutputStream getOutputStream();
    
    protected abstract Writer getWriter();
    
    public abstract void close(final boolean p0) throws IOException;
    
    public abstract void flush() throws IOException;
    
    public abstract void writeRaw(final String p0, final int p1, final int p2) throws IOException;
    
    public void writeRaw(final String str) throws IOException {
        this.writeRaw(str, 0, str.length());
    }
    
    public abstract void writeRaw(final char[] p0, final int p1, final int p2) throws IOException;
    
    public abstract void writeRawAscii(final char[] p0, final int p1, final int p2) throws IOException;
    
    public abstract void writeCDataStart() throws IOException;
    
    public abstract void writeCDataEnd() throws IOException;
    
    public abstract void writeCommentStart() throws IOException;
    
    public abstract void writeCommentEnd() throws IOException;
    
    public abstract void writePIStart(final String p0, final boolean p1) throws IOException;
    
    public abstract void writePIEnd() throws IOException;
    
    public abstract int writeCData(final String p0) throws IOException, XMLStreamException;
    
    public abstract int writeCData(final char[] p0, final int p1, final int p2) throws IOException, XMLStreamException;
    
    public abstract void writeCharacters(final String p0) throws IOException;
    
    public abstract void writeCharacters(final char[] p0, final int p1, final int p2) throws IOException;
    
    public abstract int writeComment(final String p0) throws IOException, XMLStreamException;
    
    public abstract void writeDTD(final String p0) throws IOException, XMLStreamException;
    
    public abstract void writeDTD(final String p0, final String p1, final String p2, final String p3) throws IOException, XMLStreamException;
    
    public abstract void writeEntityReference(final String p0) throws IOException, XMLStreamException;
    
    public abstract int writePI(final String p0, final String p1) throws IOException, XMLStreamException;
    
    public abstract void writeXmlDeclaration(final String p0, final String p1, final String p2) throws IOException;
    
    public abstract void writeStartTagStart(final String p0) throws IOException, XMLStreamException;
    
    public abstract void writeStartTagStart(final String p0, final String p1) throws IOException, XMLStreamException;
    
    public abstract void writeStartTagEnd() throws IOException;
    
    public abstract void writeStartTagEmptyEnd() throws IOException;
    
    public abstract void writeEndTag(final String p0) throws IOException;
    
    public abstract void writeEndTag(final String p0, final String p1) throws IOException;
    
    public abstract void writeAttribute(final String p0, final String p1) throws IOException, XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final char[] p1, final int p2, final int p3) throws IOException, XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final String p1, final String p2) throws IOException, XMLStreamException;
    
    public abstract void writeAttribute(final String p0, final String p1, final char[] p2, final int p3, final int p4) throws IOException, XMLStreamException;
    
    public abstract void writeTypedElement(final AsciiValueEncoder p0) throws IOException;
    
    public abstract void writeTypedElement(final AsciiValueEncoder p0, final XMLValidator p1, final char[] p2) throws IOException, XMLStreamException;
    
    public abstract void writeTypedAttribute(final String p0, final AsciiValueEncoder p1) throws IOException, XMLStreamException;
    
    public abstract void writeTypedAttribute(final String p0, final String p1, final AsciiValueEncoder p2) throws IOException, XMLStreamException;
    
    public abstract void writeTypedAttribute(final String p0, final String p1, final String p2, final AsciiValueEncoder p3, final XMLValidator p4, final char[] p5) throws IOException, XMLStreamException;
    
    protected abstract int getOutputPtr();
    
    public int getRow() {
        return this.mLocRowNr;
    }
    
    public int getColumn() {
        return this.getOutputPtr() - this.mLocRowStartOffset + 1;
    }
    
    public int getAbsOffset() {
        return this.mLocPastChars + this.getOutputPtr();
    }
    
    public final Writer wrapAsRawWriter() {
        if (this.mRawWrapper == null) {
            this.mRawWrapper = XmlWriterWrapper.wrapWriteRaw(this);
        }
        return this.mRawWrapper;
    }
    
    public final Writer wrapAsTextWriter() {
        if (this.mTextWrapper == null) {
            this.mTextWrapper = XmlWriterWrapper.wrapWriteCharacters(this);
        }
        return this.mTextWrapper;
    }
    
    public final void verifyNameValidity(final String name, final boolean checkNs) throws XMLStreamException {
        if (name == null || name.length() == 0) {
            this.reportNwfName(ErrorConsts.WERR_NAME_EMPTY);
        }
        final int illegalIx = WstxInputData.findIllegalNameChar(name, checkNs, this.mXml11);
        if (illegalIx >= 0) {
            if (illegalIx == 0) {
                this.reportNwfName(ErrorConsts.WERR_NAME_ILLEGAL_FIRST_CHAR, WstxInputData.getCharDesc(name.charAt(0)));
            }
            this.reportNwfName(ErrorConsts.WERR_NAME_ILLEGAL_CHAR, WstxInputData.getCharDesc(name.charAt(illegalIx)));
        }
    }
    
    protected void reportNwfName(final String msg) throws XMLStreamException {
        this.throwOutputError(msg);
    }
    
    protected void reportNwfName(final String msg, final Object arg) throws XMLStreamException {
        this.throwOutputError(msg, arg);
    }
    
    protected void reportNwfContent(final String msg) throws XMLStreamException {
        this.throwOutputError(msg);
    }
    
    protected void throwOutputError(final String msg) throws XMLStreamException {
        try {
            this.flush();
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        throw new XMLStreamException(msg);
    }
    
    protected void throwOutputError(final String format, final Object arg) throws XMLStreamException {
        final String msg = MessageFormat.format(format, arg);
        this.throwOutputError(msg);
    }
    
    protected char handleInvalidChar(final int c) throws IOException {
        this.flush();
        InvalidCharHandler h = this.mConfig.getInvalidCharHandler();
        if (h == null) {
            h = InvalidCharHandler.FailingHandler.getInstance();
        }
        return h.convertInvalidChar(c);
    }
}
