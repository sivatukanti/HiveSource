// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import com.ctc.wstx.util.TextBuffer;
import com.ctc.wstx.io.DefaultInputResolver;
import java.io.FileNotFoundException;
import com.ctc.wstx.dtd.MinimalDTDReader;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.exc.WstxLazyException;
import com.ctc.wstx.exc.WstxEOFException;
import com.ctc.wstx.exc.WstxUnexpectedCharException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.exc.WstxValidationException;
import org.codehaus.stax2.XMLReporter2;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.validation.XMLValidationProblem;
import javax.xml.stream.XMLReporter;
import java.text.MessageFormat;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.exc.WstxException;
import org.codehaus.stax2.XMLStreamLocation2;
import javax.xml.stream.Location;
import java.io.IOException;
import java.net.URL;
import com.ctc.wstx.io.WstxInputLocation;
import java.util.Collections;
import java.util.HashMap;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.ent.IntEntity;
import java.util.Map;
import javax.xml.stream.XMLResolver;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.ctc.wstx.cfg.InputConfigFlags;
import com.ctc.wstx.io.WstxInputData;

public abstract class StreamScanner extends WstxInputData implements InputProblemReporter, InputConfigFlags, ParsingErrorMsgs
{
    public static final char CHAR_CR_LF_OR_NULL = '\r';
    public static final int INT_CR_LF_OR_NULL = 13;
    protected static final char CHAR_FIRST_PURE_TEXT = '?';
    protected static final char CHAR_LOWEST_LEGAL_LOCALNAME_CHAR = '-';
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity;
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected final ReaderConfig mConfig;
    protected final boolean mCfgNsEnabled;
    protected boolean mCfgReplaceEntities;
    final SymbolTable mSymbols;
    protected String mCurrName;
    protected WstxInputSource mInput;
    protected final WstxInputSource mRootInput;
    protected XMLResolver mEntityResolver;
    protected int mCurrDepth;
    protected int mInputTopDepth;
    protected int mEntityExpansionCount;
    protected boolean mNormalizeLFs;
    protected char[] mNameBuffer;
    protected long mTokenInputTotal;
    protected int mTokenInputRow;
    protected int mTokenInputCol;
    protected String mDocInputEncoding;
    protected String mDocXmlEncoding;
    protected int mDocXmlVersion;
    protected Map<String, IntEntity> mCachedEntities;
    protected boolean mCfgTreatCharRefsAsEntities;
    protected EntityDecl mCurrEntity;
    
    protected StreamScanner(final WstxInputSource input, final ReaderConfig cfg, final XMLResolver res) {
        this.mEntityResolver = null;
        this.mNameBuffer = null;
        this.mTokenInputTotal = 0L;
        this.mTokenInputRow = 1;
        this.mTokenInputCol = 0;
        this.mDocInputEncoding = null;
        this.mDocXmlEncoding = null;
        this.mDocXmlVersion = 0;
        this.mInput = input;
        this.mRootInput = input;
        this.mConfig = cfg;
        this.mSymbols = cfg.getSymbols();
        final int cf = cfg.getConfigFlags();
        this.mCfgNsEnabled = ((cf & 0x1) != 0x0);
        this.mCfgReplaceEntities = ((cf & 0x4) != 0x0);
        this.mNormalizeLFs = this.mConfig.willNormalizeLFs();
        this.mInputBuffer = null;
        final int n = 0;
        this.mInputEnd = n;
        this.mInputPtr = n;
        this.mEntityResolver = res;
        this.mCfgTreatCharRefsAsEntities = this.mConfig.willTreatCharRefsAsEnts();
        if (this.mCfgTreatCharRefsAsEntities) {
            this.mCachedEntities = new HashMap<String, IntEntity>();
        }
        else {
            this.mCachedEntities = Collections.emptyMap();
        }
    }
    
    protected WstxInputLocation getLastCharLocation() {
        return this.mInput.getLocation(this.mCurrInputProcessed + this.mInputPtr - 1L, this.mCurrInputRow, this.mInputPtr - this.mCurrInputRowStart);
    }
    
    protected URL getSource() throws IOException {
        return this.mInput.getSource();
    }
    
    protected String getSystemId() {
        return this.mInput.getSystemId();
    }
    
    @Override
    public abstract Location getLocation();
    
    public XMLStreamLocation2 getStartLocation() {
        return this.mInput.getLocation(this.mTokenInputTotal, this.mTokenInputRow, this.mTokenInputCol + 1);
    }
    
    public XMLStreamLocation2 getCurrentLocation() {
        return this.mInput.getLocation(this.mCurrInputProcessed + this.mInputPtr, this.mCurrInputRow, this.mInputPtr - this.mCurrInputRowStart + 1);
    }
    
    public WstxException throwWfcException(final String msg, final boolean deferErrors) throws WstxException {
        final WstxException ex = this.constructWfcException(msg);
        if (!deferErrors) {
            throw ex;
        }
        return ex;
    }
    
    @Override
    public void throwParseError(final String msg) throws XMLStreamException {
        this.throwParseError(msg, null, null);
    }
    
    @Override
    public void throwParseError(final String format, final Object arg, final Object arg2) throws XMLStreamException {
        final String msg = (arg != null || arg2 != null) ? MessageFormat.format(format, arg, arg2) : format;
        throw this.constructWfcException(msg);
    }
    
