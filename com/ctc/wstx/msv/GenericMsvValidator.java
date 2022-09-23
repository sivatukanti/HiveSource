// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import com.sun.msv.util.DatatypeRef;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;
import java.util.StringTokenizer;
import com.sun.msv.verifier.regexp.StringToken;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import org.codehaus.stax2.validation.XMLValidationProblem;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.util.TextAccumulator;
import com.sun.msv.verifier.Acceptor;
import java.util.ArrayList;
import com.sun.msv.verifier.DocumentDeclaration;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationSchema;
import com.sun.msv.grammar.IDContextProvider2;
import org.codehaus.stax2.validation.XMLValidator;

public final class GenericMsvValidator extends XMLValidator implements IDContextProvider2
{
    final XMLValidationSchema mParentSchema;
    final ValidationContext mContext;
    final DocumentDeclaration mVGM;
    final ArrayList<Object> mAcceptors;
    Acceptor mCurrAcceptor;
    final TextAccumulator mTextAccumulator;
    ElementIdMap mIdDefs;
    String mCurrAttrPrefix;
    String mCurrAttrLocalName;
    XMLValidationProblem mProblem;
    final StringRef mErrorRef;
    final StartTagInfo mStartTag;
    final AttributeProxy mAttributeProxy;
    
    public GenericMsvValidator(final XMLValidationSchema parent, final ValidationContext ctxt, final DocumentDeclaration vgm) {
        this.mAcceptors = new ArrayList<Object>();
        this.mCurrAcceptor = null;
        this.mTextAccumulator = new TextAccumulator();
        this.mErrorRef = new StringRef();
        this.mStartTag = new StartTagInfo("", "", "", (Attributes)null, (IDContextProvider2)null);
        this.mParentSchema = parent;
        this.mContext = ctxt;
        this.mVGM = vgm;
        this.mCurrAcceptor = this.mVGM.createAcceptor();
        this.mAttributeProxy = new AttributeProxy(ctxt);
    }
    
    public String getBaseUri() {
        return this.mContext.getBaseUri();
    }
    
    public boolean isNotation(final String notationName) {
        return this.mContext.isNotationDeclared(notationName);
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        return this.mContext.isUnparsedEntityDeclared(entityName);
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        return this.mContext.getNamespaceURI(prefix);
    }
    
    public void onID(final Datatype datatype, final StringToken idToken) throws IllegalArgumentException {
        if (this.mIdDefs == null) {
            this.mIdDefs = new ElementIdMap();
        }
        final int idType = datatype.getIdType();
        final Location loc = this.mContext.getValidationLocation();
        final PrefixedName elemPName = this.getElementPName();
        final PrefixedName attrPName = this.getAttrPName();
        if (idType == 1) {
            final String idStr = idToken.literal.trim();
            final ElementId eid = this.mIdDefs.addDefined(idStr, loc, elemPName, attrPName);
            if (eid.getLocation() != loc) {
                (this.mProblem = new XMLValidationProblem(loc, "Duplicate id '" + idStr + "', first declared at " + eid.getLocation())).setReporter(this);
            }
        }
        else if (idType == 2) {
            final String idStr = idToken.literal.trim();
            this.mIdDefs.addReferenced(idStr, loc, elemPName, attrPName);
        }
        else {
            if (idType != 3) {
                throw new IllegalStateException("Internal error: unexpected ID datatype: " + datatype);
            }
            final StringTokenizer tokens = new StringTokenizer(idToken.literal);
            while (tokens.hasMoreTokens()) {
                this.mIdDefs.addReferenced(tokens.nextToken(), loc, elemPName, attrPName);
            }
        }
    }
    
    @Override
    public XMLValidationSchema getSchema() {
        return this.mParentSchema;
    }
    
    @Override
    public void validateElementStart(final String localName, String uri, final String prefix) throws XMLStreamException {
        if (this.mCurrAcceptor == null) {
            return;
        }
        if (this.mTextAccumulator.hasText()) {
            this.doValidateText(this.mTextAccumulator);
        }
        if (uri == null) {
            uri = "";
        }
        final String qname = localName;
        this.mStartTag.reinit(uri, localName, qname, (Attributes)this.mAttributeProxy, (IDContextProvider2)this);
        this.mCurrAcceptor = this.mCurrAcceptor.createChildAcceptor(this.mStartTag, this.mErrorRef);
        if (this.mErrorRef.str != null) {
            this.reportError(this.mErrorRef);
        }
        if (this.mProblem != null) {
            final XMLValidationProblem p = this.mProblem;
            this.mProblem = null;
            this.mContext.reportProblem(p);
        }
        this.mAcceptors.add(this.mCurrAcceptor);
    }
    
