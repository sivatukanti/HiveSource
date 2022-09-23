// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import org.codehaus.stax2.validation.XMLValidationProblem;
import java.util.HashSet;
import com.ctc.wstx.ent.UnparsedExtEntity;
import com.ctc.wstx.ent.ParsedExtEntity;
import java.util.Collection;
import java.util.ArrayList;
import java.util.TreeSet;
import com.ctc.wstx.util.WordResolver;
import java.text.MessageFormat;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.InternCache;
import com.ctc.wstx.evt.WNotationDeclaration;
import java.net.URL;
import javax.xml.stream.XMLReporter;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.ent.IntEntity;
import com.ctc.wstx.cfg.ErrorConsts;
import java.io.IOException;
import com.ctc.wstx.util.SymbolTable;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.util.TextBuffer;
import java.util.LinkedHashMap;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import javax.xml.stream.events.NotationDeclaration;
import java.util.Set;
import com.ctc.wstx.ent.EntityDecl;
import java.util.HashMap;

public class FullDTDReader extends MinimalDTDReader
{
    static final boolean INTERN_SHARED_NAMES = false;
    static final Boolean ENTITY_EXP_GE;
    static final Boolean ENTITY_EXP_PE;
    final int mConfigFlags;
    final boolean mCfgSupportDTDPP;
    final boolean mCfgFullyValidating;
    HashMap<String, EntityDecl> mParamEntities;
    final HashMap<String, EntityDecl> mPredefdPEs;
    Set<String> mRefdPEs;
    HashMap<String, EntityDecl> mGeneralEntities;
    final HashMap<String, EntityDecl> mPredefdGEs;
    Set<String> mRefdGEs;
    boolean mUsesPredefdEntities;
    HashMap<String, NotationDeclaration> mNotations;
    final HashMap<String, NotationDeclaration> mPredefdNotations;
    boolean mUsesPredefdNotations;
    HashMap<String, Location> mNotationForwardRefs;
    HashMap<PrefixedName, PrefixedName> mSharedNames;
    LinkedHashMap<PrefixedName, DTDElement> mElements;
    HashMap<String, String> mSharedEnumValues;
    DefaultAttrValue mCurrAttrDefault;
    boolean mExpandingPE;
    TextBuffer mValueBuffer;
    int mIncludeCount;
    boolean mCheckForbiddenPEs;
    String mCurrDeclaration;
    boolean mAnyDTDppFeatures;
    String mDefaultNsURI;
    HashMap<String, String> mNamespaces;
    DTDWriter mFlattenWriter;
    final DTDEventListener mEventListener;
    transient TextBuffer mTextBuffer;
    final PrefixedName mAccessKey;
    
    private FullDTDReader(final WstxInputSource input, final ReaderConfig cfg, final boolean constructFully, final int xmlVersion) {
        this(input, cfg, false, null, constructFully, xmlVersion);
    }
    
    private FullDTDReader(final WstxInputSource input, final ReaderConfig cfg, final DTDSubset intSubset, final boolean constructFully, final int xmlVersion) {
        this(input, cfg, true, intSubset, constructFully, xmlVersion);
        input.initInputLocation(this, this.mCurrDepth, 0);
    }
    
    private FullDTDReader(final WstxInputSource input, final ReaderConfig cfg, final boolean isExt, final DTDSubset intSubset, final boolean constructFully, final int xmlVersion) {
        super(input, cfg, isExt);
        this.mUsesPredefdEntities = false;
        this.mUsesPredefdNotations = false;
        this.mSharedNames = null;
        this.mSharedEnumValues = null;
        this.mCurrAttrDefault = null;
        this.mExpandingPE = false;
        this.mValueBuffer = null;
        this.mIncludeCount = 0;
        this.mCheckForbiddenPEs = false;
        this.mAnyDTDppFeatures = false;
        this.mDefaultNsURI = "";
        this.mNamespaces = null;
        this.mFlattenWriter = null;
        this.mTextBuffer = null;
        this.mAccessKey = new PrefixedName(null, null);
        this.mDocXmlVersion = xmlVersion;
        this.mXml11 = cfg.isXml11();
        final int cfgFlags = cfg.getConfigFlags();
        this.mConfigFlags = cfgFlags;
        this.mCfgSupportDTDPP = ((cfgFlags & 0x80000) != 0x0);
        this.mCfgFullyValidating = constructFully;
        this.mUsesPredefdEntities = false;
        this.mParamEntities = null;
        this.mRefdPEs = null;
        this.mRefdGEs = null;
        this.mGeneralEntities = null;
        final HashMap<String, EntityDecl> pes = (intSubset == null) ? null : intSubset.getParameterEntityMap();
        if (pes == null || pes.isEmpty()) {
            this.mPredefdPEs = null;
        }
        else {
            this.mPredefdPEs = pes;
        }
        final HashMap<String, EntityDecl> ges = (intSubset == null) ? null : intSubset.getGeneralEntityMap();
        if (ges == null || ges.isEmpty()) {
            this.mPredefdGEs = null;
        }
        else {
            this.mPredefdGEs = ges;
        }
        final HashMap<String, NotationDeclaration> not = (intSubset == null) ? null : intSubset.getNotationMap();
        if (not == null || not.isEmpty()) {
            this.mPredefdNotations = null;
        }
        else {
            this.mPredefdNotations = not;
        }
        this.mEventListener = this.mConfig.getDTDEventListener();
    }
    
    public static DTDSubset readInternalSubset(final WstxInputData srcData, final WstxInputSource input, final ReaderConfig cfg, final boolean constructFully, final int xmlVersion) throws XMLStreamException {
        final FullDTDReader r = new FullDTDReader(input, cfg, constructFully, xmlVersion);
        r.copyBufferStateFrom(srcData);
        DTDSubset ss;
        try {
            ss = r.parseDTD();
        }
        finally {
            srcData.copyBufferStateFrom(r);
        }
        return ss;
    }
    
    public static DTDSubset readExternalSubset(final WstxInputSource src, final ReaderConfig cfg, final DTDSubset intSubset, final boolean constructFully, final int xmlVersion) throws XMLStreamException {
        final FullDTDReader r = new FullDTDReader(src, cfg, intSubset, constructFully, xmlVersion);
        return r.parseDTD();
    }
    
    public static DTDSubset flattenExternalSubset(final WstxInputSource src, final Writer flattenWriter, final boolean inclComments, final boolean inclConditionals, final boolean inclPEs) throws IOException, XMLStreamException {
        ReaderConfig cfg = ReaderConfig.createFullDefaults();
        cfg = cfg.createNonShared(new SymbolTable());
        final FullDTDReader r = new FullDTDReader(src, cfg, null, true, 0);
        r.setFlattenWriter(flattenWriter, inclComments, inclConditionals, inclPEs);
        final DTDSubset ss = r.parseDTD();
        r.flushFlattenWriter();
        flattenWriter.flush();
        return ss;
    }
    
    private TextBuffer getTextBuffer() {
        if (this.mTextBuffer == null) {
            (this.mTextBuffer = TextBuffer.createTemporaryBuffer()).resetInitialized();
        }
        else {
            this.mTextBuffer.resetWithEmpty();
        }
        return this.mTextBuffer;
    }
    
    public void setFlattenWriter(final Writer w, final boolean inclComments, final boolean inclConditionals, final boolean inclPEs) {
        this.mFlattenWriter = new DTDWriter(w, inclComments, inclConditionals, inclPEs);
    }
    
