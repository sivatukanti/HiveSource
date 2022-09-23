// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import com.ctc.wstx.exc.WstxException;
import com.ctc.wstx.exc.WstxEOFException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import org.codehaus.stax2.validation.XMLValidationProblem;
import java.text.MessageFormat;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.api.ReaderConfig;
import java.io.InputStreamReader;
import java.io.Reader;

public final class ReaderBootstrapper extends InputBootstrapper
{
    static final char CHAR_BOM_MARKER = '\ufeff';
    final Reader mIn;
    final String mInputEncoding;
    private char[] mCharBuffer;
    private int mInputPtr;
    private int mInputEnd;
    
    private ReaderBootstrapper(final String pubId, final SystemId sysId, final Reader r, String appEncoding) {
        super(pubId, sysId);
        this.mIn = r;
        if (appEncoding == null && r instanceof InputStreamReader) {
            appEncoding = ((InputStreamReader)r).getEncoding();
        }
        this.mInputEncoding = appEncoding;
    }
    
    public static ReaderBootstrapper getInstance(final String pubId, final SystemId sysId, final Reader r, final String appEncoding) {
        return new ReaderBootstrapper(pubId, sysId, r, appEncoding);
    }
    
    @Override
    public Reader bootstrapInput(final ReaderConfig cfg, final boolean mainDoc, final int xmlVersion) throws IOException, XMLStreamException {
        this.mCharBuffer = ((cfg == null) ? new char[128] : cfg.allocSmallCBuffer(128));
        this.initialLoad(7);
        if (this.mInputEnd >= 7) {
            char c = this.mCharBuffer[this.mInputPtr];
            if (c == '\ufeff') {
                c = this.mCharBuffer[++this.mInputPtr];
            }
            if (c == '<') {
                if (this.mCharBuffer[this.mInputPtr + 1] == '?' && this.mCharBuffer[this.mInputPtr + 2] == 'x' && this.mCharBuffer[this.mInputPtr + 3] == 'm' && this.mCharBuffer[this.mInputPtr + 4] == 'l' && this.mCharBuffer[this.mInputPtr + 5] <= ' ') {
                    this.mInputPtr += 6;
                    this.readXmlDecl(mainDoc, xmlVersion);
                    if (this.mFoundEncoding != null && this.mInputEncoding != null) {
                        this.verifyXmlEncoding(cfg);
                    }
                }
            }
            else if (c == '\u00ef') {
                throw new WstxIOException("Unexpected first character (char code 0xEF), not valid in xml document: could be mangled UTF-8 BOM marker. Make sure that the Reader uses correct encoding or pass an InputStream instead");
            }
        }
        if (this.mInputPtr < this.mInputEnd) {
            return new MergedReader(cfg, this.mIn, this.mCharBuffer, this.mInputPtr, this.mInputEnd);
        }
        return this.mIn;
    }
    
    @Override
    public String getInputEncoding() {
        return this.mInputEncoding;
    }
    
    @Override
    public int getInputTotal() {
        return this.mInputProcessed + this.mInputPtr;
    }
    
    @Override
    public int getInputColumn() {
        return this.mInputPtr - this.mInputRowStart;
    }
    
    protected void verifyXmlEncoding(final ReaderConfig cfg) throws XMLStreamException {
        final String inputEnc = this.mInputEncoding;
        if (StringUtil.equalEncodings(inputEnc, this.mFoundEncoding)) {
            return;
        }
        final XMLReporter rep = cfg.getXMLReporter();
        if (rep != null) {
            final Location loc = this.getLocation();
            final String msg = MessageFormat.format(ErrorConsts.W_MIXED_ENCODINGS, this.mFoundEncoding, inputEnc);
            final String type = ErrorConsts.WT_XML_DECL;
            final XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, type);
            rep.report(msg, type, prob, loc);
        }
    }
    
    protected boolean initialLoad(final int minimum) throws IOException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        while (this.mInputEnd < minimum) {
            final int count = this.mIn.read(this.mCharBuffer, this.mInputEnd, this.mCharBuffer.length - this.mInputEnd);
            if (count < 1) {
                return false;
            }
            this.mInputEnd += count;
        }
        return true;
    }
    
    protected void loadMore() throws IOException, WstxException {
        this.mInputProcessed += this.mInputEnd;
        this.mInputRowStart -= this.mInputEnd;
        this.mInputPtr = 0;
        this.mInputEnd = this.mIn.read(this.mCharBuffer, 0, this.mCharBuffer.length);
        if (this.mInputEnd < 1) {
            throw new WstxEOFException(" in xml declaration", this.getLocation());
        }
    }
    
    @Override
    protected void pushback() {
        --this.mInputPtr;
    }
    
    @Override
    protected int getNext() throws IOException, WstxException {
        return (this.mInputPtr < this.mInputEnd) ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
    }
    
    @Override
    protected int getNextAfterWs(final boolean reqWs) throws IOException, WstxException {
        int count = 0;
        char c;
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mCharBuffer[this.mInputPtr++] : this.nextChar());
            if (c > ' ') {
                break;
            }
            if (c == '\r' || c == '\n') {
                this.skipCRLF(c);
            }
            else if (c == '\0') {
                this.reportNull();
            }
            ++count;
        }
        if (reqWs && count == 0) {
            this.reportUnexpectedChar(c, "; expected a white space");
        }
        return c;
    }
    
    @Override
    protected int checkKeyword(final String exp) throws IOException, WstxException {
        for (int len = exp.length(), ptr = 1; ptr < len; ++ptr) {
            final char c = (this.mInputPtr < this.mInputEnd) ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c != exp.charAt(ptr)) {
                return c;
            }
            if (c == '\0') {
                this.reportNull();
            }
        }
        return 0;
    }
    
    @Override
    protected int readQuotedValue(final char[] kw, final int quoteChar) throws IOException, WstxException {
        int i = 0;
        final int len = kw.length;
        while (true) {
            final char c = (this.mInputPtr < this.mInputEnd) ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c == '\r' || c == '\n') {
                this.skipCRLF(c);
            }
            else if (c == '\0') {
                this.reportNull();
            }
            if (c == quoteChar) {
                break;
            }
            if (i >= len) {
                continue;
            }
            kw[i++] = c;
        }
        return (i < len) ? i : -1;
    }
    
    @Override
    protected Location getLocation() {
        return new WstxInputLocation(null, this.mPublicId, this.mSystemId, this.mInputProcessed + this.mInputPtr - 1, this.mInputRow, this.mInputPtr - this.mInputRowStart);
    }
    
    protected char nextChar() throws IOException, WstxException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore();
        }
        return this.mCharBuffer[this.mInputPtr++];
    }
    
    protected void skipCRLF(final char lf) throws IOException, WstxException {
        if (lf == '\r') {
            final char c = (this.mInputPtr < this.mInputEnd) ? this.mCharBuffer[this.mInputPtr++] : this.nextChar();
            if (c != '\n') {
                --this.mInputPtr;
            }
        }
        ++this.mInputRow;
        this.mInputRowStart = this.mInputPtr;
    }
}