    public void reportProblem(final String probType, final String format, final Object arg, final Object arg2) throws XMLStreamException {
        final XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            this._reportProblem(rep, probType, MessageFormat.format(format, arg, arg2), null);
        }
    }
    
    @Override
    public void reportProblem(final Location loc, final String probType, final String format, final Object arg, final Object arg2) throws XMLStreamException {
        final XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            final String msg = (arg != null || arg2 != null) ? MessageFormat.format(format, arg, arg2) : format;
            this._reportProblem(rep, probType, msg, loc);
        }
    }
    
    protected void _reportProblem(final XMLReporter rep, final String probType, final String msg, Location loc) throws XMLStreamException {
        if (loc == null) {
            loc = this.getLastCharLocation();
        }
        this._reportProblem(rep, new XMLValidationProblem(loc, msg, 2, probType));
    }
    
    protected void _reportProblem(final XMLReporter rep, final XMLValidationProblem prob) throws XMLStreamException {
        if (rep != null) {
            Location loc = prob.getLocation();
            if (loc == null) {
                loc = this.getLastCharLocation();
                prob.setLocation(loc);
            }
            if (prob.getType() == null) {
                prob.setType(ErrorConsts.WT_VALIDATION);
            }
            if (rep instanceof XMLReporter2) {
                ((XMLReporter2)rep).report(prob);
            }
            else {
                rep.report(prob.getMessage(), prob.getType(), prob, loc);
            }
        }
    }
    
    @Override
    public void reportValidationProblem(final XMLValidationProblem prob) throws XMLStreamException {
        if (prob.getSeverity() > 2) {
            throw WstxValidationException.create(prob);
        }
        final XMLReporter rep = this.mConfig.getXMLReporter();
        if (rep != null) {
            this._reportProblem(rep, prob);
        }
        else if (prob.getSeverity() >= 2) {
            throw WstxValidationException.create(prob);
        }
    }
    
    public void reportValidationProblem(final String msg, final int severity) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(this.getLastCharLocation(), msg, severity));
    }
    
    @Override
    public void reportValidationProblem(final String msg) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(this.getLastCharLocation(), msg, 2));
    }
    
    public void reportValidationProblem(final Location loc, final String msg) throws XMLStreamException {
        this.reportValidationProblem(new XMLValidationProblem(loc, msg));
    }
    
    @Override
    public void reportValidationProblem(final String format, final Object arg, final Object arg2) throws XMLStreamException {
        this.reportValidationProblem(MessageFormat.format(format, arg, arg2));
    }
    
    protected WstxException constructWfcException(final String msg) {
        return new WstxParsingException(msg, this.getLastCharLocation());
    }
    
    protected WstxException constructFromIOE(final IOException ioe) {
        return new WstxIOException(ioe);
    }
    
    protected WstxException constructNullCharException() {
        return new WstxUnexpectedCharException("Illegal character (NULL, unicode 0) encountered: not valid in any content", this.getLastCharLocation(), '\0');
    }
    
    protected void throwUnexpectedChar(final int i, final String msg) throws WstxException {
        final char c = (char)i;
        final String excMsg = "Unexpected character " + WstxInputData.getCharDesc(c) + msg;
        throw new WstxUnexpectedCharException(excMsg, this.getLastCharLocation(), c);
    }
    
    protected void throwNullChar() throws WstxException {
        throw this.constructNullCharException();
    }
    
    protected void throwInvalidSpace(final int i) throws WstxException {
        this.throwInvalidSpace(i, false);
    }
    
    protected WstxException throwInvalidSpace(final int i, final boolean deferErrors) throws WstxException {
        final char c = (char)i;
        WstxException ex;
        if (c == '\0') {
            ex = this.constructNullCharException();
        }
        else {
            String msg = "Illegal character (" + WstxInputData.getCharDesc(c) + ")";
            if (this.mXml11) {
                msg += " [note: in XML 1.1, it could be included via entity expansion]";
            }
            ex = new WstxUnexpectedCharException(msg, this.getLastCharLocation(), c);
        }
        if (!deferErrors) {
            throw ex;
        }
        return ex;
    }
    
    protected void throwUnexpectedEOF(final String msg) throws WstxException {
        throw new WstxEOFException("Unexpected EOF" + ((msg == null) ? "" : msg), this.getLastCharLocation());
    }
    
    protected void throwUnexpectedEOB(final String msg) throws WstxException {
        throw new WstxEOFException("Unexpected end of input block" + ((msg == null) ? "" : msg), this.getLastCharLocation());
    }
    
    protected void throwFromIOE(final IOException ioe) throws WstxException {
        throw new WstxIOException(ioe);
    }
    
    protected void throwFromStrE(final XMLStreamException strex) throws WstxException {
        if (strex instanceof WstxException) {
            throw (WstxException)strex;
        }
        throw new WstxException(strex);
    }
    
    protected void throwLazyError(final Exception e) {
        if (e instanceof XMLStreamException) {
            WstxLazyException.throwLazily((XMLStreamException)e);
        }
        ExceptionUtil.throwRuntimeException(e);
    }
    
    protected String tokenTypeDesc(final int type) {
        return ErrorConsts.tokenTypeDesc(type);
    }
    
    public final WstxInputSource getCurrentInput() {
        return this.mInput;
    }
    
    protected final int inputInBuffer() {
        return this.mInputEnd - this.mInputPtr;
    }
    
    protected final int getNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
            return -1;
        }
        return this.mInputBuffer[this.mInputPtr++];
    }
    
    protected final int peekNext() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMoreFromCurrent()) {
            return -1;
        }
        return this.mInputBuffer[this.mInputPtr];
    }
    
    protected final char getNextChar(final String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        return this.mInputBuffer[this.mInputPtr++];
    }
    
    protected final char getNextCharFromCurrent(final String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMoreFromCurrent(errorMsg);
        }
        return this.mInputBuffer[this.mInputPtr++];
    }
    
    protected final int getNextAfterWS() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
            return -1;
        }
        char c;
        for (c = this.mInputBuffer[this.mInputPtr++]; c <= ' '; c = this.mInputBuffer[this.mInputPtr++]) {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd && !this.loadMore()) {
                return -1;
            }
        }
        return c;
    }
    
    protected final char getNextCharAfterWS(final String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        char c;
        for (c = this.mInputBuffer[this.mInputPtr++]; c <= ' '; c = this.mInputBuffer[this.mInputPtr++]) {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd) {
                this.loadMore(errorMsg);
            }
        }
        return c;
    }
    
    protected final char getNextInCurrAfterWS(final String errorMsg) throws XMLStreamException {
        return this.getNextInCurrAfterWS(errorMsg, this.getNextCharFromCurrent(errorMsg));
    }
    
    protected final char getNextInCurrAfterWS(final String errorMsg, char c) throws XMLStreamException {
        while (c <= ' ') {
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else if (c != ' ' && c != '\t') {
                this.throwInvalidSpace(c);
            }
            if (this.mInputPtr >= this.mInputEnd) {
                this.loadMoreFromCurrent(errorMsg);
            }
            c = this.mInputBuffer[this.mInputPtr++];
        }
        return c;
    }
    
    protected final boolean skipCRLF(final char c) throws XMLStreamException {
        boolean result;
        if (c == '\r' && this.peekNext() == 10) {
            ++this.mInputPtr;
            result = true;
        }
        else {
            result = false;
        }
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = this.mInputPtr;
        return result;
    }
    
    protected final void markLF() {
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = this.mInputPtr;
    }
    
    protected final void markLF(final int inputPtr) {
        ++this.mCurrInputRow;
        this.mCurrInputRowStart = inputPtr;
    }
    
    protected final void pushback() {
        --this.mInputPtr;
    }
    
    protected void initInputSource(final WstxInputSource newInput, final boolean isExt, final String entityId) throws XMLStreamException {
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        this.mInputTopDepth = this.mCurrDepth;
        final int entityDepth = this.mInput.getEntityDepth() + 1;
        this.verifyLimit("Maximum entity expansion depth", this.mConfig.getMaxEntityDepth(), entityDepth);
        (this.mInput = newInput).initInputLocation(this, this.mCurrDepth, entityDepth);
        if (isExt) {
            this.mNormalizeLFs = true;
        }
        else {
            this.mNormalizeLFs = false;
        }
    }
    
    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        do {
            this.mCurrInputProcessed += this.mInputEnd;
            this.verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                final int count = input.readInto(this);
                if (count > 0) {
                    return true;
                }
                input.close();
            }
            catch (IOException ioe) {
                throw this.constructFromIOE(ioe);
            }
            if (input == this.mRootInput) {
                return false;
            }
            final WstxInputSource parent = input.getParent();
            if (parent == null) {
                this.throwNullParent(input);
            }
            if (this.mCurrDepth != input.getScopeId()) {
                this.handleIncompleteEntityProblem(input);
            }
            input = (this.mInput = parent);
            input.restoreContext(this);
            this.mInputTopDepth = input.getScopeId();
            if (this.mNormalizeLFs) {
                continue;
            }
            this.mNormalizeLFs = !input.fromInternalEntity();
        } while (this.mInputPtr >= this.mInputEnd);
        return true;
    }
    
    protected final boolean loadMore(final String errorMsg) throws XMLStreamException {
        if (!this.loadMore()) {
            this.throwUnexpectedEOF(errorMsg);
        }
        return true;
    }
    
    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        this.mCurrInputProcessed += this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        this.verifyLimit("Maximum document characters", this.mConfig.getMaxCharacters(), this.mCurrInputProcessed);
        try {
            final int count = this.mInput.readInto(this);
            return count > 0;
        }
        catch (IOException ie) {
            throw this.constructFromIOE(ie);
        }
    }
    
    protected final boolean loadMoreFromCurrent(final String errorMsg) throws XMLStreamException {
        if (!this.loadMoreFromCurrent()) {
            this.throwUnexpectedEOB(errorMsg);
        }
        return true;
    }
    
    protected boolean ensureInput(final int minAmount) throws XMLStreamException {
        final int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        try {
            return this.mInput.readMore(this, minAmount);
        }
        catch (IOException ie) {
            throw this.constructFromIOE(ie);
        }
    }
    
    protected void closeAllInput(final boolean force) throws XMLStreamException {
        WstxInputSource input = this.mInput;
        while (true) {
            try {
                if (force) {
                    input.closeCompletely();
                }
                else {
                    input.close();
                }
            }
            catch (IOException ie) {
                throw this.constructFromIOE(ie);
            }
            if (input == this.mRootInput) {
                break;
            }
            final WstxInputSource parent = input.getParent();
            if (parent == null) {
                this.throwNullParent(input);
            }
            input = (this.mInput = parent);
        }
    }
    
    protected void throwNullParent(final WstxInputSource curr) {
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }
    
    protected int resolveSimpleEntity(final boolean checkStd) throws XMLStreamException {
        final char[] buf = this.mInputBuffer;
        int ptr = this.mInputPtr;
        char c = buf[ptr++];
        if (c == '#') {
            c = buf[ptr++];
            int value = 0;
            final int inputLen = this.mInputEnd;
            if (c == 'x') {
                while (ptr < inputLen) {
                    c = buf[ptr++];
                    if (c == ';') {
                        break;
                    }
                    value <<= 4;
                    if (c <= '9' && c >= '0') {
                        value += c - '0';
                    }
                    else if (c >= 'a' && c <= 'f') {
                        value += 10 + (c - 'a');
                    }
                    else if (c >= 'A' && c <= 'F') {
                        value += 10 + (c - 'A');
                    }
                    else {
                        this.mInputPtr = ptr;
                        this.throwUnexpectedChar(c, "; expected a hex digit (0-9a-fA-F).");
                    }
                    if (value <= 1114111) {
                        continue;
                    }
                    this.reportUnicodeOverflow();
                }
            }
            else {
                while (c != ';') {
                    if (c <= '9' && c >= '0') {
                        value = value * 10 + (c - '0');
                        if (value > 1114111) {
                            this.reportUnicodeOverflow();
                        }
                    }
                    else {
                        this.mInputPtr = ptr;
                        this.throwUnexpectedChar(c, "; expected a decimal number.");
                    }
                    if (ptr >= inputLen) {
                        break;
                    }
                    c = buf[ptr++];
                }
            }
            if (c == ';') {
                this.mInputPtr = ptr;
                this.validateChar(value);
                return value;
            }
        }
        else if (checkStd) {
            if (c == 'a') {
                c = buf[ptr++];
                if (c == 'm') {
                    if (buf[ptr++] == 'p' && ptr < this.mInputEnd && buf[ptr++] == ';') {
                        this.mInputPtr = ptr;
                        return 38;
                    }
                }
                else if (c == 'p' && buf[ptr++] == 'o') {
                    final int len = this.mInputEnd;
                    if (ptr < len && buf[ptr++] == 's' && ptr < len && buf[ptr++] == ';') {
                        this.mInputPtr = ptr;
                        return 39;
                    }
                }
            }
            else if (c == 'g') {
                if (buf[ptr++] == 't' && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 62;
                }
            }
            else if (c == 'l') {
                if (buf[ptr++] == 't' && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 60;
                }
            }
            else if (c == 'q' && buf[ptr++] == 'u' && buf[ptr++] == 'o') {
                final int len = this.mInputEnd;
                if (ptr < len && buf[ptr++] == 't' && ptr < len && buf[ptr++] == ';') {
                    this.mInputPtr = ptr;
                    return 34;
                }
            }
        }
        return 0;
    }
    
    protected int resolveCharOnlyEntity(final boolean checkStd) throws XMLStreamException {
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            --this.mInputPtr;
            if (!this.ensureInput(6)) {
                avail = this.inputInBuffer();
                if (avail < 3) {
                    this.throwUnexpectedEOF(" in entity reference");
                }
            }
            else {
                avail = 6;
            }
            ++this.mInputPtr;
        }
        final char c = this.mInputBuffer[this.mInputPtr];
        if (c == '#') {
            ++this.mInputPtr;
            return this.resolveCharEnt(null);
        }
        if (checkStd) {
            if (c == 'a') {
                final char d = this.mInputBuffer[this.mInputPtr + 1];
                if (d == 'm') {
                    if (avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';') {
                        this.mInputPtr += 4;
                        return 38;
                    }
                }
                else if (d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                    this.mInputPtr += 5;
                    return 39;
                }
            }
            else if (c == 'l') {
                if (avail >= 3 && this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 60;
                }
            }
            else if (c == 'g') {
                if (avail >= 3 && this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                    this.mInputPtr += 3;
                    return 62;
                }
            }
            else if (c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                this.mInputPtr += 5;
                return 34;
            }
        }
        return 0;
    }
    
    protected EntityDecl resolveNonCharEntity() throws XMLStreamException {
        int avail = this.mInputEnd - this.mInputPtr;
        if (avail < 6) {
            --this.mInputPtr;
            if (!this.ensureInput(6)) {
                avail = this.inputInBuffer();
                if (avail < 3) {
                    this.throwUnexpectedEOF(" in entity reference");
                }
            }
            else {
                avail = 6;
            }
            ++this.mInputPtr;
        }
        final char c = this.mInputBuffer[this.mInputPtr];
        if (c == '#') {
            return null;
        }
        if (c == 'a') {
            final char d = this.mInputBuffer[this.mInputPtr + 1];
            if (d == 'm') {
                if (avail >= 4 && this.mInputBuffer[this.mInputPtr + 2] == 'p' && this.mInputBuffer[this.mInputPtr + 3] == ';') {
                    return null;
                }
            }
            else if (d == 'p' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 's' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
                return null;
            }
        }
        else if (c == 'l') {
            if (avail >= 3 && this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                return null;
            }
        }
        else if (c == 'g') {
            if (avail >= 3 && this.mInputBuffer[this.mInputPtr + 1] == 't' && this.mInputBuffer[this.mInputPtr + 2] == ';') {
                return null;
            }
        }
        else if (c == 'q' && avail >= 5 && this.mInputBuffer[this.mInputPtr + 1] == 'u' && this.mInputBuffer[this.mInputPtr + 2] == 'o' && this.mInputBuffer[this.mInputPtr + 3] == 't' && this.mInputBuffer[this.mInputPtr + 4] == ';') {
            return null;
        }
        ++this.mInputPtr;
        final String id = this.parseEntityName(c);
        this.mCurrName = id;
        return this.findEntity(id, null);
    }
    
    protected int fullyResolveEntity(final boolean allowExt) throws XMLStreamException {
        char c = this.getNextCharFromCurrent(" in entity reference");
        if (c == '#') {
            final StringBuffer originalSurface = new StringBuffer("#");
            final int ch = this.resolveCharEnt(originalSurface);
            if (this.mCfgTreatCharRefsAsEntities) {
                final char[] originalChars = new char[originalSurface.length()];
                originalSurface.getChars(0, originalSurface.length(), originalChars, 0);
                this.mCurrEntity = this.getIntEntity(ch, originalChars);
                return 0;
            }
            return ch;
        }
        else {
            final String id = this.parseEntityName(c);
            c = id.charAt(0);
            char d = '\0';
            if (c == 'a') {
                if (id.equals("amp")) {
                    d = '&';
                }
                else if (id.equals("apos")) {
                    d = '\'';
                }
            }
            else if (c == 'g') {
                if (id.length() == 2 && id.charAt(1) == 't') {
                    d = '>';
                }
            }
            else if (c == 'l') {
                if (id.length() == 2 && id.charAt(1) == 't') {
                    d = '<';
                }
            }
            else if (c == 'q' && id.equals("quot")) {
                d = '\"';
            }
            if (d == '\0') {
                final EntityDecl e = this.expandEntity(id, allowExt, null);
                if (this.mCfgTreatCharRefsAsEntities) {
                    this.mCurrEntity = e;
                }
                return 0;
            }
            if (this.mCfgTreatCharRefsAsEntities) {
                final char[] originalChars = new char[id.length()];
                id.getChars(0, id.length(), originalChars, 0);
                this.mCurrEntity = this.getIntEntity(d, originalChars);
                return 0;
            }
            return d;
        }
    }
    
    protected EntityDecl getIntEntity(int ch, final char[] originalChars) {
        final String cacheKey = new String(originalChars);
        IntEntity entity = this.mCachedEntities.get(cacheKey);
        if (entity == null) {
            String repl;
            if (ch <= 65535) {
                repl = Character.toString((char)ch);
            }
            else {
                final StringBuffer sb = new StringBuffer(2);
                ch -= 65536;
                sb.append((char)((ch >> 10) + 55296));
                sb.append((char)((ch & 0x3FF) + 56320));
                repl = sb.toString();
            }
            entity = IntEntity.create(new String(originalChars), repl);
            this.mCachedEntities.put(cacheKey, entity);
        }
        return entity;
    }
    
    protected EntityDecl expandEntity(final String id, final boolean allowExt, final Object extraArg) throws XMLStreamException {
        this.mCurrName = id;
        final EntityDecl ed = this.findEntity(id, extraArg);
        if (ed == null) {
            if (this.mCfgReplaceEntities) {
                this.mCurrEntity = this.expandUnresolvedEntity(id);
            }
            return null;
        }
        if (!this.mCfgTreatCharRefsAsEntities || this instanceof MinimalDTDReader) {
            this.expandEntity(ed, allowExt);
        }
        return ed;
    }
    
    private void expandEntity(final EntityDecl ed, final boolean allowExt) throws XMLStreamException {
        final String id = ed.getName();
        if (this.mInput.isOrIsExpandedFrom(id)) {
            this.throwRecursionError(id);
        }
        if (!ed.isParsed()) {
            this.throwParseError("Illegal reference to unparsed external entity \"{0}\"", id, null);
        }
        final boolean isExt = ed.isExternal();
        if (isExt) {
            if (!allowExt) {
                this.throwParseError("Encountered a reference to external parsed entity \"{0}\" when expanding attribute value: not legal as per XML 1.0/1.1 #3.1", id, null);
            }
            if (!this.mConfig.willSupportExternalEntities()) {
                this.throwParseError("Encountered a reference to external entity \"{0}\", but stream reader has feature \"{1}\" disabled", id, "javax.xml.stream.isSupportingExternalEntities");
            }
        }
        this.verifyLimit("Maximum entity expansion count", this.mConfig.getMaxEntityCount(), ++this.mEntityExpansionCount);
        final WstxInputSource oldInput = this.mInput;
        oldInput.saveContext(this);
        WstxInputSource newInput = null;
        try {
            newInput = ed.expand(oldInput, this.mEntityResolver, this.mConfig, this.mDocXmlVersion);
        }
        catch (FileNotFoundException fex) {
            this.throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
        }
        catch (IOException ioe) {
            throw this.constructFromIOE(ioe);
        }
        this.initInputSource(newInput, isExt, id);
    }
    
    private EntityDecl expandUnresolvedEntity(final String id) throws XMLStreamException {
        final XMLResolver resolver = this.mConfig.getUndeclaredEntityResolver();
        if (resolver != null) {
            if (this.mInput.isOrIsExpandedFrom(id)) {
                this.throwRecursionError(id);
            }
            final WstxInputSource oldInput = this.mInput;
            oldInput.saveContext(this);
            int xmlVersion = this.mDocXmlVersion;
            if (xmlVersion == 0) {
                xmlVersion = 256;
            }
            WstxInputSource newInput;
            try {
                newInput = DefaultInputResolver.resolveEntityUsing(oldInput, id, null, null, resolver, this.mConfig, xmlVersion);
                if (this.mCfgTreatCharRefsAsEntities) {
                    return new IntEntity(WstxInputLocation.getEmptyLocation(), newInput.getEntityId(), newInput.getSource(), new char[0], WstxInputLocation.getEmptyLocation());
                }
            }
            catch (IOException ioe) {
                throw this.constructFromIOE(ioe);
            }
            if (newInput != null) {
                this.initInputSource(newInput, true, id);
                return null;
            }
        }
        this.handleUndeclaredEntity(id);
        return null;
    }
    
    protected abstract EntityDecl findEntity(final String p0, final Object p1) throws XMLStreamException;
    
    protected abstract void handleUndeclaredEntity(final String p0) throws XMLStreamException;
    
    protected abstract void handleIncompleteEntityProblem(final WstxInputSource p0) throws XMLStreamException;
    
    protected String parseLocalName(char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            if (c == ':') {
                this.throwUnexpectedChar(c, " (missing namespace prefix?)");
            }
            this.throwUnexpectedChar(c, " (expected a name start character)");
        }
        int ptr = this.mInputPtr;
        int hash = c;
        final int inputLen = this.mInputEnd;
        final int startPtr = ptr - 1;
        final char[] inputBuf = this.mInputBuffer;
        while (ptr < inputLen) {
            c = inputBuf[ptr];
            if (c >= '-') {
                if (this.isNameChar(c)) {
                    hash = hash * 31 + c;
                    ++ptr;
                    continue;
                }
            }
            this.mInputPtr = ptr;
            return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
        }
        this.mInputPtr = ptr;
        return this.parseLocalName2(startPtr, hash);
    }
    
    protected String parseLocalName2(final int start, int hash) throws XMLStreamException {
        int ptr = this.mInputEnd - start;
        char[] outBuf = this.getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while (this.mInputPtr < this.mInputEnd || this.loadMoreFromCurrent()) {
            final char c = this.mInputBuffer[this.mInputPtr];
            if (c >= '-') {
                if (this.isNameChar(c)) {
                    ++this.mInputPtr;
                    if (ptr >= outLen) {
                        outBuf = (this.mNameBuffer = this.expandBy50Pct(outBuf));
                        outLen = outBuf.length;
                    }
                    outBuf[ptr++] = c;
                    hash = hash * 31 + c;
                    continue;
                }
            }
            return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }
    
    protected String parseFullName() throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMoreFromCurrent();
        }
        return this.parseFullName(this.mInputBuffer[this.mInputPtr++]);
    }
    
    protected String parseFullName(char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            if (c == ':') {
                if (this.mCfgNsEnabled) {
                    this.throwNsColonException(this.parseFNameForError());
                }
            }
            else {
                if (c <= ' ') {
                    this.throwUnexpectedChar(c, " (missing name?)");
                }
                this.throwUnexpectedChar(c, " (expected a name start character)");
            }
        }
        int ptr = this.mInputPtr;
        int hash = c;
        final int inputLen = this.mInputEnd;
        final int startPtr = ptr - 1;
        while (ptr < inputLen) {
            c = this.mInputBuffer[ptr];
            Label_0178: {
                if (c != ':') {
                    if (c >= '-') {
                        if (this.isNameChar(c)) {
                            break Label_0178;
                        }
                    }
                    this.mInputPtr = ptr;
                    return this.mSymbols.findSymbol(this.mInputBuffer, startPtr, ptr - startPtr, hash);
                }
                if (this.mCfgNsEnabled) {
                    this.mInputPtr = ptr;
                    this.throwNsColonException(new String(this.mInputBuffer, startPtr, ptr - startPtr) + this.parseFNameForError());
                }
            }
            hash = hash * 31 + c;
            ++ptr;
        }
        this.mInputPtr = ptr;
        return this.parseFullName2(startPtr, hash);
    }
    
    protected String parseFullName2(final int start, int hash) throws XMLStreamException {
        int ptr = this.mInputEnd - start;
        char[] outBuf = this.getNameBuffer(ptr + 8);
        if (ptr > 0) {
            System.arraycopy(this.mInputBuffer, start, outBuf, 0, ptr);
        }
        int outLen = outBuf.length;
        while (this.mInputPtr < this.mInputEnd || this.loadMoreFromCurrent()) {
            final char c = this.mInputBuffer[this.mInputPtr];
            Label_0149: {
                if (c != ':') {
                    if (c >= '-') {
                        if (this.isNameChar(c)) {
                            break Label_0149;
                        }
                    }
                    return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
                }
                if (this.mCfgNsEnabled) {
                    this.throwNsColonException(new String(outBuf, 0, ptr) + c + this.parseFNameForError());
                }
            }
            ++this.mInputPtr;
            if (ptr >= outLen) {
                outBuf = (this.mNameBuffer = this.expandBy50Pct(outBuf));
                outLen = outBuf.length;
            }
            outBuf[ptr++] = c;
            hash = hash * 31 + c;
        }
        return this.mSymbols.findSymbol(outBuf, 0, ptr, hash);
    }
    
    protected String parseFNameForError() throws XMLStreamException {
        final StringBuilder sb = new StringBuilder(100);
        while (true) {
            char c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            }
            else {
                final int i = this.getNext();
                if (i < 0) {
                    break;
                }
                c = (char)i;
            }
            if (c != ':' && !this.isNameChar(c)) {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    protected final String parseEntityName(char c) throws XMLStreamException {
        final String id = this.parseFullName(c);
        if (this.mInputPtr >= this.mInputEnd && !this.loadMoreFromCurrent()) {
            this.throwParseError("Missing semicolon after reference for entity \"{0}\"", id, null);
        }
        c = this.mInputBuffer[this.mInputPtr++];
        if (c != ';') {
            this.throwUnexpectedChar(c, "; expected a semi-colon after the reference for entity '" + id + "'");
        }
        return id;
    }
    
    protected int skipFullName(char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            --this.mInputPtr;
            return 0;
        }
        int count = 1;
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar("; expected an identifier"));
            if (c != ':' && !this.isNameChar(c)) {
                break;
            }
            ++count;
        }
        return count;
    }
    
    protected final String parseSystemId(final char quoteChar, final boolean convertLFs, final String errorMsg) throws XMLStreamException {
        char[] buf = this.getNameBuffer(-1);
        int ptr = 0;
        while (true) {
            char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg);
            if (c == quoteChar) {
                break;
            }
            if (c == '\n') {
                this.markLF();
            }
            else if (c == '\r') {
                if (this.peekNext() == 10) {
                    ++this.mInputPtr;
                    if (!convertLFs) {
                        if (ptr >= buf.length) {
                            buf = this.expandBy50Pct(buf);
                        }
                        buf[ptr++] = '\r';
                    }
                    c = '\n';
                }
                else if (convertLFs) {
                    c = '\n';
                }
            }
            if (ptr >= buf.length) {
                buf = this.expandBy50Pct(buf);
            }
            buf[ptr++] = c;
        }
        return (ptr == 0) ? "" : new String(buf, 0, ptr);
    }
    
    protected final String parsePublicId(final char quoteChar, final String errorMsg) throws XMLStreamException {
        char[] buf = this.getNameBuffer(-1);
        int ptr = 0;
        boolean spaceToAdd = false;
        while (true) {
            final char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg);
            if (c == quoteChar) {
                break;
            }
            if (c == '\n') {
                this.markLF();
                spaceToAdd = true;
            }
            else if (c == '\r') {
                if (this.peekNext() == 10) {
                    ++this.mInputPtr;
                }
                spaceToAdd = true;
            }
            else if (c == ' ') {
                spaceToAdd = true;
            }
            else {
                if (c >= '\u0080' || StreamScanner.sPubidValidity[c] != 1) {
                    this.throwUnexpectedChar(c, " in public identifier");
                }
                if (ptr >= buf.length) {
                    buf = this.expandBy50Pct(buf);
                }
                if (spaceToAdd) {
                    if (c == ' ') {
                        continue;
                    }
                    spaceToAdd = false;
                    if (ptr > 0) {
                        buf[ptr++] = ' ';
                        if (ptr >= buf.length) {
                            buf = this.expandBy50Pct(buf);
                        }
                    }
                }
                buf[ptr++] = c;
            }
        }
        return (ptr == 0) ? "" : new String(buf, 0, ptr);
    }
    
    protected final void parseUntil(final TextBuffer tb, final char endChar, final boolean convertLFs, final String errorMsg) throws XMLStreamException {
        if (this.mInputPtr >= this.mInputEnd) {
            this.loadMore(errorMsg);
        }
        char[] inputBuf = null;
        int startPtr = 0;
        int ptr = 0;
    Block_3:
        while (true) {
            inputBuf = this.mInputBuffer;
            int inputLen = this.mInputEnd;
            ptr = (startPtr = this.mInputPtr);
            while (ptr < inputLen) {
                char c = inputBuf[ptr++];
                if (c == endChar) {
                    break Block_3;
                }
                if (c == '\n') {
                    this.mInputPtr = ptr;
                    this.markLF();
                }
                else {
                    if (c != '\r') {
                        continue;
                    }
                    if (!convertLFs && ptr < inputLen) {
                        if (inputBuf[ptr] == '\n') {
                            ++ptr;
                        }
                        this.mInputPtr = ptr;
                        this.markLF();
                    }
                    else {
                        final int thisLen = ptr - startPtr - 1;
                        if (thisLen > 0) {
                            tb.append(inputBuf, startPtr, thisLen);
                        }
                        this.mInputPtr = ptr;
                        c = this.getNextChar(errorMsg);
                        if (c != '\n') {
                            --this.mInputPtr;
                            tb.append(convertLFs ? '\n' : '\r');
                        }
                        else if (convertLFs) {
                            tb.append('\n');
                        }
                        else {
                            tb.append('\r');
                            tb.append('\n');
                        }
                        ptr = (startPtr = this.mInputPtr);
                        this.markLF();
                    }
                }
            }
            final int thisLen2 = ptr - startPtr;
            if (thisLen2 > 0) {
                tb.append(inputBuf, startPtr, thisLen2);
            }
            this.loadMore(errorMsg);
            ptr = (startPtr = this.mInputPtr);
            inputBuf = this.mInputBuffer;
            inputLen = this.mInputEnd;
        }
        final int thisLen = ptr - startPtr - 1;
        if (thisLen > 0) {
            tb.append(inputBuf, startPtr, thisLen);
        }
        this.mInputPtr = ptr;
    }
    
    private int resolveCharEnt(final StringBuffer originalCharacters) throws XMLStreamException {
        int value = 0;
        char c = this.getNextChar(" in entity reference");
        if (originalCharacters != null) {
            originalCharacters.append(c);
        }
        if (c == 'x') {
            while (true) {
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in entity reference"));
                if (c == ';') {
                    break;
                }
                if (originalCharacters != null) {
                    originalCharacters.append(c);
                }
                value <<= 4;
                if (c <= '9' && c >= '0') {
                    value += c - '0';
                }
                else if (c >= 'a' && c <= 'f') {
                    value += 10 + (c - 'a');
                }
                else if (c >= 'A' && c <= 'F') {
                    value += 10 + (c - 'A');
                }
                else {
                    this.throwUnexpectedChar(c, "; expected a hex digit (0-9a-fA-F).");
                }
                if (value <= 1114111) {
                    continue;
                }
                this.reportUnicodeOverflow();
            }
        }
        else {
            while (c != ';') {
                if (c <= '9' && c >= '0') {
                    value = value * 10 + (c - '0');
                    if (value > 1114111) {
                        this.reportUnicodeOverflow();
                    }
                }
                else {
                    this.throwUnexpectedChar(c, "; expected a decimal number.");
                }
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(" in entity reference"));
                if (originalCharacters != null && c != ';') {
                    originalCharacters.append(c);
                }
            }
        }
        this.validateChar(value);
        return value;
    }
    
    private final void validateChar(final int value) throws XMLStreamException {
        if (value >= 55296) {
            if (value < 57344) {
                this.reportIllegalChar(value);
            }
            if (value > 65535) {
                if (value > 1114111) {
                    this.reportUnicodeOverflow();
                }
            }
            else if (value >= 65534) {
                this.reportIllegalChar(value);
            }
        }
        else if (value < 32) {
            if (value == 0) {
                this.throwParseError("Invalid character reference: null character not allowed in XML content.");
            }
            if (!this.mXml11 && value != 9 && value != 10 && value != 13) {
                this.reportIllegalChar(value);
            }
        }
    }
    
    protected final char[] getNameBuffer(final int minSize) {
        char[] buf = this.mNameBuffer;
        if (buf == null) {
            buf = (this.mNameBuffer = new char[(minSize > 48) ? (minSize + 16) : 64]);
        }
        else if (minSize >= buf.length) {
            int len = buf.length;
            len += len >> 1;
            buf = (this.mNameBuffer = new char[(minSize >= len) ? (minSize + 16) : len]);
        }
        return buf;
    }
    
    protected final char[] expandBy50Pct(final char[] buf) {
        final int len = buf.length;
        final char[] newBuf = new char[len + (len >> 1)];
        System.arraycopy(buf, 0, newBuf, 0, len);
        return newBuf;
    }
    
    private void throwNsColonException(final String name) throws XMLStreamException {
        this.throwParseError("Illegal name \"{0}\" (PI target, entity/notation name): can not contain a colon (XML Namespaces 1.0#6)", name, null);
    }
    
    private void throwRecursionError(final String entityName) throws XMLStreamException {
        this.throwParseError("Illegal entity expansion: entity \"{0}\" expands itself recursively.", entityName, null);
    }
    
    private void reportUnicodeOverflow() throws XMLStreamException {
        this.throwParseError("Illegal character entity: value higher than max allowed (0x{0})", Integer.toHexString(1114111), null);
    }
    
    private void reportIllegalChar(final int value) throws XMLStreamException {
        this.throwParseError("Illegal character entity: expansion character (code 0x{0}", Integer.toHexString(value), null);
    }
    
    protected void verifyLimit(final String type, final long maxValue, final long currentValue) throws XMLStreamException {
        if (currentValue > maxValue) {
            throw this.constructLimitViolation(type, maxValue);
        }
    }
    
    protected XMLStreamException constructLimitViolation(final String type, final long limit) throws XMLStreamException {
        return new XMLStreamException(type + " limit (" + limit + ") exceeded");
    }
    
    static {
        (sCharValidity = new byte[256])[95] = 1;
        for (int i = 0, last = 25; i <= last; ++i) {
            StreamScanner.sCharValidity[65 + i] = 1;
            StreamScanner.sCharValidity[97 + i] = 1;
        }
        for (int i = 192; i < 246; ++i) {
            StreamScanner.sCharValidity[i] = 1;
        }
        StreamScanner.sCharValidity[215] = 0;
        StreamScanner.sCharValidity[247] = 0;
        StreamScanner.sCharValidity[45] = -1;
        StreamScanner.sCharValidity[46] = -1;
        StreamScanner.sCharValidity[183] = -1;
        for (int i = 48; i <= 57; ++i) {
            StreamScanner.sCharValidity[i] = -1;
        }
        sPubidValidity = new byte[128];
        for (int i = 0, last = 25; i <= last; ++i) {
            StreamScanner.sPubidValidity[65 + i] = 1;
            StreamScanner.sPubidValidity[97 + i] = 1;
        }
        for (int i = 48; i <= 57; ++i) {
            StreamScanner.sPubidValidity[i] = 1;
        }
        StreamScanner.sPubidValidity[10] = 1;
        StreamScanner.sPubidValidity[13] = 1;
        StreamScanner.sPubidValidity[32] = 1;
        StreamScanner.sPubidValidity[45] = 1;
        StreamScanner.sPubidValidity[39] = 1;
        StreamScanner.sPubidValidity[40] = 1;
        StreamScanner.sPubidValidity[41] = 1;
        StreamScanner.sPubidValidity[43] = 1;
        StreamScanner.sPubidValidity[44] = 1;
        StreamScanner.sPubidValidity[46] = 1;
        StreamScanner.sPubidValidity[47] = 1;
        StreamScanner.sPubidValidity[58] = 1;
        StreamScanner.sPubidValidity[61] = 1;
        StreamScanner.sPubidValidity[63] = 1;
        StreamScanner.sPubidValidity[59] = 1;
        StreamScanner.sPubidValidity[33] = 1;
        StreamScanner.sPubidValidity[42] = 1;
        StreamScanner.sPubidValidity[35] = 1;
        StreamScanner.sPubidValidity[64] = 1;
        StreamScanner.sPubidValidity[36] = 1;
        StreamScanner.sPubidValidity[95] = 1;
        StreamScanner.sPubidValidity[37] = 1;
    }
}