    private void flushFlattenWriter() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
    }
    
    @Override
    public EntityDecl findEntity(final String entName) {
        if (this.mPredefdGEs != null) {
            final EntityDecl decl = this.mPredefdGEs.get(entName);
            if (decl != null) {
                return decl;
            }
        }
        return this.mGeneralEntities.get(entName);
    }
    
    protected DTDSubset parseDTD() throws XMLStreamException {
        while (true) {
            this.mCheckForbiddenPEs = false;
            final int i = this.getNextAfterWS();
            if (i < 0) {
                if (this.mIsExternal) {
                    break;
                }
                this.throwUnexpectedEOF(" in internal DTD subset");
            }
            if (i == 37) {
                this.expandPE();
            }
            else {
                this.mTokenInputTotal = this.mCurrInputProcessed + this.mInputPtr;
                this.mTokenInputRow = this.mCurrInputRow;
                this.mTokenInputCol = this.mInputPtr - this.mCurrInputRowStart;
                if (i == 60) {
                    this.mCheckForbiddenPEs = (!this.mIsExternal && this.mInput == this.mRootInput);
                    if (this.mFlattenWriter == null) {
                        this.parseDirective();
                    }
                    else {
                        this.parseDirectiveFlattened();
                    }
                }
                else {
                    if (i == 93) {
                        if (this.mIncludeCount == 0 && !this.mIsExternal) {
                            break;
                        }
                        if (this.mIncludeCount > 0) {
                            final boolean suppress = this.mFlattenWriter != null && !this.mFlattenWriter.includeConditionals();
                            if (suppress) {
                                this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
                                this.mFlattenWriter.disableOutput();
                            }
                            try {
                                char c = this.dtdNextFromCurr();
                                if (c == ']') {
                                    c = this.dtdNextFromCurr();
                                    if (c == '>') {
                                        --this.mIncludeCount;
                                        continue;
                                    }
                                }
                                this.throwDTDUnexpectedChar(c, "; expected ']]>' to close conditional include section");
                            }
                            finally {
                                if (suppress) {
                                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                                }
                            }
                        }
                    }
                    if (this.mIsExternal) {
                        this.throwDTDUnexpectedChar(i, "; expected a '<' to start a directive");
                    }
                    this.throwDTDUnexpectedChar(i, "; expected a '<' to start a directive, or \"]>\" to end internal subset");
                }
            }
        }
        if (this.mIncludeCount > 0) {
            final String suffix = (this.mIncludeCount == 1) ? "an INCLUDE block" : ("" + this.mIncludeCount + " INCLUDE blocks");
            this.throwUnexpectedEOF(this.getErrorMsg() + "; expected closing marker for " + suffix);
        }
        if (this.mNotationForwardRefs != null && this.mNotationForwardRefs.size() > 0) {
            this._reportUndefinedNotationRefs();
        }
        DTDSubset ss;
        if (this.mIsExternal) {
            final boolean cachable = !this.mUsesPredefdEntities && !this.mUsesPredefdNotations;
            ss = DTDSubsetImpl.constructInstance(cachable, this.mGeneralEntities, this.mRefdGEs, null, this.mRefdPEs, this.mNotations, this.mElements, this.mCfgFullyValidating);
        }
        else {
            ss = DTDSubsetImpl.constructInstance(false, this.mGeneralEntities, null, this.mParamEntities, null, this.mNotations, this.mElements, this.mCfgFullyValidating);
        }
        return ss;
    }
    
    protected void parseDirective() throws XMLStreamException {
        char c = this.dtdNextFromCurr();
        if (c == '?') {
            this.readPI();
            return;
        }
        if (c != '!') {
            this.throwDTDUnexpectedChar(c, "; expected '!' to start a directive");
        }
        c = this.dtdNextFromCurr();
        if (c == '-') {
            c = this.dtdNextFromCurr();
            if (c != '-') {
                this.throwDTDUnexpectedChar(c, "; expected '-' for a comment");
            }
            if (this.mEventListener != null && this.mEventListener.dtdReportComments()) {
                this.readComment(this.mEventListener);
            }
            else {
                this.skipComment();
            }
        }
        else if (c == '[') {
            this.checkInclusion();
        }
        else if (c >= 'A' && c <= 'Z') {
            this.handleDeclaration(c);
        }
        else {
            this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
    }
    
    protected void parseDirectiveFlattened() throws XMLStreamException {
        this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
        this.mFlattenWriter.disableOutput();
        char c = this.dtdNextFromCurr();
        if (c == '?') {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<?");
            this.readPI();
            return;
        }
        if (c != '!') {
            this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
        }
        c = this.dtdNextFromCurr();
        if (c == '-') {
            c = this.dtdNextFromCurr();
            if (c != '-') {
                this.throwDTDUnexpectedChar(c, "; expected '-' for a comment");
            }
            final boolean comm = this.mFlattenWriter.includeComments();
            if (comm) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<!--");
            }
            try {
                this.skipComment();
            }
            finally {
                if (!comm) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        }
        else if (c == '[') {
            final boolean cond = this.mFlattenWriter.includeConditionals();
            if (cond) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<![");
            }
            try {
                this.checkInclusion();
            }
            finally {
                if (!cond) {
                    this.mFlattenWriter.enableOutput(this.mInputPtr);
                }
            }
        }
        else {
            final boolean filterPEs = c == 'E' && !this.mFlattenWriter.includeParamEntities();
            if (filterPEs) {
                this.handleSuppressedDeclaration();
            }
            else if (c >= 'A' && c <= 'Z') {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
                this.mFlattenWriter.output("<!");
                this.mFlattenWriter.output(c);
                this.handleDeclaration(c);
            }
            else {
                this.throwDTDUnexpectedChar(c, ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD);
            }
        }
    }
    
    @Override
    protected void initInputSource(final WstxInputSource newInput, final boolean isExt, final String entityId) throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr);
            this.mFlattenWriter.disableOutput();
            try {
                super.initInputSource(newInput, isExt, entityId);
            }
            finally {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        }
        else {
            super.initInputSource(newInput, isExt, entityId);
        }
    }
    
    @Override
    protected boolean loadMore() throws XMLStreamException {
        WstxInputSource input = this.mInput;
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        do {
            this.mCurrInputProcessed += this.mInputEnd;
            this.mCurrInputRowStart -= this.mInputEnd;
            try {
                final int count = input.readInto(this);
                if (count > 0) {
                    if (this.mFlattenWriter != null) {
                        this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                    }
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
            if (this.mFlattenWriter != null) {
                this.mFlattenWriter.setFlattenStart(this.mInputPtr);
            }
            this.mInputTopDepth = input.getScopeId();
            if (this.mNormalizeLFs) {
                continue;
            }
            this.mNormalizeLFs = !input.fromInternalEntity();
        } while (this.mInputPtr >= this.mInputEnd);
        return true;
    }
    
    @Override
    protected boolean loadMoreFromCurrent() throws XMLStreamException {
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        this.mCurrInputProcessed += this.mInputEnd;
        this.mCurrInputRowStart -= this.mInputEnd;
        try {
            final int count = this.mInput.readInto(this);
            if (count > 0) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(this.mInputPtr);
                }
                return true;
            }
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
        return false;
    }
    
    @Override
    protected boolean ensureInput(final int minAmount) throws XMLStreamException {
        final int currAmount = this.mInputEnd - this.mInputPtr;
        if (currAmount >= minAmount) {
            return true;
        }
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputEnd);
        }
        try {
            if (this.mInput.readMore(this, minAmount)) {
                if (this.mFlattenWriter != null) {
                    this.mFlattenWriter.setFlattenStart(currAmount);
                }
                return true;
            }
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
        return false;
    }
    
    private void loadMoreScoped(final WstxInputSource currScope, final String entityName, final Location loc) throws XMLStreamException {
        final boolean check = this.mInput == currScope;
        this.loadMore(this.getErrorMsg());
        if (check && this.mInput != currScope) {
            this._reportWFCViolation("Unterminated entity value for entity '" + entityName + "' (definition started at " + loc + ")");
        }
    }
    
    private char dtdNextIfAvailable() throws XMLStreamException {
        char c;
        if (this.mInputPtr < this.mInputEnd) {
            c = this.mInputBuffer[this.mInputPtr++];
        }
        else {
            final int i = this.peekNext();
            if (i < 0) {
                return '\0';
            }
            ++this.mInputPtr;
            c = (char)i;
        }
        if (c == '\0') {
            this.throwNullChar();
        }
        return c;
    }
    
    private char getNextExpanded() throws XMLStreamException {
        char c;
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg()));
            if (c != '%') {
                break;
            }
            this.expandPE();
        }
        return c;
    }
    
    private char skipDtdWs(final boolean handlePEs) throws XMLStreamException {
        char c;
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg()));
            if (c > ' ') {
                if (c != '%' || !handlePEs) {
                    break;
                }
                this.expandPE();
            }
            else if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
            }
            else {
                if (c == ' ' || c == '\t') {
                    continue;
                }
                this.throwInvalidSpace(c);
            }
        }
        return c;
    }
    
    private char skipObligatoryDtdWs() throws XMLStreamException {
        final int i = this.peekNext();
        char c;
        if (i == -1) {
            c = this.getNextChar(this.getErrorMsg());
            if (c > ' ' && c != '%') {
                return c;
            }
        }
        else {
            c = this.mInputBuffer[this.mInputPtr++];
            if (c > ' ' && c != '%') {
                this.throwDTDUnexpectedChar(c, "; expected a separating white space");
            }
        }
        while (true) {
            if (c == '%') {
                this.expandPE();
            }
            else {
                if (c > ' ') {
                    break;
                }
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                }
                else if (c != ' ' && c != '\t') {
                    this.throwInvalidSpace(c);
                }
            }
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg()));
        }
        return c;
    }
    
    private void expandPE() throws XMLStreamException {
        if (this.mCheckForbiddenPEs) {
            this.throwForbiddenPE();
        }
        char c;
        String id;
        if (this.mFlattenWriter != null) {
            this.mFlattenWriter.flush(this.mInputBuffer, this.mInputPtr - 1);
            this.mFlattenWriter.disableOutput();
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            id = this.readDTDName(c);
            try {
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            }
            finally {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        }
        else {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            id = this.readDTDName(c);
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
        }
        if (c != ';') {
            this.throwDTDUnexpectedChar(c, "; expected ';' to end parameter entity name");
        }
        this.expandEntity(id, this.mExpandingPE = true, FullDTDReader.ENTITY_EXP_PE);
    }
    
    protected String checkDTDKeyword(final String exp) throws XMLStreamException {
        int i = 0;
        final int len = exp.length();
        char c = ' ';
        while (i < len) {
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            }
            else {
                c = this.dtdNextIfAvailable();
                if (c == '\0') {
                    return exp.substring(0, i);
                }
            }
            if (c != exp.charAt(i)) {
                break;
            }
            ++i;
        }
        if (i == len) {
            c = this.dtdNextIfAvailable();
            if (c == '\0') {
                return null;
            }
            if (!this.isNameChar(c)) {
                --this.mInputPtr;
                return null;
            }
        }
        final StringBuilder sb = new StringBuilder(exp.substring(0, i));
        sb.append(c);
        while (true) {
            c = this.dtdNextIfAvailable();
            if (c == '\0') {
                break;
            }
            if (!this.isNameChar(c) && c != ':') {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    protected String readDTDKeyword(final String prefix) throws XMLStreamException {
        final StringBuilder sb = new StringBuilder(prefix);
        while (true) {
            char c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            }
            else {
                c = this.dtdNextIfAvailable();
                if (c == '\0') {
                    break;
                }
            }
            if (!this.isNameChar(c) && c != ':') {
                --this.mInputPtr;
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    private boolean checkPublicSystemKeyword(final char c) throws XMLStreamException {
        String errId;
        if (c == 'P') {
            errId = this.checkDTDKeyword("UBLIC");
            if (errId == null) {
                return true;
            }
            errId = "P" + errId;
        }
        else if (c == 'S') {
            errId = this.checkDTDKeyword("YSTEM");
            if (errId == null) {
                return false;
            }
            errId = "S" + errId;
        }
        else {
            if (!this.isNameStartChar(c)) {
                this.throwDTDUnexpectedChar(c, "; expected 'PUBLIC' or 'SYSTEM' keyword");
            }
            errId = this.readDTDKeyword(String.valueOf(c));
        }
        this._reportWFCViolation("Unrecognized keyword '" + errId + "'; expected 'PUBLIC' or 'SYSTEM'");
        return false;
    }
    
    private String readDTDName(final char c) throws XMLStreamException {
        if (!this.isNameStartChar(c)) {
            this.throwDTDUnexpectedChar(c, "; expected an identifier");
        }
        return this.parseFullName(c);
    }
    
    private String readDTDLocalName(final char c, final boolean checkChar) throws XMLStreamException {
        if (checkChar && !this.isNameStartChar(c)) {
            this.throwDTDUnexpectedChar(c, "; expected an identifier");
        }
        return this.parseLocalName(c);
    }
    
    private String readDTDNmtoken(char c) throws XMLStreamException {
        char[] outBuf = this.getNameBuffer(64);
        int outLen = outBuf.length;
        int outPtr = 0;
        while (this.isNameChar(c) || c == ':') {
            if (outPtr >= outLen) {
                outBuf = this.expandBy50Pct(outBuf);
                outLen = outBuf.length;
            }
            outBuf[outPtr++] = c;
            if (this.mInputPtr < this.mInputEnd) {
                c = this.mInputBuffer[this.mInputPtr++];
            }
            else {
                c = this.dtdNextIfAvailable();
                if (c == '\0') {
                    return new String(outBuf, 0, outPtr);
                }
                continue;
            }
        }
        if (outPtr == 0) {
            this.throwDTDUnexpectedChar(c, "; expected a NMTOKEN character to start a NMTOKEN");
        }
        --this.mInputPtr;
        return new String(outBuf, 0, outPtr);
    }
    
    private PrefixedName readDTDQName(final char firstChar) throws XMLStreamException {
        String prefix;
        String localName;
        if (!this.mCfgNsEnabled) {
            prefix = null;
            localName = this.parseFullName(firstChar);
        }
        else {
            localName = this.parseLocalName(firstChar);
            char c = this.dtdNextIfAvailable();
            if (c == '\0') {
                prefix = null;
            }
            else if (c == ':') {
                prefix = localName;
                c = this.dtdNextFromCurr();
                localName = this.parseLocalName(c);
            }
            else {
                prefix = null;
                --this.mInputPtr;
            }
        }
        return this.findSharedName(prefix, localName);
    }
    
    private char readArity() throws XMLStreamException {
        final char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
        if (c == '?' || c == '*' || c == '+') {
            return c;
        }
        --this.mInputPtr;
        return ' ';
    }
    
    private char[] parseEntityValue(final String id, final Location loc, final char quoteChar) throws XMLStreamException {
        final WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = tb.getCurrentSegmentSize();
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                this.loadMoreScoped(currScope, id, loc);
            }
            char c = this.mInputBuffer[this.mInputPtr++];
            if (c < '?') {
                if (c == quoteChar) {
                    if (this.mInput == currScope) {
                        break;
                    }
                }
                else if (c == '&') {
                    int d = this.resolveCharOnlyEntity(false);
                    if (d != 0) {
                        if (d <= 65535) {
                            c = (char)d;
                        }
                        else {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            d -= 65536;
                            outBuf[outPtr++] = (char)((d >> 10) + 55296);
                            c = (char)((d & 0x3FF) + 56320);
                        }
                    }
                    else {
                        boolean first = true;
                        while (true) {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            outBuf[outPtr++] = c;
                            if (this.mInputPtr >= this.mInputEnd) {
                                this.loadMoreScoped(currScope, id, loc);
                            }
                            c = this.mInputBuffer[this.mInputPtr++];
                            if (c == ';') {
                                break;
                            }
                            if (first) {
                                first = false;
                                if (this.isNameStartChar(c)) {
                                    continue;
                                }
                            }
                            else if (this.isNameChar(c)) {
                                continue;
                            }
                            if (c == ':' && !this.mCfgNsEnabled) {
                                continue;
                            }
                            if (first) {
                                this.throwDTDUnexpectedChar(c, "; expected entity name after '&'");
                            }
                            this.throwDTDUnexpectedChar(c, "; expected semi-colon after entity name");
                        }
                    }
                }
                else {
                    if (c == '%') {
                        this.expandPE();
                        continue;
                    }
                    if (c < ' ') {
                        if (c == '\n') {
                            this.markLF();
                        }
                        else if (c == '\r') {
                            if (this.skipCRLF(c)) {
                                if (!this.mNormalizeLFs) {
                                    if (outPtr >= outBuf.length) {
                                        outBuf = tb.finishCurrentSegment();
                                        outPtr = 0;
                                    }
                                    outBuf[outPtr++] = c;
                                }
                                c = '\n';
                            }
                            else if (this.mNormalizeLFs) {
                                c = '\n';
                            }
                        }
                        else if (c != '\t') {
                            this.throwInvalidSpace(c);
                        }
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
        tb.setCurrentLength(outPtr);
        char c = this.skipDtdWs(true);
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected closing '>' after ENTITY declaration");
        }
        final char[] result = tb.contentsAsArray();
        this.mValueBuffer = tb;
        return result;
    }
    
    private void parseAttrDefaultValue(final DefaultAttrValue defVal, final char quoteChar, final PrefixedName attrName, final Location loc, final boolean gotFixed) throws XMLStreamException {
        if (quoteChar != '\"' && quoteChar != '\'') {
            String msg = "; expected a single or double quote to enclose the default value";
            if (!gotFixed) {
                msg += ", or one of keywords (#REQUIRED, #IMPLIED, #FIXED)";
            }
            msg = msg + " (for attribute '" + attrName + "')";
            this.throwDTDUnexpectedChar(quoteChar, msg);
        }
        final WstxInputSource currScope = this.mInput;
        TextBuffer tb = this.mValueBuffer;
        if (tb == null) {
            tb = TextBuffer.createTemporaryBuffer();
        }
        tb.resetInitialized();
        int outPtr = 0;
        char[] outBuf = tb.getCurrentSegment();
        int outLen = outBuf.length;
        while (true) {
            if (this.mInputPtr >= this.mInputEnd) {
                final boolean check = this.mInput == currScope;
                this.loadMore(this.getErrorMsg());
                if (check && this.mInput != currScope) {
                    this._reportWFCViolation("Unterminated attribute default value for attribute '" + attrName + "' (definition started at " + loc + ")");
                }
            }
            char c = this.mInputBuffer[this.mInputPtr++];
            if (c < '?') {
                if (c <= ' ') {
                    if (c == '\n') {
                        this.markLF();
                    }
                    else if (c == '\r') {
                        c = this.getNextChar(" in attribute default value");
                        if (c != '\n') {
                            --this.mInputPtr;
                            c = (this.mNormalizeLFs ? '\n' : '\r');
                        }
                        this.markLF();
                    }
                    else if (c != ' ' && c != '\t') {
                        this.throwInvalidSpace(c);
                    }
                    c = ' ';
                }
                else if (c == quoteChar) {
                    if (this.mInput == currScope) {
                        break;
                    }
                }
                else if (c == '&') {
                    int d;
                    if (this.inputInBuffer() >= 3) {
                        d = this.resolveSimpleEntity(true);
                    }
                    else {
                        d = this.resolveCharOnlyEntity(true);
                    }
                    if (d == 0) {
                        c = this.getNextChar(" in entity reference");
                        final String id = this.parseEntityName(c);
                        try {
                            this.mCurrAttrDefault = defVal;
                            this.expandEntity(id, this.mExpandingPE = false, FullDTDReader.ENTITY_EXP_GE);
                        }
                        finally {
                            this.mCurrAttrDefault = null;
                        }
                        continue;
                    }
                    if (c > '\uffff') {
                        if (d <= 65535) {
                            c = (char)d;
                        }
                        else {
                            if (outPtr >= outBuf.length) {
                                outBuf = tb.finishCurrentSegment();
                                outPtr = 0;
                            }
                            d -= 65536;
                            outBuf[outPtr++] = (char)((d >> 10) + 55296);
                            c = (char)((d & 0x3FF) + 56320);
                        }
                    }
                }
                else if (c == '<') {
                    this.throwDTDUnexpectedChar(c, " in attribute default value");
                }
            }
            if (outPtr >= outLen) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
                outLen = outBuf.length;
            }
            outBuf[outPtr++] = c;
        }
        tb.setCurrentLength(outPtr);
        defVal.setValue(tb.contentsAsString());
        this.mValueBuffer = tb;
    }
    
    protected void readPI() throws XMLStreamException {
        final String target = this.parseFullName();
        if (target.length() == 0) {
            this._reportWFCViolation(ErrorConsts.ERR_WF_PI_MISSING_TARGET);
        }
        if (target.equalsIgnoreCase("xml")) {
            this._reportWFCViolation(ErrorConsts.ERR_WF_PI_XML_TARGET, target);
        }
        char c = this.dtdNextFromCurr();
        if (!WstxInputData.isSpaceChar(c)) {
            if (c != '?' || this.dtdNextFromCurr() != '>') {
                this.throwUnexpectedChar(c, ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE);
            }
            if (this.mEventListener != null) {
                this.mEventListener.dtdProcessingInstruction(target, "");
            }
        }
        else if (this.mEventListener == null) {
            while (true) {
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
                if (c == '?') {
                    do {
                        c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
                    } while (c == '?');
                    if (c == '>') {
                        break;
                    }
                }
                if (c < ' ') {
                    if (c == '\n' || c == '\r') {
                        this.skipCRLF(c);
                    }
                    else {
                        if (c == '\t') {
                            continue;
                        }
                        this.throwInvalidSpace(c);
                    }
                }
            }
        }
        else {
            while (c <= ' ') {
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                }
                else if (c != '\t' && c != ' ') {
                    this.throwInvalidSpace(c);
                }
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            }
            final TextBuffer tb = this.getTextBuffer();
            char[] outBuf = tb.getCurrentSegment();
            int outPtr = 0;
            while (true) {
                if (c == '?') {
                    while (true) {
                        c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
                        if (c != '?') {
                            break;
                        }
                        if (outPtr >= outBuf.length) {
                            outBuf = tb.finishCurrentSegment();
                            outPtr = 0;
                        }
                        outBuf[outPtr++] = c;
                    }
                    if (c == '>') {
                        break;
                    }
                    --this.mInputPtr;
                    c = '?';
                }
                else if (c < ' ') {
                    if (c == '\n' || c == '\r') {
                        this.skipCRLF(c);
                        c = '\n';
                    }
                    else if (c != '\t') {
                        this.throwInvalidSpace(c);
                    }
                }
                if (outPtr >= outBuf.length) {
                    outBuf = tb.finishCurrentSegment();
                    outPtr = 0;
                }
                outBuf[outPtr++] = c;
                c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr());
            }
            tb.setCurrentLength(outPtr);
            final String data = tb.contentsAsString();
            this.mEventListener.dtdProcessingInstruction(target, data);
        }
    }
    
    protected void readComment(final DTDEventListener l) throws XMLStreamException {
        final TextBuffer tb = this.getTextBuffer();
        char[] outBuf = tb.getCurrentSegment();
        int outPtr = 0;
        while (true) {
            char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c < ' ') {
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                    c = '\n';
                }
                else if (c != '\t') {
                    this.throwInvalidSpace(c);
                }
            }
            else if (c == '-') {
                c = this.dtdNextFromCurr();
                if (c == '-') {
                    break;
                }
                c = '-';
                --this.mInputPtr;
            }
            if (outPtr >= outBuf.length) {
                outBuf = tb.finishCurrentSegment();
                outPtr = 0;
            }
            outBuf[outPtr++] = c;
        }
        char c = this.dtdNextFromCurr();
        if (c != '>') {
            this.throwParseError(ErrorConsts.ERR_HYPHENS_IN_COMMENT);
        }
        tb.setCurrentLength(outPtr);
        tb.fireDtdCommentEvent(l);
    }
    
    private void checkInclusion() throws XMLStreamException {
        if (!this.mIsExternal && this.mInput == this.mRootInput) {
            this._reportWFCViolation("Internal DTD subset can not use (INCLUDE/IGNORE) directives (except via external entities)");
        }
        char c = this.skipDtdWs(true);
        String keyword;
        if (c != 'I') {
            keyword = this.readDTDKeyword(String.valueOf(c));
        }
        else {
            c = this.dtdNextFromCurr();
            if (c == 'G') {
                keyword = this.checkDTDKeyword("NORE");
                if (keyword == null) {
                    this.handleIgnored();
                    return;
                }
                keyword = "IG" + keyword;
            }
            else if (c == 'N') {
                keyword = this.checkDTDKeyword("CLUDE");
                if (keyword == null) {
                    this.handleIncluded();
                    return;
                }
                keyword = "IN" + keyword;
            }
            else {
                --this.mInputPtr;
                keyword = this.readDTDKeyword("I");
            }
        }
        this._reportWFCViolation("Unrecognized directive '" + keyword + "'; expected either 'IGNORE' or 'INCLUDE'");
    }
    
    private void handleIncluded() throws XMLStreamException {
        final char c = this.skipDtdWs(false);
        if (c != '[') {
            this.throwDTDUnexpectedChar(c, "; expected '[' to follow 'INCLUDE' directive");
        }
        ++this.mIncludeCount;
    }
    
    private void handleIgnored() throws XMLStreamException {
        char c = this.skipDtdWs(false);
        int count = 1;
        if (c != '[') {
            this.throwDTDUnexpectedChar(c, "; expected '[' to follow 'IGNORE' directive");
        }
        final String errorMsg = this.getErrorMsg();
        while (true) {
            c = ((this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(errorMsg));
            if (c < ' ') {
                if (c == '\n' || c == '\r') {
                    this.skipCRLF(c);
                }
                else {
                    if (c == '\t') {
                        continue;
                    }
                    this.throwInvalidSpace(c);
                }
            }
            else if (c == ']') {
                if (this.getNextChar(errorMsg) == ']' && this.getNextChar(errorMsg) == '>') {
                    if (--count < 1) {
                        break;
                    }
                    continue;
                }
                else {
                    --this.mInputPtr;
                }
            }
            else {
                if (c != '<') {
                    continue;
                }
                if (this.getNextChar(errorMsg) == '!' && this.getNextChar(errorMsg) == '[') {
                    ++count;
                }
                else {
                    --this.mInputPtr;
                }
            }
        }
    }
    
    private void _reportUndefinedNotationRefs() throws XMLStreamException {
        final int count = this.mNotationForwardRefs.size();
        final String id = this.mNotationForwardRefs.keySet().iterator().next();
        final String msg = "" + count + " referenced notation" + ((count == 1) ? "" : "s") + " undefined: first one '" + id + "'";
        this._reportVCViolation(msg);
    }
    
    private void _reportBadDirective(final String dir) throws XMLStreamException {
        String msg = "Unrecognized DTD directive '<!" + dir + " >'; expected ATTLIST, ELEMENT, ENTITY or NOTATION";
        if (this.mCfgSupportDTDPP) {
            msg += " (or, for DTD++, TARGETNS)";
        }
        this._reportWFCViolation(msg);
    }
    
    private void _reportVCViolation(final String msg) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            this.reportValidationProblem(msg, 2);
        }
        else {
            this.reportValidationProblem(msg, 1);
        }
    }
    
    private void _reportWFCViolation(final String msg) throws XMLStreamException {
        this.throwParseError(msg);
    }
    
    private void _reportWFCViolation(final String format, final Object arg) throws XMLStreamException {
        this.throwParseError(format, arg, null);
    }
    
    private void throwDTDElemError(final String msg, final Object elem) throws XMLStreamException {
        this._reportWFCViolation(this.elemDesc(elem) + ": " + msg);
    }
    
    private void throwDTDAttrError(final String msg, final DTDElement elem, final PrefixedName attrName) throws XMLStreamException {
        this._reportWFCViolation(this.attrDesc(elem, attrName) + ": " + msg);
    }
    
    private void throwDTDUnexpectedChar(final int i, final String extraMsg) throws XMLStreamException {
        if (extraMsg == null) {
            this.throwUnexpectedChar(i, this.getErrorMsg());
        }
        this.throwUnexpectedChar(i, this.getErrorMsg() + extraMsg);
    }
    
    private void throwForbiddenPE() throws XMLStreamException {
        this._reportWFCViolation("Can not have parameter entities in the internal subset, except for defining complete declarations (XML 1.0, #2.8, WFC 'PEs In Internal Subset')");
    }
    
    private String elemDesc(final Object elem) {
        return "Element <" + elem + ">)";
    }
    
    private String attrDesc(final Object elem, final PrefixedName attrName) {
        return "Attribute '" + attrName + "' (of element <" + elem + ">)";
    }
    
    private String entityDesc(final WstxInputSource input) {
        return "Entity &" + input.getEntityId() + ";";
    }
    
    private void handleDeclaration(char c) throws XMLStreamException {
        String keyw = null;
        this.mCurrDepth = 1;
        try {
            Label_0348: {
                if (c == 'A') {
                    keyw = this.checkDTDKeyword("TTLIST");
                    if (keyw == null) {
                        this.mCurrDeclaration = "ATTLIST";
                        this.handleAttlistDecl();
                        break Label_0348;
                    }
                    keyw = "A" + keyw;
                }
                else if (c == 'E') {
                    c = this.dtdNextFromCurr();
                    if (c == 'N') {
                        keyw = this.checkDTDKeyword("TITY");
                        if (keyw == null) {
                            this.mCurrDeclaration = "ENTITY";
                            this.handleEntityDecl(false);
                            break Label_0348;
                        }
                        keyw = "EN" + keyw;
                    }
                    else if (c == 'L') {
                        keyw = this.checkDTDKeyword("EMENT");
                        if (keyw == null) {
                            this.mCurrDeclaration = "ELEMENT";
                            this.handleElementDecl();
                            break Label_0348;
                        }
                        keyw = "EL" + keyw;
                    }
                    else {
                        keyw = this.readDTDKeyword("E" + c);
                    }
                }
                else if (c == 'N') {
                    keyw = this.checkDTDKeyword("OTATION");
                    if (keyw == null) {
                        this.mCurrDeclaration = "NOTATION";
                        this.handleNotationDecl();
                        break Label_0348;
                    }
                    keyw = "N" + keyw;
                }
                else if (c == 'T' && this.mCfgSupportDTDPP) {
                    keyw = this.checkDTDKeyword("ARGETNS");
                    if (keyw == null) {
                        this.mCurrDeclaration = "TARGETNS";
                        this.handleTargetNsDecl();
                        break Label_0348;
                    }
                    keyw = "T" + keyw;
                }
                else {
                    keyw = this.readDTDKeyword(String.valueOf(c));
                }
                this._reportBadDirective(keyw);
            }
            if (this.mInput.getScopeId() > 0) {
                this.handleGreedyEntityProblem(this.mInput);
            }
        }
        finally {
            this.mCurrDepth = 0;
            this.mCurrDeclaration = null;
        }
    }
    
    private void handleSuppressedDeclaration() throws XMLStreamException {
        final char c = this.dtdNextFromCurr();
        String keyw;
        if (c == 'N') {
            keyw = this.checkDTDKeyword("TITY");
            if (keyw == null) {
                this.handleEntityDecl(true);
                return;
            }
            keyw = "EN" + keyw;
            this.mFlattenWriter.enableOutput(this.mInputPtr);
        }
        else {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!E");
            this.mFlattenWriter.output(c);
            if (c == 'L') {
                keyw = this.checkDTDKeyword("EMENT");
                if (keyw == null) {
                    this.handleElementDecl();
                    return;
                }
                keyw = "EL" + keyw;
            }
            else {
                keyw = this.readDTDKeyword("E");
            }
        }
        this._reportBadDirective(keyw);
    }
    
    private void handleAttlistDecl() throws XMLStreamException {
        char c = this.skipObligatoryDtdWs();
        final PrefixedName elemName = this.readDTDQName(c);
        final Location loc = this.getStartLocation();
        final HashMap<PrefixedName, DTDElement> m = this.getElementMap();
        DTDElement elem = m.get(elemName);
        if (elem == null) {
            elem = DTDElement.createPlaceholder(this.mConfig, loc, elemName);
            m.put(elemName, elem);
        }
        int index = 0;
        while (true) {
            c = this.getNextExpanded();
            if (WstxInputData.isSpaceChar(c)) {
                --this.mInputPtr;
                c = this.skipDtdWs(true);
            }
            if (c == '>') {
                break;
            }
            this.handleAttrDecl(elem, c, index, loc);
            ++index;
        }
    }
    
    private void handleElementDecl() throws XMLStreamException {
        char c = this.skipObligatoryDtdWs();
        final PrefixedName elemName = this.readDTDQName(c);
        final Location loc = this.getStartLocation();
        c = this.skipObligatoryDtdWs();
        StructValidator val = null;
        int vldContent = 4;
        Label_0334: {
            if (c == '(') {
                c = this.skipDtdWs(true);
                if (c == '#') {
                    val = this.readMixedSpec(elemName, this.mCfgFullyValidating);
                    vldContent = 4;
                }
                else {
                    --this.mInputPtr;
                    final ContentSpec spec = this.readContentSpec(elemName, true, this.mCfgFullyValidating);
                    val = spec.getSimpleValidator();
                    if (val == null) {
                        val = new DFAValidator(DFAState.constructDFA(spec));
                    }
                    vldContent = 1;
                }
            }
            else if (this.isNameStartChar(c)) {
                String keyw = null;
                if (c == 'A') {
                    keyw = this.checkDTDKeyword("NY");
                    if (keyw == null) {
                        val = null;
                        vldContent = 4;
                        break Label_0334;
                    }
                    keyw = "A" + keyw;
                }
                else if (c == 'E') {
                    keyw = this.checkDTDKeyword("MPTY");
                    if (keyw == null) {
                        val = EmptyValidator.getPcdataInstance();
                        vldContent = 0;
                        break Label_0334;
                    }
                    keyw = "E" + keyw;
                }
                else {
                    --this.mInputPtr;
                    keyw = this.readDTDKeyword(String.valueOf(c));
                }
                this._reportWFCViolation("Unrecognized DTD content spec keyword '" + keyw + "' (for element <" + elemName + ">); expected ANY or EMPTY");
            }
            else {
                this.throwDTDUnexpectedChar(c, ": excepted '(' to start content specification for element <" + elemName + ">");
            }
        }
        c = this.skipDtdWs(true);
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected '>' to finish the element declaration for <" + elemName + ">");
        }
        final LinkedHashMap<PrefixedName, DTDElement> m = this.getElementMap();
        DTDElement oldElem = m.get(elemName);
        if (oldElem != null) {
            if (oldElem.isDefined()) {
                if (!this.mCfgFullyValidating) {
                    return;
                }
                DTDSubsetImpl.throwElementException(oldElem, loc);
            }
            oldElem = oldElem.define(loc, val, vldContent);
        }
        else {
            oldElem = DTDElement.createDefined(this.mConfig, loc, elemName, val, vldContent);
        }
        m.put(elemName, oldElem);
    }
    
    private void handleEntityDecl(final boolean suppressPEDecl) throws XMLStreamException {
        char c = this.dtdNextFromCurr();
        boolean gotSeparator = false;
        boolean isParam = false;
        while (true) {
            if (c == '%') {
                final char d = this.dtdNextIfAvailable();
                if (d == '\0' || WstxInputData.isSpaceChar(d)) {
                    isParam = true;
                    if (d == '\n' || c == '\r') {
                        this.skipCRLF(d);
                        break;
                    }
                    break;
                }
                else {
                    if (!this.isNameStartChar(d)) {
                        this.throwDTDUnexpectedChar(d, "; expected a space (for PE declaration) or PE reference name");
                    }
                    --this.mInputPtr;
                    gotSeparator = true;
                    this.expandPE();
                    c = this.dtdNextChar();
                }
            }
            else {
                if (!WstxInputData.isSpaceChar(c)) {
                    break;
                }
                gotSeparator = true;
                c = this.dtdNextFromCurr();
            }
        }
        if (!gotSeparator) {
            this.throwDTDUnexpectedChar(c, "; expected a space separating ENTITY keyword and entity name");
        }
        if (isParam) {
            c = this.skipDtdWs(true);
        }
        if (suppressPEDecl && !isParam) {
            this.mFlattenWriter.enableOutput(this.mInputPtr);
            this.mFlattenWriter.output("<!ENTITY ");
            this.mFlattenWriter.output(c);
        }
        final String id = this.readDTDName(c);
        final Location evtLoc = this.getStartLocation();
        EntityDecl ent;
        try {
            c = this.skipObligatoryDtdWs();
            if (c == '\'' || c == '\"') {
                this.dtdNextFromCurr();
                final Location contentLoc = this.getLastCharLocation();
                --this.mInputPtr;
                final char[] contents = this.parseEntityValue(id, contentLoc, c);
                try {
                    ent = new IntEntity(evtLoc, id, this.getSource(), contents, contentLoc);
                }
                catch (IOException e) {
                    throw new WstxIOException(e);
                }
            }
            else {
                if (!this.isNameStartChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected either quoted value, or keyword 'PUBLIC' or 'SYSTEM'");
                }
                ent = this.handleExternalEntityDecl(this.mInput, isParam, id, c, evtLoc);
            }
            if (this.mIsExternal) {
                ent.markAsExternallyDeclared();
            }
        }
        finally {
            if (suppressPEDecl && isParam) {
                this.mFlattenWriter.enableOutput(this.mInputPtr);
            }
        }
        HashMap<String, EntityDecl> m;
        if (isParam) {
            m = this.mParamEntities;
            if (m == null) {
                m = (this.mParamEntities = new HashMap<String, EntityDecl>());
            }
        }
        else {
            m = this.mGeneralEntities;
            if (m == null) {
                m = (this.mGeneralEntities = new LinkedHashMap<String, EntityDecl>());
            }
        }
        final Object old;
        if (m.size() > 0 && (old = m.get(id)) != null) {
            final XMLReporter rep = this.mConfig.getXMLReporter();
            if (rep != null) {
                final EntityDecl oldED = (EntityDecl)old;
                String str = " entity '" + id + "' defined more than once: first declaration at " + oldED.getLocation();
                if (isParam) {
                    str = "Parameter" + str;
                }
                else {
                    str = "General" + str;
                }
                this._reportWarning(rep, ErrorConsts.WT_ENT_DECL, str, evtLoc);
            }
        }
        else {
            m.put(id, ent);
        }
        if (this.mEventListener != null) {
            if (!ent.isParsed()) {
                URL src;
                try {
                    src = this.mInput.getSource();
                }
                catch (IOException e2) {
                    throw new WstxIOException(e2);
                }
                this.mEventListener.dtdUnparsedEntityDecl(id, ent.getPublicId(), ent.getSystemId(), ent.getNotationName(), src);
            }
        }
    }
    
    private void handleNotationDecl() throws XMLStreamException {
        char c = this.skipObligatoryDtdWs();
        final String id = this.readDTDName(c);
        c = this.skipObligatoryDtdWs();
        final boolean isPublic = this.checkPublicSystemKeyword(c);
        c = this.skipObligatoryDtdWs();
        String pubId;
        if (isPublic) {
            if (c != '\"' && c != '\'') {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the public identifier");
            }
            pubId = this.parsePublicId(c, this.getErrorMsg());
            c = this.skipDtdWs(true);
        }
        else {
            pubId = null;
        }
        String sysId;
        if (c == '\"' || c == '\'') {
            sysId = this.parseSystemId(c, this.mNormalizeLFs, this.getErrorMsg());
            c = this.skipDtdWs(true);
        }
        else {
            if (!isPublic) {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the system identifier");
            }
            sysId = null;
        }
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected closing '>' after NOTATION declaration");
        }
        URL baseURL;
        try {
            baseURL = this.mInput.getSource();
        }
        catch (IOException e) {
            throw new WstxIOException(e);
        }
        if (this.mEventListener != null) {
            this.mEventListener.dtdNotationDecl(id, pubId, sysId, baseURL);
        }
        final Location evtLoc = this.getStartLocation();
        final NotationDeclaration nd = new WNotationDeclaration(evtLoc, id, pubId, sysId, baseURL);
        if (this.mPredefdNotations != null) {
            final NotationDeclaration oldDecl = this.mPredefdNotations.get(id);
            if (oldDecl != null) {
                DTDSubsetImpl.throwNotationException(oldDecl, nd);
            }
        }
        HashMap<String, NotationDeclaration> m = this.mNotations;
        if (m == null) {
            m = (this.mNotations = new LinkedHashMap<String, NotationDeclaration>());
        }
        else {
            final NotationDeclaration oldDecl2 = m.get(id);
            if (oldDecl2 != null) {
                DTDSubsetImpl.throwNotationException(oldDecl2, nd);
            }
        }
        if (this.mNotationForwardRefs != null) {
            this.mNotationForwardRefs.remove(id);
        }
        m.put(id, nd);
    }
    
    private void handleTargetNsDecl() throws XMLStreamException {
        this.mAnyDTDppFeatures = true;
        char c = this.skipObligatoryDtdWs();
        String name;
        if (this.isNameStartChar(c)) {
            name = this.readDTDLocalName(c, false);
            c = this.skipObligatoryDtdWs();
        }
        else {
            name = null;
        }
        if (c != '\"' && c != '\'') {
            if (c == '>') {
                this._reportWFCViolation("Missing namespace URI for TARGETNS directive");
            }
            this.throwDTDUnexpectedChar(c, "; expected a single or double quote to enclose the namespace URI");
        }
        String uri = this.parseSystemId(c, false, "in namespace URI");
        if ((this.mConfigFlags & 0x800) != 0x0) {
            uri = InternCache.getInstance().intern(uri);
        }
        c = this.skipDtdWs(true);
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected '>' to end TARGETNS directive");
        }
        if (name == null) {
            this.mDefaultNsURI = uri;
        }
        else {
            if (this.mNamespaces == null) {
                this.mNamespaces = new HashMap<String, String>();
            }
            this.mNamespaces.put(name, uri);
        }
    }
    
    private void handleAttrDecl(final DTDElement elem, char c, final int index, final Location loc) throws XMLStreamException {
        final PrefixedName attrName = this.readDTDQName(c);
        c = this.skipObligatoryDtdWs();
        int type = 0;
        WordResolver enumValues = null;
        Label_0294: {
            if (c == '(') {
                enumValues = this.parseEnumerated(elem, attrName, false);
                type = 1;
            }
            else {
                final String typeStr = this.readDTDName(c);
                switch (typeStr.charAt(0)) {
                    case 'C': {
                        if (typeStr == "CDATA") {
                            type = 0;
                            break Label_0294;
                        }
                        break;
                    }
                    case 'I': {
                        if (typeStr == "ID") {
                            type = 2;
                            break Label_0294;
                        }
                        if (typeStr == "IDREF") {
                            type = 3;
                            break Label_0294;
                        }
                        if (typeStr == "IDREFS") {
                            type = 4;
                            break Label_0294;
                        }
                        break;
                    }
                    case 'E': {
                        if (typeStr == "ENTITY") {
                            type = 5;
                            break Label_0294;
                        }
                        if (typeStr == "ENTITIES") {
                            type = 6;
                            break Label_0294;
                        }
                        break;
                    }
                    case 'N': {
                        if (typeStr == "NOTATION") {
                            type = 7;
                            c = this.skipObligatoryDtdWs();
                            if (c != '(') {
                                this.throwDTDUnexpectedChar(c, "Excepted '(' to start the list of NOTATION ids");
                            }
                            enumValues = this.parseEnumerated(elem, attrName, true);
                            break Label_0294;
                        }
                        if (typeStr == "NMTOKEN") {
                            type = 8;
                            break Label_0294;
                        }
                        if (typeStr == "NMTOKENS") {
                            type = 9;
                            break Label_0294;
                        }
                        break;
                    }
                }
                this.throwDTDAttrError("Unrecognized attribute type '" + typeStr + "'" + ErrorConsts.ERR_DTD_ATTR_TYPE, elem, attrName);
            }
        }
        c = this.skipObligatoryDtdWs();
        DefaultAttrValue defVal;
        if (c == '#') {
            final String defTypeStr = this.readDTDName(this.getNextExpanded());
            if (defTypeStr == "REQUIRED") {
                defVal = DefaultAttrValue.constructRequired();
            }
            else if (defTypeStr == "IMPLIED") {
                defVal = DefaultAttrValue.constructImplied();
            }
            else if (defTypeStr == "FIXED") {
                defVal = DefaultAttrValue.constructFixed();
                c = this.skipObligatoryDtdWs();
                this.parseAttrDefaultValue(defVal, c, attrName, loc, true);
            }
            else {
                this.throwDTDAttrError("Unrecognized attribute default value directive #" + defTypeStr + ErrorConsts.ERR_DTD_DEFAULT_TYPE, elem, attrName);
                defVal = null;
            }
        }
        else {
            defVal = DefaultAttrValue.constructOptional();
            this.parseAttrDefaultValue(defVal, c, attrName, loc, false);
        }
        if (type == 2 && defVal.hasDefaultValue()) {
            if (this.mCfgFullyValidating) {
                this.throwDTDAttrError("has type ID; can not have a default (or #FIXED) value (XML 1.0/#3.3.1)", elem, attrName);
            }
        }
        else if (this.mConfig.willDoXmlIdTyping() && attrName.isXmlReservedAttr(this.mCfgNsEnabled, "id")) {
            this.checkXmlIdAttr(type);
        }
        if (attrName.isXmlReservedAttr(this.mCfgNsEnabled, "space")) {
            this.checkXmlSpaceAttr(type, enumValues);
        }
        DTDAttribute attr;
        if (this.mCfgNsEnabled && attrName.isaNsDeclaration()) {
            if (!defVal.hasDefaultValue()) {
                return;
            }
            attr = elem.addNsDefault(this, attrName, type, defVal, this.mCfgFullyValidating);
        }
        else {
            attr = elem.addAttribute(this, attrName, type, defVal, enumValues, this.mCfgFullyValidating);
        }
        if (attr == null) {
            final XMLReporter rep = this.mConfig.getXMLReporter();
            if (rep != null) {
                final String msg = MessageFormat.format(ErrorConsts.W_DTD_ATTR_REDECL, attrName, elem);
                this._reportWarning(rep, ErrorConsts.WT_ATTR_DECL, msg, loc);
            }
        }
        else if (defVal.hasDefaultValue()) {
            attr.normalizeDefault();
            if (this.mCfgFullyValidating) {
                attr.validateDefault(this, true);
            }
        }
    }
    
    private WordResolver parseEnumerated(final DTDElement elem, final PrefixedName attrName, final boolean isNotation) throws XMLStreamException {
        final TreeSet<String> set = new TreeSet<String>();
        char c = this.skipDtdWs(true);
        if (c == ')') {
            this.throwDTDUnexpectedChar(c, " (empty list; missing identifier(s))?");
        }
        if (isNotation) {
            final HashMap<String, String> sharedEnums = null;
        }
        else {
            HashMap<String, String> sharedEnums = this.mSharedEnumValues;
            if (sharedEnums == null && !isNotation) {
                sharedEnums = (this.mSharedEnumValues = new HashMap<String, String>());
            }
        }
        HashMap<String, String> sharedEnums;
        String id = isNotation ? this.readNotationEntry(c, attrName, elem.getLocation()) : this.readEnumEntry(c, sharedEnums);
        set.add(id);
        while (true) {
            c = this.skipDtdWs(true);
            if (c == ')') {
                break;
            }
            if (c != '|') {
                this.throwDTDUnexpectedChar(c, "; missing '|' separator?");
            }
            c = this.skipDtdWs(true);
            id = (isNotation ? this.readNotationEntry(c, attrName, elem.getLocation()) : this.readEnumEntry(c, sharedEnums));
            if (set.add(id) || !this.mCfgFullyValidating) {
                continue;
            }
            this.throwDTDAttrError("Duplicate enumeration value '" + id + "'", elem, attrName);
        }
        return WordResolver.constructInstance(set);
    }
    
    private String readNotationEntry(final char c, final PrefixedName attrName, final Location refLoc) throws XMLStreamException {
        final String id = this.readDTDName(c);
        if (this.mPredefdNotations != null) {
            final NotationDeclaration decl = this.mPredefdNotations.get(id);
            if (decl != null) {
                this.mUsesPredefdNotations = true;
                return decl.getName();
            }
        }
        final NotationDeclaration decl = (this.mNotations == null) ? null : this.mNotations.get(id);
        if (decl == null) {
            if (this.mCfgFullyValidating) {
                if (this.mNotationForwardRefs == null) {
                    this.mNotationForwardRefs = new LinkedHashMap<String, Location>();
                }
                this.mNotationForwardRefs.put(id, refLoc);
            }
            return id;
        }
        return decl.getName();
    }
    
    private String readEnumEntry(final char c, final HashMap<String, String> sharedEnums) throws XMLStreamException {
        final String id = this.readDTDNmtoken(c);
        String sid = sharedEnums.get(id);
        if (sid == null) {
            sid = id;
            sharedEnums.put(sid, sid);
        }
        return sid;
    }
    
    private StructValidator readMixedSpec(final PrefixedName elemName, final boolean construct) throws XMLStreamException {
        final String keyw = this.checkDTDKeyword("PCDATA");
        if (keyw != null) {
            this._reportWFCViolation("Unrecognized directive #" + keyw + "'; expected #PCDATA (or element name)");
        }
        final HashMap<PrefixedName, ContentSpec> m = new LinkedHashMap<PrefixedName, ContentSpec>();
        while (true) {
            char c = this.skipDtdWs(true);
            if (c == ')') {
                break;
            }
            if (c == '|') {
                c = this.skipDtdWs(true);
            }
            else if (c == ',') {
                this.throwDTDUnexpectedChar(c, " (sequences not allowed within mixed content)");
            }
            else if (c == '(') {
                this.throwDTDUnexpectedChar(c, " (sub-content specs not allowed within mixed content)");
            }
            else {
                this.throwDTDUnexpectedChar(c, "; expected either '|' to separate elements, or ')' to close the list");
            }
            final PrefixedName n = this.readDTDQName(c);
            final Object old = m.put(n, TokenContentSpec.construct(' ', n));
            if (old == null || !this.mCfgFullyValidating) {
                continue;
            }
            this.throwDTDElemError("duplicate child element <" + n + "> in mixed content model", elemName);
        }
        char c = (this.mInputPtr < this.mInputEnd) ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
        if (c != '*') {
            if (m.size() > 0) {
                this._reportWFCViolation("Missing trailing '*' after a non-empty mixed content specification");
            }
            --this.mInputPtr;
        }
        if (!construct) {
            return null;
        }
        if (m.isEmpty()) {
            return EmptyValidator.getPcdataInstance();
        }
        final ContentSpec spec = ChoiceContentSpec.constructMixed(this.mCfgNsEnabled, m.values());
        StructValidator val = spec.getSimpleValidator();
        if (val == null) {
            final DFAState dfa = DFAState.constructDFA(spec);
            val = new DFAValidator(dfa);
        }
        return val;
    }
    
    private ContentSpec readContentSpec(final PrefixedName elemName, final boolean mainLevel, final boolean construct) throws XMLStreamException {
        final ArrayList<ContentSpec> subSpecs = new ArrayList<ContentSpec>();
        boolean isChoice = false;
        boolean choiceSet = false;
        while (true) {
            char c = this.skipDtdWs(true);
            if (c == ')') {
                break;
            }
            if (c == '|' || c == ',') {
                final boolean newChoice = c == '|';
                if (!choiceSet) {
                    isChoice = newChoice;
                    choiceSet = true;
                }
                else if (isChoice != newChoice) {
                    this._reportWFCViolation("Can not mix content spec separators ('|' and ','); need to use parenthesis groups");
                }
                c = this.skipDtdWs(true);
            }
            else if (!subSpecs.isEmpty()) {
                this.throwDTDUnexpectedChar(c, " (missing separator '|' or ','?)");
            }
            if (c == '(') {
                final ContentSpec cs = this.readContentSpec(elemName, false, construct);
                subSpecs.add(cs);
            }
            else {
                if (c == '|' || c == ',') {
                    this.throwDTDUnexpectedChar(c, " (missing element name?)");
                }
                final PrefixedName thisName = this.readDTDQName(c);
                final char arity = this.readArity();
                final ContentSpec cs2 = construct ? TokenContentSpec.construct(arity, thisName) : TokenContentSpec.getDummySpec();
                subSpecs.add(cs2);
            }
        }
        if (subSpecs.isEmpty()) {
            this._reportWFCViolation("Empty content specification for '" + elemName + "' (need at least one entry)");
        }
        final char arity2 = this.readArity();
        if (!construct) {
            return TokenContentSpec.getDummySpec();
        }
        if (subSpecs.size() == 1) {
            final ContentSpec cs = subSpecs.get(0);
            final char otherArity = cs.getArity();
            if (arity2 != otherArity) {
                cs.setArity(combineArities(arity2, otherArity));
            }
            return cs;
        }
        if (isChoice) {
            return ChoiceContentSpec.constructChoice(this.mCfgNsEnabled, arity2, subSpecs);
        }
        return SeqContentSpec.construct(this.mCfgNsEnabled, arity2, subSpecs);
    }
    
    private static char combineArities(final char arity1, final char arity2) {
        if (arity1 == arity2) {
            return arity1;
        }
        if (arity1 == ' ') {
            return arity2;
        }
        if (arity2 == ' ') {
            return arity1;
        }
        if (arity1 == '*' || arity2 == '*') {
            return '*';
        }
        return '*';
    }
    
    private EntityDecl handleExternalEntityDecl(final WstxInputSource inputSource, final boolean isParam, final String id, char c, final Location evtLoc) throws XMLStreamException {
        final boolean isPublic = this.checkPublicSystemKeyword(c);
        String pubId = null;
        if (isPublic) {
            c = this.skipObligatoryDtdWs();
            if (c != '\"' && c != '\'') {
                this.throwDTDUnexpectedChar(c, "; expected a quote to start the public identifier");
            }
            pubId = this.parsePublicId(c, this.getErrorMsg());
            c = this.getNextExpanded();
            if (c <= ' ') {
                c = this.skipDtdWs(true);
            }
            else if (c != '>') {
                --this.mInputPtr;
                c = this.skipObligatoryDtdWs();
            }
            if (c == '>') {
                this._reportWFCViolation("Unexpected end of ENTITY declaration (expected a system id after public id): trying to use an SGML DTD instead of XML one?");
            }
        }
        else {
            c = this.skipObligatoryDtdWs();
        }
        if (c != '\"' && c != '\'') {
            this.throwDTDUnexpectedChar(c, "; expected a quote to start the system identifier");
        }
        final String sysId = this.parseSystemId(c, this.mNormalizeLFs, this.getErrorMsg());
        String notationId = null;
        if (isParam) {
            c = this.skipDtdWs(true);
        }
        else {
            final int i = this.peekNext();
            if (i == 62) {
                c = '>';
                ++this.mInputPtr;
            }
            else if (i < 0) {
                c = this.skipDtdWs(true);
            }
            else if (i == 37) {
                c = this.getNextExpanded();
            }
            else {
                ++this.mInputPtr;
                c = (char)i;
                if (!WstxInputData.isSpaceChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected a separating space or closing '>'");
                }
                c = this.skipDtdWs(true);
            }
            if (c != '>') {
                if (!this.isNameStartChar(c)) {
                    this.throwDTDUnexpectedChar(c, "; expected either NDATA keyword, or closing '>'");
                }
                final String keyw = this.checkDTDKeyword("DATA");
                if (keyw != null) {
                    this._reportWFCViolation("Unrecognized keyword '" + keyw + "'; expected NOTATION (or closing '>')");
                }
                c = this.skipObligatoryDtdWs();
                notationId = this.readNotationEntry(c, null, evtLoc);
                c = this.skipDtdWs(true);
            }
        }
        if (c != '>') {
            this.throwDTDUnexpectedChar(c, "; expected closing '>'");
        }
        URL ctxt;
        try {
            ctxt = inputSource.getSource();
        }
        catch (IOException e) {
            throw new WstxIOException(e);
        }
        if (notationId == null) {
            return new ParsedExtEntity(evtLoc, id, ctxt, pubId, sysId);
        }
        return new UnparsedExtEntity(evtLoc, id, ctxt, pubId, sysId, notationId);
    }
    
    private LinkedHashMap<PrefixedName, DTDElement> getElementMap() {
        LinkedHashMap<PrefixedName, DTDElement> m = this.mElements;
        if (m == null) {
            m = (this.mElements = new LinkedHashMap<PrefixedName, DTDElement>());
        }
        return m;
    }
    
    private PrefixedName findSharedName(final String prefix, final String localName) {
        HashMap<PrefixedName, PrefixedName> m = this.mSharedNames;
        if (this.mSharedNames == null) {
            m = (this.mSharedNames = new HashMap<PrefixedName, PrefixedName>());
        }
        else {
            PrefixedName key = this.mAccessKey;
            key.reset(prefix, localName);
            key = m.get(key);
            if (key != null) {
                return key;
            }
        }
        final PrefixedName result = new PrefixedName(prefix, localName);
        m.put(result, result);
        return result;
    }
    
    @Override
    protected EntityDecl findEntity(final String id, final Object arg) {
        if (arg == FullDTDReader.ENTITY_EXP_PE) {
            EntityDecl ed = (this.mPredefdPEs == null) ? null : this.mPredefdPEs.get(id);
            if (ed != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdPEs = null;
            }
            else if (this.mParamEntities != null) {
                ed = this.mParamEntities.get(id);
                if (ed != null && !this.mUsesPredefdEntities) {
                    Set<String> used = this.mRefdPEs;
                    if (used == null) {
                        used = (this.mRefdPEs = new HashSet<String>());
                    }
                    used.add(id);
                }
            }
            return ed;
        }
        if (arg == FullDTDReader.ENTITY_EXP_GE) {
            EntityDecl ed = (this.mPredefdGEs == null) ? null : this.mPredefdGEs.get(id);
            if (ed != null) {
                this.mUsesPredefdEntities = true;
                this.mRefdGEs = null;
            }
            else if (this.mGeneralEntities != null) {
                ed = this.mGeneralEntities.get(id);
                if (ed != null && !this.mUsesPredefdEntities) {
                    if (this.mRefdGEs == null) {
                        this.mRefdGEs = new HashSet<String>();
                    }
                    this.mRefdGEs.add(id);
                }
            }
            return ed;
        }
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }
    
    @Override
    protected void handleUndeclaredEntity(final String id) throws XMLStreamException {
        this._reportVCViolation("Undeclared parameter entity '" + id + "'.");
        if (this.mCurrAttrDefault != null) {
            final Location loc = this.getLastCharLocation();
            if (this.mExpandingPE) {
                this.mCurrAttrDefault.addUndeclaredPE(id, loc);
            }
            else {
                this.mCurrAttrDefault.addUndeclaredGE(id, loc);
            }
        }
        if (this.mEventListener != null && this.mExpandingPE) {
            this.mEventListener.dtdSkippedEntity("%" + id);
        }
    }
    
    @Override
    protected void handleIncompleteEntityProblem(final WstxInputSource closing) throws XMLStreamException {
        if (closing.getScopeId() == 0) {
            this._reportWFCViolation(this.entityDesc(closing) + ": " + "Incomplete PE: has to fully contain a declaration (as per xml 1.0.3, section 2.8, WFC 'PE Between Declarations')");
        }
        else if (this.mCfgFullyValidating) {
            this._reportVCViolation(this.entityDesc(closing) + ": " + "Incomplete PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }
    
    protected void handleGreedyEntityProblem(final WstxInputSource input) throws XMLStreamException {
        if (this.mCfgFullyValidating) {
            this._reportWFCViolation(this.entityDesc(input) + ": " + "Unbalanced PE: has to be fully contained in a declaration (as per xml 1.0.3, section 2.8, VC 'Proper Declaration/PE Nesting')");
        }
    }
    
    protected void checkXmlSpaceAttr(final int type, final WordResolver enumValues) throws XMLStreamException {
        boolean ok = type == 1;
        if (ok) {
            switch (enumValues.size()) {
                case 1: {
                    ok = (enumValues.find("preserve") != null || enumValues.find("default") != null);
                    break;
                }
                case 2: {
                    ok = (enumValues.find("preserve") != null && enumValues.find("default") != null);
                    break;
                }
                default: {
                    ok = false;
                    break;
                }
            }
        }
        if (!ok) {
            this._reportVCViolation(ErrorConsts.ERR_DTD_XML_SPACE);
        }
    }
    
    protected void checkXmlIdAttr(final int type) throws XMLStreamException {
        if (type != 2) {
            this._reportVCViolation(ErrorConsts.ERR_DTD_XML_ID);
        }
    }
    
    private void _reportWarning(final XMLReporter rep, final String probType, final String msg, final Location loc) throws XMLStreamException {
        if (rep != null) {
            final XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 1, probType);
            rep.report(msg, probType, prob, loc);
        }
    }
    
    static {
        ENTITY_EXP_GE = Boolean.FALSE;
        ENTITY_EXP_PE = Boolean.TRUE;
    }
}
