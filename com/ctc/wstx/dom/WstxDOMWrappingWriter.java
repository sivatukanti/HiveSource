// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dom;

import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.dom.DOMResult;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.HashMap;
import com.ctc.wstx.api.WriterConfig;
import org.codehaus.stax2.ri.dom.DOMWrappingWriter;

public class WstxDOMWrappingWriter extends DOMWrappingWriter
{
    protected static final String ERR_NSDECL_WRONG_STATE = "Trying to write a namespace declaration when there is no open start element.";
    protected final WriterConfig mConfig;
    protected DOMOutputElement mCurrElem;
    protected DOMOutputElement mOpenElement;
    protected int[] mAutoNsSeq;
    protected String mSuggestedDefNs;
    protected String mAutomaticNsPrefix;
    HashMap<String, String> mSuggestedPrefixes;
    
    private WstxDOMWrappingWriter(final WriterConfig cfg, final Node treeRoot) throws XMLStreamException {
        super(treeRoot, cfg.willSupportNamespaces(), cfg.automaticNamespacesEnabled());
        this.mSuggestedDefNs = null;
        this.mSuggestedPrefixes = null;
        this.mConfig = cfg;
        this.mAutoNsSeq = null;
        this.mAutomaticNsPrefix = (this.mNsRepairing ? this.mConfig.getAutomaticNsPrefix() : null);
        switch (treeRoot.getNodeType()) {
            case 9:
            case 11: {
                this.mCurrElem = DOMOutputElement.createRoot(treeRoot);
                this.mOpenElement = null;
                break;
            }
            case 1: {
                final DOMOutputElement root = DOMOutputElement.createRoot(treeRoot);
                final Element elem = (Element)treeRoot;
                final DOMOutputElement child = root.createChild(elem);
                this.mCurrElem = child;
                this.mOpenElement = child;
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + treeRoot.getClass());
            }
        }
    }
    
    public static WstxDOMWrappingWriter createFrom(final WriterConfig cfg, final DOMResult dst) throws XMLStreamException {
        final Node rootNode = dst.getNode();
        return new WstxDOMWrappingWriter(cfg, rootNode);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        if (!this.mNsAware) {
            return EmptyNamespaceContext.getInstance();
        }
        return this.mCurrElem;
    }
    
    @Override
    public String getPrefix(final String uri) {
        if (!this.mNsAware) {
            return null;
        }
        if (this.mNsContext != null) {
            final String prefix = this.mNsContext.getPrefix(uri);
            if (prefix != null) {
                return prefix;
            }
        }
        return this.mCurrElem.getPrefix(uri);
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.mConfig.getProperty(name);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) {
        this.mSuggestedDefNs = ((uri == null || uri.length() == 0) ? null : uri);
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
        if (prefix.equals("xml")) {
            if (!uri.equals("http://www.w3.org/XML/1998/namespace")) {
                DOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML, uri);
            }
        }
        else {
            if (prefix.equals("xmlns")) {
                if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                    DOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS, uri);
                }
                return;
            }
            if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
                DOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix);
            }
            else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
                DOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI, prefix);
            }
        }
        if (this.mSuggestedPrefixes == null) {
            this.mSuggestedPrefixes = new HashMap<String, String>(16);
        }
        this.mSuggestedPrefixes.put(uri, prefix);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.outputAttribute(null, null, localName, value);
    }
    
    @Override
    public void writeAttribute(final String nsURI, final String localName, final String value) throws XMLStreamException {
        this.outputAttribute(nsURI, null, localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String nsURI, final String localName, final String value) throws XMLStreamException {
        this.outputAttribute(nsURI, prefix, localName, value);
    }
    
    @Override
    public void writeDefaultNamespace(final String nsURI) {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        this.setDefaultNamespace(nsURI);
        this.mOpenElement.addAttribute("http://www.w3.org/2000/xmlns/", "xmlns", nsURI);
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName);
    }
    
    @Override
    public void writeEmptyElement(final String nsURI, final String localName) throws XMLStreamException {
        this.createStartElem(nsURI, null, localName, true);
    }
    
    @Override
    public void writeEmptyElement(String prefix, final String localName, final String nsURI) throws XMLStreamException {
        if (prefix == null) {
            prefix = "";
        }
        this.createStartElem(nsURI, prefix, localName, true);
    }
    
    @Override
    public void writeEndDocument() {
        final DOMOutputElement domOutputElement = null;
        this.mOpenElement = domOutputElement;
        this.mCurrElem = domOutputElement;
    }
    
    @Override
    public void writeEndElement() {
        if (this.mCurrElem == null || this.mCurrElem.isRoot()) {
            throw new IllegalStateException("No open start element to close");
        }
        this.mOpenElement = null;
        this.mCurrElem = this.mCurrElem.getParent();
    }
    
    @Override
    public void writeNamespace(final String prefix, final String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mNsAware) {
            DOMWrappingWriter.throwOutputError("Can not write namespaces with non-namespace writer.");
        }
        this.outputAttribute("http://www.w3.org/2000/xmlns/", "xmlns", prefix, nsURI);
        this.mCurrElem.addPrefix(prefix, nsURI);
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writeStartElement(null, localName);
    }
    
    @Override
    public void writeStartElement(final String nsURI, final String localName) throws XMLStreamException {
        this.createStartElem(nsURI, null, localName, false);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.createStartElem(nsURI, prefix, localName, false);
    }
    
    @Override
    public boolean isPropertySupported(final String name) {
        return this.mConfig.isPropertySupported(name);
    }
    
    @Override
    public boolean setProperty(final String name, final Object value) {
        return this.mConfig.setProperty(name, value);
    }
    
    @Override
    public void writeDTD(final String rootName, final String systemId, final String publicId, final String internalSubset) throws XMLStreamException {
        if (this.mCurrElem != null) {
            throw new IllegalStateException("Operation only allowed to the document before adding root element");
        }
        this.reportUnsupported("writeDTD()");
    }
    
    @Override
    protected void appendLeaf(final Node n) throws IllegalStateException {
        this.mCurrElem.appendNode(n);
        this.mOpenElement = null;
    }
    
    protected void createStartElem(final String nsURI, String prefix, String localName, final boolean isEmpty) throws XMLStreamException {
        DOMOutputElement elem;
        if (!this.mNsAware) {
            if (nsURI != null && nsURI.length() > 0) {
                DOMWrappingWriter.throwOutputError("Can not specify non-empty uri/prefix in non-namespace mode");
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElement(localName));
        }
        else if (this.mNsRepairing) {
            String actPrefix = this.validateElemPrefix(prefix, nsURI, this.mCurrElem);
            if (actPrefix != null) {
                if (actPrefix.length() != 0) {
                    elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, actPrefix + ":" + localName));
                }
                else {
                    elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
                }
            }
            else {
                if (prefix == null) {
                    prefix = "";
                }
                actPrefix = this.generateElemPrefix(prefix, nsURI, this.mCurrElem);
                final boolean hasPrefix = actPrefix.length() != 0;
                if (hasPrefix) {
                    localName = actPrefix + ":" + localName;
                }
                elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
                this.mOpenElement = elem;
                if (hasPrefix) {
                    this.writeNamespace(actPrefix, nsURI);
                    elem.addPrefix(actPrefix, nsURI);
                }
                else {
                    this.writeDefaultNamespace(nsURI);
                    elem.setDefaultNsUri(nsURI);
                }
            }
        }
        else {
            if (prefix == null && nsURI != null && nsURI.length() > 0) {
                prefix = ((this.mSuggestedPrefixes == null) ? null : this.mSuggestedPrefixes.get(nsURI));
                if (prefix == null) {
                    DOMWrappingWriter.throwOutputError("Can not find prefix for namespace \"" + nsURI + "\"");
                }
            }
            if (prefix != null && prefix.length() != 0) {
                localName = prefix + ":" + localName;
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
        }
        this.mOpenElement = elem;
        if (!isEmpty) {
            this.mCurrElem = elem;
        }
    }
    
    protected void outputAttribute(final String nsURI, String prefix, String localName, final String value) throws XMLStreamException {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        if (this.mNsAware) {
            if (this.mNsRepairing) {
                prefix = this.findOrCreateAttrPrefix(prefix, nsURI, this.mOpenElement);
            }
            if (prefix != null && prefix.length() > 0) {
                localName = prefix + ":" + localName;
            }
            this.mOpenElement.addAttribute(nsURI, localName, value);
        }
        else {
            if (prefix != null && prefix.length() > 0) {
                localName = prefix + ":" + localName;
            }
            this.mOpenElement.addAttribute(localName, value);
        }
    }
    
    private final String validateElemPrefix(final String prefix, final String nsURI, final DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            final String currURL = elem.getDefaultNsUri();
            if (currURL == null || currURL.length() == 0) {
                return "";
            }
            return null;
        }
        else {
            final int status = elem.isPrefixValid(prefix, nsURI, true);
            if (status == 1) {
                return prefix;
            }
            return null;
        }
    }
    
    protected final String findElemPrefix(final String nsURI, final DOMOutputElement elem) throws XMLStreamException {
        if (nsURI != null && nsURI.length() != 0) {
            return this.mCurrElem.getPrefix(nsURI);
        }
        final String currDefNsURI = elem.getDefaultNsUri();
        if (currDefNsURI != null && currDefNsURI.length() > 0) {
            return null;
        }
        return "";
    }
    
    protected final String generateElemPrefix(String suggPrefix, final String nsURI, final DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return "";
        }
        if (suggPrefix == null) {
            if (this.mSuggestedDefNs != null && this.mSuggestedDefNs.equals(nsURI)) {
                suggPrefix = "";
            }
            else {
                suggPrefix = ((this.mSuggestedPrefixes == null) ? null : this.mSuggestedPrefixes.get(nsURI));
                if (suggPrefix == null) {
                    if (this.mAutoNsSeq == null) {
                        (this.mAutoNsSeq = new int[1])[0] = 1;
                    }
                    suggPrefix = elem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
                }
            }
        }
        return suggPrefix;
    }
    
    protected final String findOrCreateAttrPrefix(final String suggPrefix, final String nsURI, final DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return null;
        }
        if (suggPrefix != null) {
            final int status = elem.isPrefixValid(suggPrefix, nsURI, false);
            if (status == 1) {
                return suggPrefix;
            }
            if (status == 0) {
                elem.addPrefix(suggPrefix, nsURI);
                this.writeNamespace(suggPrefix, nsURI);
                return suggPrefix;
            }
        }
        String prefix = elem.getExplicitPrefix(nsURI);
        if (prefix != null) {
            return prefix;
        }
        if (suggPrefix != null) {
            prefix = suggPrefix;
        }
        else if (this.mSuggestedPrefixes != null) {
            prefix = this.mSuggestedPrefixes.get(nsURI);
        }
        if (prefix != null && (prefix.length() == 0 || elem.getNamespaceURI(prefix) != null)) {
            prefix = null;
        }
        if (prefix == null) {
            if (this.mAutoNsSeq == null) {
                (this.mAutoNsSeq = new int[1])[0] = 1;
            }
            prefix = this.mCurrElem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
        }
        elem.addPrefix(prefix, nsURI);
        this.writeNamespace(prefix, nsURI);
        return prefix;
    }
}
