// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import com.ctc.wstx.util.TextBuffer;
import org.codehaus.stax2.validation.XMLValidationProblem;
import com.ctc.wstx.compat.QNameCreator;
import java.util.ArrayList;
import com.ctc.wstx.util.DataUtil;
import java.util.Iterator;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.EmptyNamespaceContext;
import javax.xml.stream.Location;
import com.ctc.wstx.dtd.DTDValidatorBase;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.ValidatorPair;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import org.codehaus.stax2.validation.XMLValidator;
import com.ctc.wstx.util.StringVector;
import com.ctc.wstx.api.ReaderConfig;
import org.codehaus.stax2.validation.ValidationContext;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.AttributeInfo;

public final class InputElementStack implements AttributeInfo, NamespaceContext, ValidationContext
{
    static final int ID_ATTR_NONE = -1;
    protected final boolean mNsAware;
    protected final AttributeCollector mAttrCollector;
    protected final ReaderConfig mConfig;
    protected InputProblemReporter mReporter;
    protected NsDefaultProvider mNsDefaultProvider;
    protected int mDepth;
    protected long mTotalElements;
    protected final StringVector mNamespaces;
    protected Element mCurrElement;
    protected boolean mMayHaveNsDefaults;
    protected XMLValidator mValidator;
    protected int mIdAttrIndex;
    protected String mLastLocalName;
    protected String mLastPrefix;
    protected String mLastNsURI;
    protected QName mLastName;
    protected BaseNsContext mLastNsContext;
    protected Element mFreeElement;
    
    protected InputElementStack(final ReaderConfig cfg, final boolean nsAware) {
        this.mReporter = null;
        this.mDepth = 0;
        this.mTotalElements = 0L;
        this.mNamespaces = new StringVector(64);
        this.mMayHaveNsDefaults = false;
        this.mValidator = null;
        this.mIdAttrIndex = -1;
        this.mLastLocalName = null;
        this.mLastPrefix = null;
        this.mLastNsURI = null;
        this.mLastName = null;
        this.mLastNsContext = null;
        this.mFreeElement = null;
        this.mConfig = cfg;
        this.mNsAware = nsAware;
        this.mAttrCollector = new AttributeCollector(cfg, nsAware);
    }
    
    protected void connectReporter(final InputProblemReporter rep) {
        this.mReporter = rep;
    }
    
    protected XMLValidator addValidator(final XMLValidator vld) {
        if (this.mValidator == null) {
            this.mValidator = vld;
        }
        else {
            this.mValidator = new ValidatorPair(this.mValidator, vld);
        }
        return vld;
    }
    