    @Override
    public String validateAttribute(final String localName, String uri, final String prefix, final String value) throws XMLStreamException {
        this.mCurrAttrLocalName = localName;
        this.mCurrAttrPrefix = prefix;
        if (this.mCurrAcceptor != null) {
            final String qname = localName;
            final DatatypeRef typeRef = null;
            if (uri == null) {
                uri = "";
            }
            if (!this.mCurrAcceptor.onAttribute2(uri, localName, qname, value, (IDContextProvider2)this, this.mErrorRef, typeRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef);
            }
            if (this.mProblem != null) {
                final XMLValidationProblem p = this.mProblem;
                this.mProblem = null;
                this.mContext.reportProblem(p);
            }
        }
        return null;
    }
    
    @Override
    public String validateAttribute(final String localName, final String uri, final String prefix, final char[] valueChars, final int valueStart, final int valueEnd) throws XMLStreamException {
        final int len = valueEnd - valueStart;
        return this.validateAttribute(localName, uri, prefix, new String(valueChars, valueStart, len));
    }
    
    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        final String s = "";
        this.mCurrAttrPrefix = s;
        this.mCurrAttrLocalName = s;
        if (this.mCurrAcceptor == null) {
            return 4;
        }
        if (!this.mCurrAcceptor.onEndAttributes(this.mStartTag, this.mErrorRef) || this.mErrorRef.str != null) {
            this.reportError(this.mErrorRef);
        }
        final int stringChecks = this.mCurrAcceptor.getStringCareLevel();
        switch (stringChecks) {
            case 0: {
                return 1;
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 3;
            }
            default: {
                throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
            }
        }
    }
    
    @Override
    public int validateElementEnd(final String localName, final String uri, final String prefix) throws XMLStreamException {
        this.doValidateText(this.mTextAccumulator);
        final int lastIx = this.mAcceptors.size() - 1;
        if (lastIx < 0) {
            return 1;
        }
        final Acceptor acc = this.mAcceptors.remove(lastIx);
        if (acc != null && (!acc.isAcceptState(this.mErrorRef) || this.mErrorRef.str != null)) {
            this.reportError(this.mErrorRef);
        }
        if (lastIx == 0) {
            this.mCurrAcceptor = null;
        }
        else {
            this.mCurrAcceptor = this.mAcceptors.get(lastIx - 1);
        }
        if (this.mCurrAcceptor == null || acc == null) {
            return 4;
        }
        if (!this.mCurrAcceptor.stepForward(acc, this.mErrorRef) || this.mErrorRef.str != null) {
            this.reportError(this.mErrorRef);
        }
        final int stringChecks = this.mCurrAcceptor.getStringCareLevel();
        switch (stringChecks) {
            case 0: {
                return 1;
            }
            case 1: {
                return 4;
            }
            case 2: {
                return 3;
            }
            default: {
                throw new IllegalArgumentException("Internal error: unexpected string care level value return by MSV: " + stringChecks);
            }
        }
    }
    
    @Override
    public void validateText(final String text, final boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(text);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }
    
    @Override
    public void validateText(final char[] cbuf, final int textStart, final int textEnd, final boolean lastTextSegment) throws XMLStreamException {
        this.mTextAccumulator.addText(cbuf, textStart, textEnd);
        if (lastTextSegment) {
            this.doValidateText(this.mTextAccumulator);
        }
    }
    
    @Override
    public void validationCompleted(final boolean eod) throws XMLStreamException {
        if (eod && this.mIdDefs != null) {
            final ElementId ref = this.mIdDefs.getFirstUndefined();
            if (ref != null) {
                final String msg = "Undefined ID '" + ref.getId() + "': referenced from element <" + ref.getElemName() + ">, attribute '" + ref.getAttrName() + "'";
                this.reportError(msg, ref.getLocation());
            }
        }
    }
    
    @Override
    public String getAttributeType(final int index) {
        return null;
    }
    
    @Override
    public int getIdAttrIndex() {
        return -1;
    }
    
    @Override
    public int getNotationAttrIndex() {
        return -1;
    }
    
    PrefixedName getElementPName() {
        return PrefixedName.valueOf(this.mContext.getCurrentElementName());
    }
    
    PrefixedName getAttrPName() {
        return new PrefixedName(this.mCurrAttrPrefix, this.mCurrAttrLocalName);
    }
    
    void doValidateText(final TextAccumulator textAcc) throws XMLStreamException {
        if (this.mCurrAcceptor != null) {
            final String str = textAcc.getAndClear();
            final DatatypeRef typeRef = null;
            if (!this.mCurrAcceptor.onText2(str, (IDContextProvider2)this, this.mErrorRef, typeRef) || this.mErrorRef.str != null) {
                this.reportError(this.mErrorRef);
            }
        }
    }
    
    private void reportError(final StringRef errorRef) throws XMLStreamException {
        String msg = errorRef.str;
        errorRef.str = null;
        if (msg == null) {
            msg = "Unknown reason";
        }
        this.reportError(msg);
    }
    
    private void reportError(final String msg) throws XMLStreamException {
        this.reportError(msg, this.mContext.getValidationLocation());
    }
    
    private void reportError(final String msg, final Location loc) throws XMLStreamException {
        final XMLValidationProblem prob = new XMLValidationProblem(loc, msg, 2);
        prob.setReporter(this);
        this.mContext.reportProblem(prob);
    }
}
