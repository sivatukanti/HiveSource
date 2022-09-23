// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import java.net.URISyntaxException;
import com.ctc.wstx.util.URLUtil;
import java.net.URI;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.dtd.DTDId;
import java.io.FileNotFoundException;
import java.net.URL;
import com.ctc.wstx.io.DefaultInputResolver;
import java.io.IOException;
import org.codehaus.stax2.validation.XMLValidationProblem;
import com.ctc.wstx.dtd.DTDValidatorBase;
import org.codehaus.stax2.validation.ValidationContext;
import javax.xml.stream.Location;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.dtd.FullDTDReader;
import org.codehaus.stax2.validation.XMLValidationSchema;
import javax.xml.stream.events.NotationDeclaration;
import com.ctc.wstx.ent.EntityDecl;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.ctc.wstx.dtd.DTDSubset;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.DTDValidationSchema;

public class ValidatingStreamReader extends TypedStreamReader
{
    static final String STAX_PROP_ENTITIES = "javax.xml.stream.entities";
    static final String STAX_PROP_NOTATIONS = "javax.xml.stream.notations";
    DTDValidationSchema mDTD;
    XMLValidator mAutoDtdValidator;
    boolean mDtdValidatorSet;
    protected ValidationProblemHandler mVldProbHandler;
    
    private ValidatingStreamReader(final InputBootstrapper bs, final BranchingReaderSource input, final ReaderCreator owner, final ReaderConfig cfg, final InputElementStack elemStack, final boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
        this.mDTD = null;
        this.mAutoDtdValidator = null;
        this.mDtdValidatorSet = false;
        this.mVldProbHandler = null;
    }
    
    public static ValidatingStreamReader createValidatingStreamReader(final BranchingReaderSource input, final ReaderCreator owner, final ReaderConfig cfg, final InputBootstrapper bs, final boolean forER) throws XMLStreamException {
        final ValidatingStreamReader sr = new ValidatingStreamReader(bs, input, owner, cfg, BasicStreamReader.createElementStack(cfg), forER);
        return sr;
    }
    
    @Override
    public Object getProperty(final String name) {
        if (name.equals("javax.xml.stream.entities")) {
            this.safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            final List<EntityDecl> l = ((DTDSubset)this.mDTD).getGeneralEntityList();
            return new ArrayList(l);
        }
        else {
            if (!name.equals("javax.xml.stream.notations")) {
                return super.getProperty(name);
            }
            this.safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            final List<NotationDeclaration> i = ((DTDSubset)this.mDTD).getNotationList();
            return new ArrayList(i);
        }
    }
    
    @Override
    public Object getProcessedDTD() {
        return this.getProcessedDTDSchema();
    }
    
    @Override
    public DTDValidationSchema getProcessedDTDSchema() {
        DTDValidationSchema dtd = this.mConfig.getDTDOverride();
        if (dtd == null) {
            dtd = this.mDTD;
        }
        return this.mDTD;
    }
    
