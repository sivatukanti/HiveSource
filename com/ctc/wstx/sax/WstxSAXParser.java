// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sax;

import org.xml.sax.AttributeList;
import org.xml.sax.SAXParseException;
import com.ctc.wstx.exc.WstxIOException;
import javax.xml.stream.Location;
import java.util.Locale;
import org.xml.sax.Attributes;
import com.ctc.wstx.io.InputBootstrapper;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.SystemId;
import com.ctc.wstx.io.DefaultInputResolver;
import org.xml.sax.Locator;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.URLUtil;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.stream.XMLResolver;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.BasicStreamReader;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.dtd.DTDEventListener;
import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.XMLReader;
import org.xml.sax.Parser;
import javax.xml.parsers.SAXParser;

public class WstxSAXParser extends SAXParser implements Parser, XMLReader, Attributes2, Locator2, DTDEventListener
{
    static final boolean FEAT_DEFAULT_NS_PREFIXES = false;
    protected final WstxInputFactory mStaxFactory;
    protected final ReaderConfig mConfig;
    protected boolean mFeatNsPrefixes;
    protected BasicStreamReader mScanner;
    protected AttributeCollector mAttrCollector;
    protected InputElementStack mElemStack;
    protected String mEncoding;
    protected String mXmlVersion;
    protected boolean mStandalone;
    protected ContentHandler mContentHandler;
    protected DTDHandler mDTDHandler;
    private EntityResolver mEntityResolver;
    private ErrorHandler mErrorHandler;
    private LexicalHandler mLexicalHandler;
    private DeclHandler mDeclHandler;
    protected int mAttrCount;
    protected int mNsCount;
    
    public WstxSAXParser(final WstxInputFactory sf, final boolean nsPrefixes) {
        this.mNsCount = 0;
        this.mStaxFactory = sf;
        this.mFeatNsPrefixes = nsPrefixes;
        (this.mConfig = sf.createPrivateConfig()).doSupportDTDs(true);
        final ResolverProxy r = new ResolverProxy();
        this.mConfig.setDtdResolver(r);
        this.mConfig.setEntityResolver(r);
        this.mConfig.setDTDEventListener(this);
    }
    
    public WstxSAXParser() {
        this(new WstxInputFactory(), false);
    }
    
    @Override
    public final Parser getParser() {
        return this;
    }
    
    @Override
    public final XMLReader getXMLReader() {
        return this;
    }
    
    public final ReaderConfig getStaxConfig() {
        return this.mConfig;
    }
    
    @Override
    public boolean isNamespaceAware() {
        return this.mConfig.willSupportNamespaces();
    }
    
