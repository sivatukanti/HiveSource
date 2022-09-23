// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.ent.EntityDecl;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.StreamScanner;

public class MinimalDTDReader extends StreamScanner
{
    final boolean mIsExternal;
    
    private MinimalDTDReader(final WstxInputSource input, final ReaderConfig cfg) {
        this(input, cfg, false);
    }
    
    protected MinimalDTDReader(final WstxInputSource input, final ReaderConfig cfg, final boolean isExt) {
        super(input, cfg, cfg.getDtdResolver());
        this.mIsExternal = isExt;
        this.mCfgReplaceEntities = true;
    }
    
    public static void skipInternalSubset(final WstxInputData srcData, final WstxInputSource input, final ReaderConfig cfg) throws XMLStreamException {
        final MinimalDTDReader r = new MinimalDTDReader(input, cfg);
        r.copyBufferStateFrom(srcData);
        try {
            r.skipInternalSubset();
        }
        finally {
            srcData.copyBufferStateFrom(r);
        }
    }
    
    @Override
    public final Location getLocation() {
        return this.getStartLocation();
    }
    
    @Override
    protected EntityDecl findEntity(final String id, final Object arg) {
        this.throwIllegalCall();
        return null;
    }
    
    @Override
    protected void handleUndeclaredEntity(final String id) throws XMLStreamException {
    }
    
    @Override
    protected void handleIncompleteEntityProblem(final WstxInputSource closing) throws XMLStreamException {
    }
    
    protected char handleExpandedSurrogate(final char first, final char second) {
        return first;
    }
    
    public EntityDecl findEntity(final String entName) {
        return null;
    }
    
    protected void skipInternalSubset() throws XMLStreamException {
        while (true) {
            final int i = this.getNextAfterWS();
            if (i < 0) {
                this.throwUnexpectedEOF(" in internal DTD subset");
            }
            if (i == 37) {
                this.skipPE();
            }
            else if (i == 60) {
                char c = this.getNextSkippingPEs();
                if (c == '?') {
                    this.skipPI();
                }
                else if (c == '!') {
                    c = this.getNextSkippingPEs();
                    if (c == '[') {
                        continue;
                    }
                    if (c == '-') {
                        this.skipComment();
                    }
                    else if (c >= 'A' && c <= 'Z') {
                        this.skipDeclaration(c);
                    }
                    else {
                        this.skipDeclaration(c);
                    }
                }
                else {
                    --this.mInputPtr;
                }
            }
            else {
                if (i == 93) {
                    break;
                }
                this.throwUnexpectedChar(i, " in internal DTD subset; expected a '<' to start a directive, or \"]>\" to end internal subset.");
            }
        }
        if (this.mInput != this.mRootInput) {
            this.throwParseError("Encountered int. subset end marker ']]>' in an expanded entity; has to be at main level.");
        }
    }
    
    protected char dtdNextFromCurr() throws XMLStreamException {
        return (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(this.getErrorMsg());
    }
    
    protected char dtdNextChar() throws XMLStreamException {
        return (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
    }
    
    protected char getNextSkippingPEs() throws XMLStreamException {
        char c;
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg()));
            if (c != '%') {
                break;
            }
            this.skipPE();
        }
        return c;
    }
    
    private void skipPE() throws XMLStreamException {
        this.skipDTDName();
        final char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
        if (c != ';') {
            --this.mInputPtr;
        }
    }
    
    protected void skipComment() throws XMLStreamException {
        this.skipCommentContent();
        final char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
        if (c != '>') {
            this.throwParseError("String '--' not allowed in comment (missing '>'?)");
        }
    }
    
    protected void skipCommentContent() throws XMLStreamException {
        while (true) {
            char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '-') {
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
                if (c == '-') {
                    break;
                }
                continue;
            }
            else {
                if (c != '\n' && c != '\r') {
                    continue;
                }
                this.skipCRLF(c);
            }
        }
    }
    
    protected void skipPI() throws XMLStreamException {
        while (true) {
            char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '?') {
                do {
                    c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
                } while (c == '?');
                if (c == '>') {
                    break;
                }
            }
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
        }
    }
    
    private void skipDeclaration(char c) throws XMLStreamException {
        while (c != '>') {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else {
                if (c != '\'' && c != '\"') {
                    continue;
                }
                this.skipLiteral(c);
            }
        }
    }
    
    private void skipLiteral(final char quoteChar) throws XMLStreamException {
        while (true) {
            final char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else {
                if (c == quoteChar) {
                    break;
                }
                continue;
            }
        }
    }
    
    private void skipDTDName() throws XMLStreamException {
        this.skipFullName(this.getNextChar(this.getErrorMsg()));
    }
    
    protected String getErrorMsg() {
        return this.mIsExternal ? " in external DTD subset" : " in internal DTD subset";
    }
    
    protected void throwIllegalCall() throws Error {
        throw new IllegalStateException("Internal error: this method should never be called");
    }
}