    @Override
    public XMLValidator validateAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.validateAgainst(schema);
    }
    
    @Override
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(schema);
    }
    
    @Override
    public XMLValidator stopValidatingAgainst(final XMLValidator validator) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(validator);
    }
    
    @Override
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler h) {
        final ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }
    
    @Override
    protected void finishDTD(final boolean copyContents) throws XMLStreamException {
        if (!this.hasConfigFlags(16)) {
            super.finishDTD(copyContents);
            return;
        }
        char c = this.getNextChar(" in DOCTYPE declaration");
        DTDSubset intSubset = null;
        if (c == '[') {
            if (copyContents) {
                ((BranchingReaderSource)this.mInput).startBranch(this.mTextBuffer, this.mInputPtr, this.mNormalizeLFs);
            }
            try {
                intSubset = FullDTDReader.readInternalSubset(this, this.mInput, this.mConfig, this.hasConfigFlags(32), this.mDocXmlVersion);
            }
            finally {
                if (copyContents) {
                    ((BranchingReaderSource)this.mInput).endBranch(this.mInputPtr - 1);
                }
            }
            c = this.getNextCharAfterWS(" in internal DTD subset");
        }
        if (c != '>') {
            this.throwUnexpectedChar(c, "; expected '>' to finish DOCTYPE declaration.");
        }
        this.mDTD = this.mConfig.getDTDOverride();
        if (this.mDTD == null) {
            DTDSubset extSubset = null;
            if (this.mDtdPublicId != null || this.mDtdSystemId != null) {
                extSubset = this.findDtdExtSubset(this.mDtdPublicId, this.mDtdSystemId, intSubset);
            }
            if (intSubset == null) {
                this.mDTD = extSubset;
            }
            else if (extSubset == null) {
                this.mDTD = intSubset;
            }
            else {
                this.mDTD = intSubset.combineWithExternalSubset(this, extSubset);
            }
        }
        if (this.mDTD == null) {
            this.mGeneralEntities = null;
        }
        else {
            if (this.mDTD instanceof DTDSubset) {
                this.mGeneralEntities = ((DTDSubset)this.mDTD).getGeneralEntityMap();
            }
            else {
                this._reportProblem(this.mConfig.getXMLReporter(), ErrorConsts.WT_DT_DECL, "Value to set for property 'org.codehaus.stax2.propDtdOverride' not a native Woodstox DTD implementation (but " + this.mDTD.getClass() + "): can not access full entity or notation information", null);
            }
            this.mAutoDtdValidator = this.mDTD.createValidator(this.mElementStack);
            this.mDtdValidatorSet = true;
            NsDefaultProvider nsDefs = null;
            if (this.mAutoDtdValidator instanceof DTDValidatorBase) {
                final DTDValidatorBase dtdv = (DTDValidatorBase)this.mAutoDtdValidator;
                dtdv.setAttrValueNormalization(true);
                if (dtdv.hasNsDefaults()) {
                    nsDefs = dtdv;
                }
            }
            this.mElementStack.setAutomaticDTDValidator(this.mAutoDtdValidator, nsDefs);
        }
    }
    
    @Override
    public void reportValidationProblem(final XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
        }
        else {
            super.reportValidationProblem(prob);
        }
    }
    
    @Override
    protected void initValidation() throws XMLStreamException {
        if (this.hasConfigFlags(32) && !this.mDtdValidatorSet) {
            this.reportProblem(null, ErrorConsts.WT_DT_DECL, ErrorConsts.W_MISSING_DTD, null, null);
        }
    }
    
    private DTDSubset findDtdExtSubset(final String pubId, final String sysId, final DTDSubset intSubset) throws XMLStreamException {
        final boolean cache = this.hasConfigFlags(65536);
        DTDId dtdId;
        try {
            dtdId = this.constructDtdId(pubId, sysId);
        }
        catch (IOException ioe) {
            throw this.constructFromIOE(ioe);
        }
        if (cache) {
            final DTDSubset extSubset = this.findCachedSubset(dtdId, intSubset);
            if (extSubset != null) {
                return extSubset;
            }
        }
        if (sysId == null) {
            this.throwParseError("Can not resolve DTD with public id \"{0}\"; missing system identifier", this.mDtdPublicId, null);
        }
        WstxInputSource src = null;
        try {
            int xmlVersion = this.mDocXmlVersion;
            if (xmlVersion == 0) {
                xmlVersion = 256;
            }
            src = DefaultInputResolver.resolveEntity(this.mInput, null, null, pubId, sysId, this.mConfig.getDtdResolver(), this.mConfig, xmlVersion);
        }
        catch (FileNotFoundException fex) {
            this.throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
        }
        catch (IOException ioe2) {
            this.throwFromIOE(ioe2);
        }
        final DTDSubset extSubset2 = FullDTDReader.readExternalSubset(src, this.mConfig, intSubset, this.hasConfigFlags(32), this.mDocXmlVersion);
        if (cache && extSubset2.isCachable()) {
            this.mOwner.addCachedDTD(dtdId, extSubset2);
        }
        return extSubset2;
    }
    
    private DTDSubset findCachedSubset(final DTDId id, final DTDSubset intSubset) throws XMLStreamException {
        final DTDSubset extSubset = this.mOwner.findCachedDTD(id);
        if (extSubset != null && (intSubset == null || extSubset.isReusableWith(intSubset))) {
            return extSubset;
        }
        return null;
    }
    
    private URI resolveExtSubsetPath(final String systemId) throws IOException {
        final URL ctxt = (this.mInput == null) ? null : this.mInput.getSource();
        if (ctxt == null) {
            return URLUtil.uriFromSystemId(systemId);
        }
        final URL url = URLUtil.urlFromSystemId(systemId, ctxt);
        try {
            return new URI(url.toExternalForm());
        }
        catch (URISyntaxException e) {
            throw new IOException("Failed to construct URI for external subset, URL = " + url.toExternalForm() + ": " + e.getMessage());
        }
    }
    
    protected DTDId constructDtdId(final String pubId, final String sysId) throws IOException {
        final int significantFlags = this.mConfigFlags & 0x280021;
        final URI sysRef = (sysId == null || sysId.length() == 0) ? null : this.resolveExtSubsetPath(sysId);
        final boolean usePublicId = (this.mConfigFlags & 0x20000) != 0x0;
        if (usePublicId && pubId != null && pubId.length() > 0) {
            return DTDId.construct(pubId, sysRef, significantFlags, this.mXml11);
        }
        if (sysRef == null) {
            return null;
        }
        return DTDId.constructFromSystemId(sysRef, significantFlags, this.mXml11);
    }
    
    protected DTDId constructDtdId(final URI sysId) throws IOException {
        final int significantFlags = this.mConfigFlags & 0x80021;
        return DTDId.constructFromSystemId(sysId, significantFlags, this.mXml11);
    }
    
    @Override
    protected void reportInvalidContent(final int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            case 1:
            case 2: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, this.mElementStack.getTopElementDesc(), null);
                break;
            }
            case 3:
            case 4: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_ANY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            default: {
                this.throwParseError("Internal error: trying to report invalid content for " + evtType);
                break;
            }
        }
    }
}
