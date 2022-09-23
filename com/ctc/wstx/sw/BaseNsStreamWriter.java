// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import com.ctc.wstx.util.DefaultXmlSymbolTable;
import javax.xml.stream.events.StartElement;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import javax.xml.namespace.QName;
import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.api.WriterConfig;
import javax.xml.namespace.NamespaceContext;
import com.ctc.wstx.api.EmptyElementHandler;

public abstract class BaseNsStreamWriter extends TypedStreamWriter
{
    protected static final String sPrefixXml;
    protected static final String sPrefixXmlns;
    protected static final String ERR_NSDECL_WRONG_STATE = "Trying to write a namespace declaration when there is no open start element.";
    protected final boolean mAutomaticNS;
    protected final EmptyElementHandler mEmptyElementHandler;
    protected SimpleOutputElement mCurrElem;
    protected NamespaceContext mRootNsContext;
    protected SimpleOutputElement mOutputElemPool;
    static final int MAX_POOL_SIZE = 8;
    protected int mPoolSize;
    
    public BaseNsStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg, final boolean repairing) {
        super(xw, enc, cfg);
        this.mCurrElem = SimpleOutputElement.createRoot();
        this.mRootNsContext = null;
        this.mOutputElemPool = null;
        this.mPoolSize = 0;
        this.mAutomaticNS = repairing;
        this.mEmptyElementHandler = cfg.getEmptyElementHandler();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.mCurrElem;
    }
    
    @Override
    public String getPrefix(final String uri) {
        return this.mCurrElem.getPrefix(uri);
    }
    
    @Override
    public abstract void setDefaultNamespace(final String p0) throws XMLStreamException;
    
    @Override
    public void setNamespaceContext(final NamespaceContext ctxt) throws XMLStreamException {
        if (this.mState != 1) {
            BaseStreamWriter.throwOutputError("Called setNamespaceContext() after having already output root element.");
        }
        this.mRootNsContext = ctxt;
        this.mCurrElem.setRootNsContext(ctxt);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new NullPointerException("Can not pass null 'prefix' value");
        }
        if (prefix.length() == 0) {
            this.setDefaultNamespace(uri);
            return;
        }
        if (uri == null) {
            throw new NullPointerException("Can not pass null 'uri' value");
        }
        if (prefix.equals(BaseNsStreamWriter.sPrefixXml)) {
            if (!uri.equals("http://www.w3.org/XML/1998/namespace")) {
                BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML, uri);
            }
        }
        else if (prefix.equals(BaseNsStreamWriter.sPrefixXmlns)) {
            if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS, uri);
            }
        }
        else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix);
        }
        else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI, prefix);
        }
        if (!this.mXml11 && uri.length() == 0) {
            BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_EMPTY);
        }
        this.doSetPrefix(prefix, uri);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, null, null, value);
    }
    
    @Override
    public abstract void writeAttribute(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    @Override
    public abstract void writeAttribute(final String p0, final String p1, final String p2, final String p3) throws XMLStreamException;
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.checkStartElement(localName, null);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mEmptyElement = true;
        if (this.mOutputElemPool != null) {
            final SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, localName);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        }
        else {
            this.mCurrElem = this.mCurrElem.createChild(localName);
        }
        this.doWriteStartTag(localName);
    }
    
    @Override
    public void writeEmptyElement(final String nsURI, final String localName) throws XMLStreamException {
        this.writeStartOrEmpty(localName, nsURI);
        this.mEmptyElement = true;
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.writeStartOrEmpty(prefix, localName, nsURI);
        this.mEmptyElement = true;
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, this.mCfgAutomaticEmptyElems);
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.checkStartElement(localName, null);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mEmptyElement = false;
        if (this.mOutputElemPool != null) {
            final SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, localName);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        }
        else {
            this.mCurrElem = this.mCurrElem.createChild(localName);
        }
        this.doWriteStartTag(localName);
    }
    
    @Override
    public void writeStartElement(final String nsURI, final String localName) throws XMLStreamException {
        this.writeStartOrEmpty(localName, nsURI);
        this.mEmptyElement = false;
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.writeStartOrEmpty(prefix, localName, nsURI);
        this.mEmptyElement = false;
    }
    
    @Override
    protected void writeTypedAttribute(final String prefix, final String nsURI, final String localName, final AsciiValueEncoder enc) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        try {
            if (this.mValidator == null) {
                if (prefix == null || prefix.length() == 0) {
                    this.mWriter.writeTypedAttribute(localName, enc);
                }
                else {
                    this.mWriter.writeTypedAttribute(prefix, localName, enc);
                }
            }
            else {
                this.mWriter.writeTypedAttribute(prefix, localName, nsURI, enc, this.mValidator, this.getCopyBuffer());
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public void writeFullEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, false);
    }
    
    @Override
    public QName getCurrentElementName() {
        return this.mCurrElem.getName();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return this.mCurrElem.getNamespaceURI(prefix);
    }
    
    @Override
    public void writeEndElement(final QName name) throws XMLStreamException {
        this.doWriteEndTag(this.mCheckStructure ? name : null, this.mCfgAutomaticEmptyElems);
    }
    
    @Override
    protected void closeStartElement(final boolean emptyElem) throws XMLStreamException {
        this.mStartElementOpen = false;
        try {
            if (emptyElem) {
                this.mWriter.writeStartTagEmptyEnd();
            }
            else {
                this.mWriter.writeStartTagEnd();
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementAndAttributes();
        }
        if (emptyElem) {
            final SimpleOutputElement curr = this.mCurrElem;
            this.mCurrElem = curr.getParent();
            if (this.mCurrElem.isRoot()) {
                this.mState = 3;
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementEnd(curr.getLocalName(), curr.getNamespaceURI(), curr.getPrefix());
            }
            if (this.mPoolSize < 8) {
                curr.addToPool(this.mOutputElemPool);
                this.mOutputElemPool = curr;
                ++this.mPoolSize;
            }
        }
    }
    
    @Override
    protected String getTopElementDesc() {
        return this.mCurrElem.getNameDesc();
    }
    
    protected void checkStartElement(final String localName, final String prefix) throws XMLStreamException {
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        else if (this.mState == 1) {
            this.verifyRootElement(localName, prefix);
        }
        else if (this.mState == 3) {
            if (this.mCheckStructure) {
                final String name = (prefix == null || prefix.length() == 0) ? localName : (prefix + ":" + localName);
                BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_SECOND_ROOT, name);
            }
            this.mState = 2;
        }
    }
    
    protected final void doWriteAttr(final String localName, final String nsURI, final String prefix, final String value) throws XMLStreamException {
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, nsURI, prefix, value);
        }
        try {
            final int vlen = value.length();
            if (vlen >= 12) {
                char[] buf = this.mCopyBuffer;
                if (buf == null) {
                    buf = (this.mCopyBuffer = this.mConfig.allocMediumCBuffer(512));
                }
                if (vlen <= buf.length) {
                    value.getChars(0, vlen, buf, 0);
                    if (prefix != null && prefix.length() > 0) {
                        this.mWriter.writeAttribute(prefix, localName, buf, 0, vlen);
                    }
                    else {
                        this.mWriter.writeAttribute(localName, buf, 0, vlen);
                    }
                    return;
                }
            }
            if (prefix != null && prefix.length() > 0) {
                this.mWriter.writeAttribute(prefix, localName, value);
            }
            else {
                this.mWriter.writeAttribute(localName, value);
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected final void doWriteAttr(final String localName, final String nsURI, final String prefix, final char[] buf, final int start, final int len) throws XMLStreamException {
        if (this.mCheckAttrs) {
            this.mCurrElem.checkAttrWrite(nsURI, localName);
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, nsURI, prefix, buf, start, len);
        }
        try {
            if (prefix != null && prefix.length() > 0) {
                this.mWriter.writeAttribute(prefix, localName, buf, start, len);
            }
            else {
                this.mWriter.writeAttribute(localName, buf, start, len);
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected void doWriteNamespace(final String prefix, final String nsURI) throws XMLStreamException {
        try {
            final int vlen = nsURI.length();
            if (vlen >= 12) {
                char[] buf = this.mCopyBuffer;
                if (buf == null) {
                    buf = (this.mCopyBuffer = this.mConfig.allocMediumCBuffer(512));
                }
                if (vlen <= buf.length) {
                    nsURI.getChars(0, vlen, buf, 0);
                    this.mWriter.writeAttribute("xmlns", prefix, buf, 0, vlen);
                    return;
                }
            }
            this.mWriter.writeAttribute("xmlns", prefix, nsURI);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected void doWriteDefaultNs(final String nsURI) throws XMLStreamException {
        try {
            final int vlen = (nsURI == null) ? 0 : nsURI.length();
            if (vlen >= 12) {
                char[] buf = this.mCopyBuffer;
                if (buf == null) {
                    buf = (this.mCopyBuffer = this.mConfig.allocMediumCBuffer(512));
                }
                if (vlen <= buf.length) {
                    nsURI.getChars(0, vlen, buf, 0);
                    this.mWriter.writeAttribute("xmlns", buf, 0, vlen);
                    return;
                }
            }
            this.mWriter.writeAttribute("xmlns", nsURI);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected final void doWriteStartTag(final String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        this.mStartElementOpen = true;
        try {
            this.mWriter.writeStartTagStart(localName);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected final void doWriteStartTag(final String prefix, final String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        this.mStartElementOpen = true;
        try {
            final boolean hasPrefix = prefix != null && prefix.length() > 0;
            if (hasPrefix) {
                this.mWriter.writeStartTagStart(prefix, localName);
            }
            else {
                this.mWriter.writeStartTagStart(localName);
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    protected void doWriteEndTag(final QName expName, boolean allowEmpty) throws XMLStreamException {
        if (this.mStartElementOpen && this.mEmptyElement) {
            this.mEmptyElement = false;
            this.closeStartElement(true);
        }
        if (this.mState != 2) {
            BaseStreamWriter.reportNwfStructure("No open start element, when trying to write end element");
        }
        final SimpleOutputElement thisElem = this.mCurrElem;
        final String prefix = thisElem.getPrefix();
        final String localName = thisElem.getLocalName();
        final String nsURI = thisElem.getNamespaceURI();
        this.mCurrElem = thisElem.getParent();
        if (this.mPoolSize < 8) {
            thisElem.addToPool(this.mOutputElemPool);
            this.mOutputElemPool = thisElem;
            ++this.mPoolSize;
        }
        if (this.mCheckStructure && expName != null && !localName.equals(expName.getLocalPart())) {
            BaseStreamWriter.reportNwfStructure("Mismatching close element local name, '" + localName + "'; expected '" + expName.getLocalPart() + "'.");
        }
        if (this.mStartElementOpen) {
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            this.mStartElementOpen = false;
            try {
                if (this.mEmptyElementHandler != null) {
                    allowEmpty = this.mEmptyElementHandler.allowEmptyElement(prefix, localName, nsURI, allowEmpty);
                }
                if (allowEmpty) {
                    this.mWriter.writeStartTagEmptyEnd();
                    if (this.mCurrElem.isRoot()) {
                        this.mState = 3;
                    }
                    if (this.mValidator != null) {
                        this.mVldContent = this.mValidator.validateElementEnd(localName, nsURI, prefix);
                    }
                    return;
                }
                this.mWriter.writeStartTagEnd();
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
        try {
            this.mWriter.writeEndTag(prefix, localName);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
        if (this.mCurrElem.isRoot()) {
            this.mState = 3;
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementEnd(localName, nsURI, prefix);
        }
    }
    
    public abstract void doSetPrefix(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeDefaultNamespace(final String p0) throws XMLStreamException;
    
    @Override
    public abstract void writeNamespace(final String p0, final String p1) throws XMLStreamException;
    
    @Override
    public abstract void writeStartElement(final StartElement p0) throws XMLStreamException;
    
    protected abstract void writeStartOrEmpty(final String p0, final String p1) throws XMLStreamException;
    
    protected abstract void writeStartOrEmpty(final String p0, final String p1, final String p2) throws XMLStreamException;
    
    static {
        sPrefixXml = DefaultXmlSymbolTable.getXmlSymbol();
        sPrefixXmlns = DefaultXmlSymbolTable.getXmlnsSymbol();
    }
}