    protected void setAutomaticDTDValidator(final XMLValidator validator, final NsDefaultProvider nsDefs) {
        this.mNsDefaultProvider = nsDefs;
        this.addValidator(validator);
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        return this.addValidator(schema.createValidator(this));
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema schema) throws XMLStreamException {
        final XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, schema, results)) {
            final XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidator validator) throws XMLStreamException {
        final XMLValidator[] results = new XMLValidator[2];
        if (ValidatorPair.removeValidator(this.mValidator, validator, results)) {
            final XMLValidator found = results[0];
            this.mValidator = results[1];
            found.validationCompleted(false);
            return found;
        }
        return null;
    }
    
    protected boolean reallyValidating() {
        return this.mValidator != null && (!(this.mValidator instanceof DTDValidatorBase) || ((DTDValidatorBase)this.mValidator).reallyValidating());
    }
    
    public final AttributeCollector getAttrCollector() {
        return this.mAttrCollector;
    }
    
    public BaseNsContext createNonTransientNsContext(final Location loc) {
        if (this.mLastNsContext != null) {
            return this.mLastNsContext;
        }
        final int totalNsSize = this.mNamespaces.size();
        if (totalNsSize < 1) {
            return this.mLastNsContext = EmptyNamespaceContext.getInstance();
        }
        final int localCount = this.getCurrentNsCount() << 1;
        final BaseNsContext nsCtxt = new CompactNsContext(loc, this.mNamespaces.asArray(), totalNsSize, totalNsSize - localCount);
        if (localCount == 0) {
            this.mLastNsContext = nsCtxt;
        }
        return nsCtxt;
    }
    
    public final void push(final String prefix, final String localName) throws XMLStreamException {
        if (++this.mDepth > this.mConfig.getMaxElementDepth()) {
            throw new XMLStreamException("Maximum Element Depth limit (" + this.mConfig.getMaxElementDepth() + ") Exceeded");
        }
        final long mTotalElements = this.mTotalElements + 1L;
        this.mTotalElements = mTotalElements;
        if (mTotalElements > this.mConfig.getMaxElementCount()) {
            throw new XMLStreamException("Maximum Element Count limit (" + this.mConfig.getMaxElementCount() + ") Exceeded");
        }
        final String defaultNs = (this.mCurrElement == null) ? "" : this.mCurrElement.mDefaultNsURI;
        if (this.mCurrElement != null) {
            final Element mCurrElement = this.mCurrElement;
            ++mCurrElement.mChildCount;
            final int max = this.mConfig.getMaxChildrenPerElement();
            if (max > 0 && this.mCurrElement.mChildCount > max) {
                throw new XMLStreamException("Maximum Number of Child Elements limit (" + max + ") Exceeded");
            }
        }
        if (this.mFreeElement == null) {
            this.mCurrElement = new Element(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
        }
        else {
            final Element newElem = this.mFreeElement;
            this.mFreeElement = newElem.mParent;
            newElem.reset(this.mCurrElement, this.mNamespaces.size(), prefix, localName);
            this.mCurrElement = newElem;
        }
        this.mCurrElement.mDefaultNsURI = defaultNs;
        this.mAttrCollector.reset();
        if (this.mNsDefaultProvider != null) {
            this.mMayHaveNsDefaults = this.mNsDefaultProvider.mayHaveNsDefaults(prefix, localName);
        }
    }
    
    public final boolean pop() throws XMLStreamException {
        if (this.mCurrElement == null) {
            throw new IllegalStateException("Popping from empty stack");
        }
        --this.mDepth;
        final Element child = this.mCurrElement;
        final Element parent = child.mParent;
        this.mCurrElement = parent;
        child.relink(this.mFreeElement);
        this.mFreeElement = child;
        final int nsCount = this.mNamespaces.size() - child.mNsOffset;
        if (nsCount > 0) {
            this.mLastNsContext = null;
            this.mNamespaces.removeLast(nsCount);
        }
        return parent != null;
    }
    
    public int resolveAndValidateElement() throws XMLStreamException {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Calling validate() on empty stack.");
        }
        final AttributeCollector ac = this.mAttrCollector;
        final int nsCount = ac.getNsCount();
        if (nsCount > 0) {
            this.mLastNsContext = null;
            final boolean internNsUris = this.mConfig.willInternNsURIs();
            for (int i = 0; i < nsCount; ++i) {
                final Attribute ns = ac.resolveNamespaceDecl(i, internNsUris);
                String nsUri = ns.mNamespaceURI;
                final String prefix = ns.mLocalName;
                if (prefix == "xmlns") {
                    this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS);
                }
                else if (prefix == "xml") {
                    if (!nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML, nsUri, null);
                    }
                }
                else {
                    if (nsUri == null || nsUri.length() == 0) {
                        nsUri = "";
                    }
                    if (prefix == null) {
                        this.mCurrElement.mDefaultNsURI = nsUri;
                    }
                    if (internNsUris) {
                        if (nsUri == "http://www.w3.org/XML/1998/namespace") {
                            this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix, null);
                        }
                        else if (nsUri == "http://www.w3.org/2000/xmlns/") {
                            this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                        }
                    }
                    else if (nsUri.equals("http://www.w3.org/XML/1998/namespace")) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix, null);
                    }
                    else if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                        this.mReporter.throwParseError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI);
                    }
                    this.mNamespaces.addStrings(prefix, nsUri);
                }
            }
        }
        if (this.mMayHaveNsDefaults) {
            this.mNsDefaultProvider.checkNsDefaults(this);
        }
        final String prefix2 = this.mCurrElement.mPrefix;
        String ns2;
        if (prefix2 == null) {
            ns2 = this.mCurrElement.mDefaultNsURI;
        }
        else if (prefix2 == "xml") {
            ns2 = "http://www.w3.org/XML/1998/namespace";
        }
        else {
            ns2 = this.mNamespaces.findLastFromMap(prefix2);
            if (ns2 == null || ns2.length() == 0) {
                this.mReporter.throwParseError(ErrorConsts.ERR_NS_UNDECLARED, prefix2, null);
            }
        }
        this.mCurrElement.mNamespaceURI = ns2;
        final int xmlidIx = ac.resolveNamespaces(this.mReporter, this.mNamespaces);
        this.mIdAttrIndex = xmlidIx;
        final XMLValidator vld = this.mValidator;
        if (vld == null) {
            if (xmlidIx >= 0) {
                ac.normalizeSpacesInValue(xmlidIx);
            }
            return 4;
        }
        vld.validateElementStart(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        final int attrLen = ac.getCount();
        if (attrLen > 0) {
            for (int j = 0; j < attrLen; ++j) {
                ac.validateAttribute(j, this.mValidator);
            }
        }
        return this.mValidator.validateElementAndAttributes();
    }
    
    public int validateEndElement() throws XMLStreamException {
        if (this.mValidator == null) {
            return 4;
        }
        final int result = this.mValidator.validateElementEnd(this.mCurrElement.mLocalName, this.mCurrElement.mNamespaceURI, this.mCurrElement.mPrefix);
        if (this.mDepth == 1) {
            this.mValidator.validationCompleted(true);
        }
        return result;
    }
    
    @Override
    public final int getAttributeCount() {
        return this.mAttrCollector.getCount();
    }
    
    @Override
    public final int findAttributeIndex(final String nsURI, final String localName) {
        return this.mAttrCollector.findIndex(nsURI, localName);
    }
    
    @Override
    public final int getIdAttributeIndex() {
        if (this.mIdAttrIndex >= 0) {
            return this.mIdAttrIndex;
        }
        return (this.mValidator == null) ? -1 : this.mValidator.getIdAttrIndex();
    }
    
    @Override
    public final int getNotationAttributeIndex() {
        return (this.mValidator == null) ? -1 : this.mValidator.getNotationAttrIndex();
    }
    
    @Override
    public final String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() == 0) {
            if (this.mDepth == 0) {
                return "";
            }
            return this.mCurrElement.mDefaultNsURI;
        }
        else {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals("xmlns")) {
                return "http://www.w3.org/2000/xmlns/";
            }
            return this.mNamespaces.findLastNonInterned(prefix);
        }
    }
    
    @Override
    public final String getPrefix(final String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        String prefix = null;
        final String[] strs = this.mNamespaces.getInternalArray();
        final int len = this.mNamespaces.size();
    Label_0135:
        for (int index = len - 1; index > 0; index -= 2) {
            if (nsURI.equals(strs[index])) {
                prefix = strs[index - 1];
                for (int j = index + 1; j < len; j += 2) {
                    if (strs[j] == prefix) {
                        prefix = null;
                        continue Label_0135;
                    }
                }
                if (prefix == null) {
                    prefix = "";
                    break;
                }
                break;
            }
        }
        return prefix;
    }
    
    @Override
    public final Iterator<String> getPrefixes(final String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return DataUtil.singletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return DataUtil.singletonIterator("xmlns");
        }
        final String[] strs = this.mNamespaces.getInternalArray();
        final int len = this.mNamespaces.size();
        ArrayList<String> l = null;
    Label_0151:
        for (int index = len - 1; index > 0; index -= 2) {
            if (nsURI.equals(strs[index])) {
                final String prefix = strs[index - 1];
                for (int j = index + 1; j < len; j += 2) {
                    if (strs[j] == prefix) {
                        continue Label_0151;
                    }
                }
                if (l == null) {
                    l = new ArrayList<String>();
                }
                l.add(prefix);
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }
    
    @Override
    public final String getXmlVersion() {
        return this.mConfig.isXml11() ? "1.1" : "1.0";
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        return this.getAttrCollector().getLocalName(index);
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        return this.getAttrCollector().getURI(index);
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        return this.getAttrCollector().getPrefix(index);
    }
    
    @Override
    public String getAttributeValue(final int index) {
        return this.getAttrCollector().getValue(index);
    }
    
    @Override
    public String getAttributeValue(final String nsURI, final String localName) {
        final int ix = this.findAttributeIndex(nsURI, localName);
        return (ix < 0) ? null : this.getAttributeValue(ix);
    }
    
    @Override
    public boolean isNotationDeclared(final String name) {
        return false;
    }
    
    @Override
    public boolean isUnparsedEntityDeclared(final String name) {
        return false;
    }
    
    @Override
    public String getBaseUri() {
        return null;
    }
    
    @Override
    public final QName getCurrentElementName() {
        if (this.mDepth == 0) {
            return null;
        }
        String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            prefix = "";
        }
        final String nsURI = this.mCurrElement.mNamespaceURI;
        final String ln = this.mCurrElement.mLocalName;
        if (ln != this.mLastLocalName) {
            this.mLastLocalName = ln;
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        }
        else if (prefix != this.mLastPrefix) {
            this.mLastPrefix = prefix;
            this.mLastNsURI = nsURI;
        }
        else {
            if (nsURI == this.mLastNsURI) {
                return this.mLastName;
            }
            this.mLastNsURI = nsURI;
        }
        final QName n = QNameCreator.create(nsURI, ln, prefix);
        return this.mLastName = n;
    }
    
    @Override
    public Location getValidationLocation() {
        return this.mReporter.getLocation();
    }
    
    @Override
    public void reportProblem(final XMLValidationProblem problem) throws XMLStreamException {
        this.mReporter.reportValidationProblem(problem);
    }
    
    @Override
    public int addDefaultAttribute(final String localName, final String uri, final String prefix, final String value) throws XMLStreamException {
        return this.mAttrCollector.addDefaultAttribute(localName, uri, prefix, value);
    }
    
    public boolean isPrefixLocallyDeclared(String internedPrefix) {
        if (internedPrefix != null && internedPrefix.length() == 0) {
            internedPrefix = null;
        }
        for (int offset = this.mCurrElement.mNsOffset, len = this.mNamespaces.size(); offset < len; offset += 2) {
            final String thisPrefix = this.mNamespaces.getString(offset);
            if (thisPrefix == internedPrefix) {
                return true;
            }
        }
        return false;
    }
    
    public void addNsBinding(String prefix, String uri) {
        if (uri == null || uri.length() == 0) {
            uri = null;
        }
        if (prefix == null || prefix.length() == 0) {
            prefix = null;
            this.mCurrElement.mDefaultNsURI = uri;
        }
        this.mNamespaces.addStrings(prefix, uri);
    }
    
    public final void validateText(final TextBuffer tb, final boolean lastTextSegment) throws XMLStreamException {
        tb.validateText(this.mValidator, lastTextSegment);
    }
    
    public final void validateText(final String contents, final boolean lastTextSegment) throws XMLStreamException {
        this.mValidator.validateText(contents, lastTextSegment);
    }
    
    public final boolean isNamespaceAware() {
        return this.mNsAware;
    }
    
    public final boolean isEmpty() {
        return this.mDepth == 0;
    }
    
    public final int getDepth() {
        return this.mDepth;
    }
    
    public final String getDefaultNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mDefaultNsURI;
    }
    
    public final String getNsURI() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mNamespaceURI;
    }
    
    public final String getPrefix() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mPrefix;
    }
    
    public final String getLocalName() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        return this.mCurrElement.mLocalName;
    }
    
    public final boolean matches(final String prefix, final String localName) {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        final String thisPrefix = this.mCurrElement.mPrefix;
        if (prefix == null || prefix.length() == 0) {
            if (thisPrefix != null && thisPrefix.length() > 0) {
                return false;
            }
        }
        else if (thisPrefix != prefix && !thisPrefix.equals(prefix)) {
            return false;
        }
        final String thisName = this.mCurrElement.mLocalName;
        return thisName == localName || thisName.equals(localName);
    }
    
    public final String getTopElementDesc() {
        if (this.mDepth == 0) {
            throw new IllegalStateException("Illegal access, empty stack.");
        }
        final String name = this.mCurrElement.mLocalName;
        final String prefix = this.mCurrElement.mPrefix;
        if (prefix == null) {
            return name;
        }
        return prefix + ":" + name;
    }
    
    public final int getTotalNsCount() {
        return this.mNamespaces.size() >> 1;
    }
    
    public final int getCurrentNsCount() {
        return this.mNamespaces.size() - this.mCurrElement.mNsOffset >> 1;
    }
    
    public final String getLocalNsPrefix(int index) {
        final int offset = this.mCurrElement.mNsOffset;
        final int localCount = this.mNamespaces.size() - offset;
        index <<= 1;
        if (index < 0 || index >= localCount) {
            this.throwIllegalIndex(index >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index);
    }
    
    public final String getLocalNsURI(int index) {
        final int offset = this.mCurrElement.mNsOffset;
        final int localCount = this.mNamespaces.size() - offset;
        index <<= 1;
        if (index < 0 || index >= localCount) {
            this.throwIllegalIndex(index >> 1, localCount >> 1);
        }
        return this.mNamespaces.getString(offset + index + 1);
    }
    
    private void throwIllegalIndex(final int index, final int localCount) {
        throw new IllegalArgumentException("Illegal namespace index " + (index >> 1) + "; current scope only has " + (localCount >> 1) + " namespace declarations.");
    }
    
    @Override
    public final String getAttributeType(final int index) {
        if (index == this.mIdAttrIndex && index >= 0) {
            return "ID";
        }
        return (this.mValidator == null) ? "CDATA" : this.mValidator.getAttributeType(index);
    }
}