    @Override
    public boolean isValidating() {
        return this.mConfig.willValidateWithDTD();
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        final SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            return this.mDeclHandler;
        }
        if (prop == SAXProperty.DOCUMENT_XML_VERSION) {
            return this.mXmlVersion;
        }
        if (prop == SAXProperty.DOM_NODE) {
            return null;
        }
        if (prop == SAXProperty.LEXICAL_HANDLER) {
            return this.mLexicalHandler;
        }
        if (prop == SAXProperty.XML_STRING) {
            return null;
        }
        throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        final SAXProperty prop = SAXProperty.findByUri(name);
        if (prop == SAXProperty.DECLARATION_HANDLER) {
            this.mDeclHandler = (DeclHandler)value;
            return;
        }
        if (prop != SAXProperty.DOCUMENT_XML_VERSION) {
            if (prop != SAXProperty.DOM_NODE) {
                if (prop == SAXProperty.LEXICAL_HANDLER) {
                    this.mLexicalHandler = (LexicalHandler)value;
                    return;
                }
                if (prop != SAXProperty.XML_STRING) {
                    throw new SAXNotRecognizedException("Property '" + name + "' not recognized");
                }
            }
        }
        throw new SAXNotSupportedException("Property '" + name + "' is read-only, can not be modified");
    }
    
    @Override
    public void parse(final InputSource is, final HandlerBase hb) throws SAXException, IOException {
        if (hb != null) {
            if (this.mContentHandler == null) {
                this.setDocumentHandler(hb);
            }
            if (this.mEntityResolver == null) {
                this.setEntityResolver(hb);
            }
            if (this.mErrorHandler == null) {
                this.setErrorHandler(hb);
            }
            if (this.mDTDHandler == null) {
                this.setDTDHandler(hb);
            }
        }
        this.parse(is);
    }
    
    @Override
    public void parse(final InputSource is, final DefaultHandler dh) throws SAXException, IOException {
        if (dh != null) {
            if (this.mContentHandler == null) {
                this.setContentHandler(dh);
            }
            if (this.mEntityResolver == null) {
                this.setEntityResolver(dh);
            }
            if (this.mErrorHandler == null) {
                this.setErrorHandler(dh);
            }
            if (this.mDTDHandler == null) {
                this.setDTDHandler(dh);
            }
        }
        this.parse(is);
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this.mContentHandler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this.mDTDHandler;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return this.mEntityResolver;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.mErrorHandler;
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException {
        final SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            return this.mConfig.willSupportExternalEntities();
        }
        if (stdFeat == SAXFeature.IS_STANDALONE) {
            return this.mStandalone;
        }
        if (stdFeat == SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
            return false;
        }
        if (stdFeat == SAXFeature.NAMESPACES) {
            return this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
            return !this.mConfig.willSupportNamespaces();
        }
        if (stdFeat == SAXFeature.RESOLVE_DTD_URIS) {
            return false;
        }
        if (stdFeat == SAXFeature.STRING_INTERNING) {
            return true;
        }
        if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
            return false;
        }
        if (stdFeat == SAXFeature.USE_ATTRIBUTES2) {
            return true;
        }
        if (stdFeat == SAXFeature.USE_LOCATOR2) {
            return true;
        }
        if (stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
            return true;
        }
        if (stdFeat == SAXFeature.VALIDATION) {
            return this.mConfig.willValidateWithDTD();
        }
        if (stdFeat == SAXFeature.XMLNS_URIS) {
            return true;
        }
        if (stdFeat == SAXFeature.XML_1_1) {
            return true;
        }
        throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this.mContentHandler = handler;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) {
        this.mDTDHandler = handler;
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
        this.mEntityResolver = resolver;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this.mErrorHandler = handler;
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean invalidValue = false;
        boolean readOnly = false;
        final SAXFeature stdFeat = SAXFeature.findByUri(name);
        if (stdFeat == SAXFeature.EXTERNAL_GENERAL_ENTITIES) {
            this.mConfig.doSupportExternalEntities(value);
        }
        else if (stdFeat != SAXFeature.EXTERNAL_PARAMETER_ENTITIES) {
            if (stdFeat == SAXFeature.IS_STANDALONE) {
                readOnly = true;
            }
            else if (stdFeat != SAXFeature.LEXICAL_HANDLER_PARAMETER_ENTITIES) {
                if (stdFeat == SAXFeature.NAMESPACES) {
                    this.mConfig.doSupportNamespaces(value);
                }
                else if (stdFeat == SAXFeature.NAMESPACE_PREFIXES) {
                    this.mFeatNsPrefixes = value;
                }
                else if (stdFeat != SAXFeature.RESOLVE_DTD_URIS) {
                    if (stdFeat == SAXFeature.STRING_INTERNING) {
                        invalidValue = !value;
                    }
                    else if (stdFeat == SAXFeature.UNICODE_NORMALIZATION_CHECKING) {
                        invalidValue = value;
                    }
                    else if (stdFeat == SAXFeature.USE_ATTRIBUTES2) {
                        readOnly = true;
                    }
                    else if (stdFeat == SAXFeature.USE_LOCATOR2) {
                        readOnly = true;
                    }
                    else if (stdFeat == SAXFeature.USE_ENTITY_RESOLVER2) {
                        readOnly = true;
                    }
                    else if (stdFeat == SAXFeature.VALIDATION) {
                        this.mConfig.doValidateWithDTD(value);
                    }
                    else if (stdFeat == SAXFeature.XMLNS_URIS) {
                        invalidValue = !value;
                    }
                    else {
                        if (stdFeat != SAXFeature.XML_1_1) {
                            throw new SAXNotRecognizedException("Feature '" + name + "' not recognized");
                        }
                        readOnly = true;
                    }
                }
            }
        }
        if (readOnly) {
            throw new SAXNotSupportedException("Feature '" + name + "' is read-only, can not be modified");
        }
        if (invalidValue) {
            throw new SAXNotSupportedException("Trying to set invalid value for feature '" + name + "', '" + value + "'");
        }
    }
    
    @Override
    public void parse(final InputSource input) throws SAXException {
        this.mScanner = null;
        final String sysIdStr = input.getSystemId();
        final ReaderConfig cfg = this.mConfig;
        URL srcUrl = null;
        InputStream is = null;
        Reader r = input.getCharacterStream();
        if (r == null) {
            is = input.getByteStream();
            if (is == null) {
                if (sysIdStr == null) {
                    throw new SAXException("Invalid InputSource passed: neither character or byte stream passed, nor system id specified");
                }
                try {
                    srcUrl = URLUtil.urlFromSystemId(sysIdStr);
                    is = URLUtil.inputStreamFromURL(srcUrl);
                }
                catch (IOException ioe) {
                    final SAXException saxe = new SAXException(ioe);
                    ExceptionUtil.setInitCause(saxe, ioe);
                    throw saxe;
                }
            }
        }
        if (this.mContentHandler != null) {
            this.mContentHandler.setDocumentLocator(this);
            this.mContentHandler.startDocument();
        }
        cfg.resetState();
        try {
            final String inputEnc = input.getEncoding();
            final String publicId = input.getPublicId();
            if (r == null && inputEnc != null && inputEnc.length() > 0) {
                r = DefaultInputResolver.constructOptimizedReader(cfg, is, false, inputEnc);
            }
            final SystemId systemId = SystemId.construct(sysIdStr, srcUrl);
            if (r != null) {
                final InputBootstrapper bs = ReaderBootstrapper.getInstance(publicId, systemId, r, inputEnc);
                this.mScanner = (BasicStreamReader)this.mStaxFactory.createSR(cfg, systemId, bs, false, false);
            }
            else {
                final InputBootstrapper bs = StreamBootstrapper.getInstance(publicId, systemId, is);
                this.mScanner = (BasicStreamReader)this.mStaxFactory.createSR(cfg, systemId, bs, false, false);
            }
            String enc2 = this.mScanner.getEncoding();
            if (enc2 == null) {
                enc2 = this.mScanner.getCharacterEncodingScheme();
            }
            this.mEncoding = enc2;
            this.mXmlVersion = this.mScanner.getVersion();
            this.mStandalone = this.mScanner.standaloneSet();
            this.mAttrCollector = this.mScanner.getAttributeCollector();
            this.mElemStack = this.mScanner.getInputElementStack();
            this.fireEvents();
        }
        catch (IOException io) {
            this.throwSaxException(io);
        }
        catch (XMLStreamException strex) {
            this.throwSaxException(strex);
        }
        finally {
            if (this.mContentHandler != null) {
                this.mContentHandler.endDocument();
            }
            if (this.mScanner != null) {
                final BasicStreamReader sr = this.mScanner;
                this.mScanner = null;
                try {
                    sr.close();
                }
                catch (XMLStreamException ex) {}
            }
            if (r != null) {
                try {
                    r.close();
                }
                catch (IOException ex2) {}
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex3) {}
            }
        }
    }
    
    @Override
    public void parse(final String systemId) throws SAXException {
        final InputSource src = new InputSource(systemId);
        this.parse(src);
    }
    
    private final void fireEvents() throws IOException, SAXException, XMLStreamException {
        this.mConfig.doParseLazily(false);
        int type;
        while ((type = this.mScanner.next()) != 1) {
            this.fireAuxEvent(type, false);
        }
        this.fireStartTag();
        int depth = 1;
        while (true) {
            type = this.mScanner.next();
            if (type == 1) {
                this.fireStartTag();
                ++depth;
            }
            else if (type == 2) {
                this.mScanner.fireSaxEndElement(this.mContentHandler);
                if (--depth < 1) {
                    break;
                }
                continue;
            }
            else if (type == 4) {
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
            }
            else {
                this.fireAuxEvent(type, true);
            }
        }
        while (true) {
            type = this.mScanner.next();
            if (type == 8) {
                break;
            }
            if (type == 6) {
                continue;
            }
            this.fireAuxEvent(type, false);
        }
    }
    
    private final void fireAuxEvent(final int type, final boolean inTree) throws IOException, SAXException, XMLStreamException {
        switch (type) {
            case 5: {
                this.mScanner.fireSaxCommentEvent(this.mLexicalHandler);
                break;
            }
            case 12: {
                if (this.mLexicalHandler != null) {
                    this.mLexicalHandler.startCDATA();
                    this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                    this.mLexicalHandler.endCDATA();
                    break;
                }
                this.mScanner.fireSaxCharacterEvents(this.mContentHandler);
                break;
            }
            case 11: {
                if (this.mLexicalHandler != null) {
                    final String rootName = this.mScanner.getDTDRootName();
                    final String sysId = this.mScanner.getDTDSystemId();
                    final String pubId = this.mScanner.getDTDPublicId();
                    this.mLexicalHandler.startDTD(rootName, pubId, sysId);
                    try {
                        this.mScanner.getDTDInfo();
                    }
                    catch (WrappedSaxException wse) {
                        throw wse.getSaxException();
                    }
                    this.mLexicalHandler.endDTD();
                    break;
                }
                break;
            }
            case 3: {
                this.mScanner.fireSaxPIEvent(this.mContentHandler);
                break;
            }
            case 6: {
                if (inTree) {
                    this.mScanner.fireSaxSpaceEvents(this.mContentHandler);
                    break;
                }
                break;
            }
            case 9: {
                if (this.mContentHandler != null) {
                    this.mContentHandler.skippedEntity(this.mScanner.getLocalName());
                    break;
                }
                break;
            }
            default: {
                if (type == 8) {
                    this.throwSaxException("Unexpected end-of-input in " + (inTree ? "tree" : "prolog"));
                }
                throw new RuntimeException("Internal error: unexpected type, " + type);
            }
        }
    }
    
    private final void fireStartTag() throws SAXException {
        this.mAttrCount = this.mAttrCollector.getCount();
        if (this.mFeatNsPrefixes) {
            this.mNsCount = this.mElemStack.getCurrentNsCount();
        }
        this.mScanner.fireSaxStartElement(this.mContentHandler, this);
    }
    
    @Override
    public void setDocumentHandler(final DocumentHandler handler) {
        this.setContentHandler(new DocHandlerWrapper(handler));
    }
    
    @Override
    public void setLocale(final Locale locale) {
    }
    
    @Override
    public int getIndex(final String qName) {
        if (this.mElemStack == null) {
            return -1;
        }
        final int ix = this.mElemStack.findAttributeIndex(null, qName);
        return ix;
    }
    
    @Override
    public int getIndex(final String uri, final String localName) {
        if (this.mElemStack == null) {
            return -1;
        }
        final int ix = this.mElemStack.findAttributeIndex(uri, localName);
        return ix;
    }
    
    @Override
    public int getLength() {
        return this.mAttrCount + this.mNsCount;
    }
    
    @Override
    public String getLocalName(int index) {
        if (index < this.mAttrCount) {
            return (index < 0) ? null : this.mAttrCollector.getLocalName(index);
        }
        index -= this.mAttrCount;
        if (index < this.mNsCount) {
            final String prefix = this.mElemStack.getLocalNsPrefix(index);
            return (prefix == null || prefix.length() == 0) ? "xmlns" : prefix;
        }
        return null;
    }
    
    @Override
    public String getQName(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            final String prefix = this.mAttrCollector.getPrefix(index);
            final String ln = this.mAttrCollector.getLocalName(index);
            return (prefix == null || prefix.length() == 0) ? ln : (prefix + ":" + ln);
        }
        else {
            index -= this.mAttrCount;
            if (index >= this.mNsCount) {
                return null;
            }
            final String prefix = this.mElemStack.getLocalNsPrefix(index);
            if (prefix == null || prefix.length() == 0) {
                return "xmlns";
            }
            return "xmlns:" + prefix;
        }
    }
    
    @Override
    public String getType(int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            String type = this.mElemStack.getAttributeType(index);
            if (type == "ENUMERATED") {
                type = "NMTOKEN";
            }
            return type;
        }
        else {
            index -= this.mAttrCount;
            if (index < this.mNsCount) {
                return "CDATA";
            }
            return null;
        }
    }
    
    @Override
    public String getType(final String qName) {
        return this.getType(this.getIndex(qName));
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        return this.getType(this.getIndex(uri, localName));
    }
    
    @Override
    public String getURI(final int index) {
        if (index < this.mAttrCount) {
            if (index < 0) {
                return null;
            }
            final String uri = this.mAttrCollector.getURI(index);
            return (uri == null) ? "" : uri;
        }
        else {
            if (index - this.mAttrCount < this.mNsCount) {
                return "http://www.w3.org/2000/xmlns/";
            }
            return null;
        }
    }
    
    @Override
    public String getValue(int index) {
        if (index < this.mAttrCount) {
            return (index < 0) ? null : this.mAttrCollector.getValue(index);
        }
        index -= this.mAttrCount;
        if (index < this.mNsCount) {
            final String uri = this.mElemStack.getLocalNsURI(index);
            return (uri == null) ? "" : uri;
        }
        return null;
    }
    
    @Override
    public String getValue(final String qName) {
        return this.getValue(this.getIndex(qName));
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        return this.getValue(this.getIndex(uri, localName));
    }
    
    @Override
    public boolean isDeclared(int index) {
        if (index < this.mAttrCount) {
            if (index >= 0) {
                return true;
            }
        }
        else {
            index -= this.mAttrCount;
            if (index < this.mNsCount) {
                return true;
            }
        }
        this.throwNoSuchAttribute(index);
        return false;
    }
    
    @Override
    public boolean isDeclared(final String qName) {
        return false;
    }
    
    @Override
    public boolean isDeclared(final String uri, final String localName) {
        return false;
    }
    
    @Override
    public boolean isSpecified(int index) {
        if (index < this.mAttrCount) {
            if (index >= 0) {
                return this.mAttrCollector.isSpecified(index);
            }
        }
        else {
            index -= this.mAttrCount;
            if (index < this.mNsCount) {
                return true;
            }
        }
        this.throwNoSuchAttribute(index);
        return false;
    }
    
    @Override
    public boolean isSpecified(final String qName) {
        final int ix = this.getIndex(qName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with qName '" + qName + "'");
        }
        return this.isSpecified(ix);
    }
    
    @Override
    public boolean isSpecified(final String uri, final String localName) {
        final int ix = this.getIndex(uri, localName);
        if (ix < 0) {
            throw new IllegalArgumentException("No attribute with uri " + uri + ", local name '" + localName + "'");
        }
        return this.isSpecified(ix);
    }
    
    @Override
    public int getColumnNumber() {
        if (this.mScanner != null) {
            final Location loc = this.mScanner.getLocation();
            return loc.getColumnNumber();
        }
        return -1;
    }
    
    @Override
    public int getLineNumber() {
        if (this.mScanner != null) {
            final Location loc = this.mScanner.getLocation();
            return loc.getLineNumber();
        }
        return -1;
    }
    
    @Override
    public String getPublicId() {
        if (this.mScanner != null) {
            final Location loc = this.mScanner.getLocation();
            return loc.getPublicId();
        }
        return null;
    }
    
    @Override
    public String getSystemId() {
        if (this.mScanner != null) {
            final Location loc = this.mScanner.getLocation();
            return loc.getSystemId();
        }
        return null;
    }
    
    @Override
    public String getEncoding() {
        return this.mEncoding;
    }
    
    @Override
    public String getXMLVersion() {
        return this.mXmlVersion;
    }
    
    @Override
    public boolean dtdReportComments() {
        return this.mLexicalHandler != null;
    }
    
    @Override
    public void dtdComment(final char[] data, final int offset, final int len) {
        if (this.mLexicalHandler != null) {
            try {
                this.mLexicalHandler.comment(data, offset, len);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdProcessingInstruction(final String target, final String data) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.processingInstruction(target, data);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdSkippedEntity(final String name) {
        if (this.mContentHandler != null) {
            try {
                this.mContentHandler.skippedEntity(name);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdNotationDecl(final String name, final String publicId, String systemId, final URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId != null && systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.notationDecl(name, publicId, systemId);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdUnparsedEntityDecl(final String name, final String publicId, String systemId, final String notationName, final URL baseURL) throws XMLStreamException {
        if (this.mDTDHandler != null) {
            if (systemId.indexOf(58) < 0) {
                try {
                    systemId = URLUtil.urlFromSystemId(systemId, baseURL).toExternalForm();
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            try {
                this.mDTDHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void attributeDecl(final String eName, final String aName, final String type, final String mode, final String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.attributeDecl(eName, aName, type, mode, value);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdElementDecl(final String name, final String model) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.elementDecl(name, model);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdExternalEntityDecl(final String name, final String publicId, final String systemId) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.externalEntityDecl(name, publicId, systemId);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    @Override
    public void dtdInternalEntityDecl(final String name, final String value) {
        if (this.mDeclHandler != null) {
            try {
                this.mDeclHandler.internalEntityDecl(name, value);
            }
            catch (SAXException sex) {
                throw new WrappedSaxException(sex);
            }
        }
    }
    
    private void throwSaxException(final Exception src) throws SAXException {
        final SAXParseException se = new SAXParseException(src.getMessage(), this, src);
        ExceptionUtil.setInitCause(se, src);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }
    
    private void throwSaxException(final String msg) throws SAXException {
        final SAXParseException se = new SAXParseException(msg, this);
        if (this.mErrorHandler != null) {
            this.mErrorHandler.fatalError(se);
        }
        throw se;
    }
    
    private void throwNoSuchAttribute(final int index) {
        throw new IllegalArgumentException("No attribute with index " + index + " (have " + (this.mAttrCount + this.mNsCount) + " attributes)");
    }
    
    final class ResolverProxy implements XMLResolver
    {
        public ResolverProxy() {
        }
        
        @Override
        public Object resolveEntity(final String publicID, final String systemID, final String baseURI, final String namespace) throws XMLStreamException {
            if (WstxSAXParser.this.mEntityResolver != null) {
                try {
                    final URL url = new URL(baseURI);
                    final String ref = new URL(url, systemID).toExternalForm();
                    final InputSource isrc = WstxSAXParser.this.mEntityResolver.resolveEntity(publicID, ref);
                    if (isrc != null) {
                        final InputStream in = isrc.getByteStream();
                        if (in != null) {
                            return in;
                        }
                        final Reader r = isrc.getCharacterStream();
                        if (r != null) {
                            return r;
                        }
                    }
                    return null;
                }
                catch (IOException ex) {
                    throw new WstxIOException(ex);
                }
                catch (Exception ex2) {
                    throw new XMLStreamException(ex2.getMessage(), ex2);
                }
            }
            return null;
        }
    }
    
    static final class DocHandlerWrapper implements ContentHandler
    {
        final DocumentHandler mDocHandler;
        final AttributesWrapper mAttrWrapper;
        
        DocHandlerWrapper(final DocumentHandler h) {
            this.mAttrWrapper = new AttributesWrapper();
            this.mDocHandler = h;
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            this.mDocHandler.characters(ch, start, length);
        }
        
        @Override
        public void endDocument() throws SAXException {
            this.mDocHandler.endDocument();
        }
        
        @Override
        public void endElement(final String uri, final String localName, String qName) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mDocHandler.endElement(qName);
        }
        
        @Override
        public void endPrefixMapping(final String prefix) {
        }
        
        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
            this.mDocHandler.ignorableWhitespace(ch, start, length);
        }
        
        @Override
        public void processingInstruction(final String target, final String data) throws SAXException {
            this.mDocHandler.processingInstruction(target, data);
        }
        
        @Override
        public void setDocumentLocator(final Locator locator) {
            this.mDocHandler.setDocumentLocator(locator);
        }
        
        @Override
        public void skippedEntity(final String name) {
        }
        
        @Override
        public void startDocument() throws SAXException {
            this.mDocHandler.startDocument();
        }
        
        @Override
        public void startElement(final String uri, final String localName, String qName, final Attributes attrs) throws SAXException {
            if (qName == null) {
                qName = localName;
            }
            this.mAttrWrapper.setAttributes(attrs);
            this.mDocHandler.startElement(qName, this.mAttrWrapper);
        }
        
        @Override
        public void startPrefixMapping(final String prefix, final String uri) {
        }
    }
    
    static final class AttributesWrapper implements AttributeList
    {
        Attributes mAttrs;
        
        public AttributesWrapper() {
        }
        
        public void setAttributes(final Attributes a) {
            this.mAttrs = a;
        }
        
        @Override
        public int getLength() {
            return this.mAttrs.getLength();
        }
        
        @Override
        public String getName(final int i) {
            final String n = this.mAttrs.getQName(i);
            return (n == null) ? this.mAttrs.getLocalName(i) : n;
        }
        
        @Override
        public String getType(final int i) {
            return this.mAttrs.getType(i);
        }
        
        @Override
        public String getType(final String name) {
            return this.mAttrs.getType(name);
        }
        
        @Override
        public String getValue(final int i) {
            return this.mAttrs.getValue(i);
        }
        
        @Override
        public String getValue(final String name) {
            return this.mAttrs.getValue(name);
        }
    }
}
