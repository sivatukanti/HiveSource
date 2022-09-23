// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.io.IOException;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.api.WriterConfig;
import java.util.HashMap;

public final class RepairingNsStreamWriter extends BaseNsStreamWriter
{
    protected final String mAutomaticNsPrefix;
    protected int[] mAutoNsSeq;
    protected String mSuggestedDefNs;
    protected HashMap<String, String> mSuggestedPrefixes;
    
    public RepairingNsStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg) {
        super(xw, enc, cfg, true);
        this.mAutoNsSeq = null;
        this.mSuggestedDefNs = null;
        this.mSuggestedPrefixes = null;
        this.mAutomaticNsPrefix = cfg.getAutomaticNsPrefix();
    }
    
    @Override
    public void writeAttribute(final String nsURI, final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, this.findOrCreateAttrPrefix(null, nsURI, this.mCurrElem), value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String nsURI, final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, this.findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), value);
    }
    
    @Override
    public void writeDefaultNamespace(final String nsURI) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        final String prefix = this.mCurrElem.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            this.mCurrElem.setDefaultNsUri(nsURI);
            this.doWriteDefaultNs(nsURI);
        }
    }
    
    @Override
    public void writeNamespace(final String prefix, final String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        final int value = this.mCurrElem.isPrefixValid(prefix, nsURI, true);
        if (value == 0) {
            this.mCurrElem.addPrefix(prefix, nsURI);
            this.doWriteNamespace(prefix, nsURI);
        }
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.mSuggestedDefNs = ((uri == null || uri.length() == 0) ? null : uri);
    }
    
    @Override
    public void doSetPrefix(final String prefix, final String uri) throws XMLStreamException {
        if (uri == null || uri.length() == 0) {
            if (this.mSuggestedPrefixes != null) {
                final Iterator<Map.Entry<String, String>> it = this.mSuggestedPrefixes.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry<String, String> en = it.next();
                    final String thisP = en.getValue();
                    if (thisP.equals(prefix)) {
                        it.remove();
                    }
                }
            }
        }
        else {
            if (this.mSuggestedPrefixes == null) {
                this.mSuggestedPrefixes = new HashMap<String, String>(16);
            }
            this.mSuggestedPrefixes.put(uri, prefix);
        }
    }
    
    @Override
    public void writeStartElement(final StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        this.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        final Iterator<Attribute> it = (Iterator<Attribute>)elem.getAttributes();
        while (it.hasNext()) {
            final Attribute attr = it.next();
            name = attr.getName();
            this.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
        }
    }
    
    @Override
    protected void writeTypedAttribute(final String prefix, final String nsURI, final String localName, final AsciiValueEncoder enc) throws XMLStreamException {
        super.writeTypedAttribute(this.findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), nsURI, localName, enc);
    }
    
    @Override
    protected void writeStartOrEmpty(final String localName, final String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, "");
        String prefix = this.findElemPrefix(nsURI, this.mCurrElem);
        if (this.mOutputElemPool != null) {
            final SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        }
        else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        if (prefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, prefix);
            }
            this.doWriteStartTag(prefix, localName);
        }
        else {
            prefix = this.generateElemPrefix(null, nsURI, this.mCurrElem);
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, prefix);
            }
            this.mCurrElem.setPrefix(prefix);
            this.doWriteStartTag(prefix, localName);
            if (prefix == null || prefix.length() == 0) {
                this.mCurrElem.setDefaultNsUri(nsURI);
                this.doWriteDefaultNs(nsURI);
            }
            else {
                this.mCurrElem.addPrefix(prefix, nsURI);
                this.doWriteNamespace(prefix, nsURI);
            }
        }
    }
    
    @Override
    protected void writeStartOrEmpty(String suggPrefix, final String localName, final String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, suggPrefix);
        String actPrefix = this.validateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
        if (actPrefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, actPrefix);
            }
            if (this.mOutputElemPool != null) {
                final SimpleOutputElement newCurr = this.mOutputElemPool;
                this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, actPrefix, localName, nsURI);
                --this.mPoolSize;
                this.mCurrElem = newCurr;
            }
            else {
                this.mCurrElem = this.mCurrElem.createChild(actPrefix, localName, nsURI);
            }
            this.doWriteStartTag(actPrefix, localName);
        }
        else {
            if (suggPrefix == null) {
                suggPrefix = "";
            }
            actPrefix = this.generateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, actPrefix);
            }
            if (this.mOutputElemPool != null) {
                final SimpleOutputElement newCurr = this.mOutputElemPool;
                this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, actPrefix, localName, nsURI);
                --this.mPoolSize;
                this.mCurrElem = newCurr;
            }
            else {
                this.mCurrElem = this.mCurrElem.createChild(actPrefix, localName, nsURI);
            }
            this.mCurrElem.setPrefix(actPrefix);
            this.doWriteStartTag(actPrefix, localName);
            if (actPrefix == null || actPrefix.length() == 0) {
                this.mCurrElem.setDefaultNsUri(nsURI);
                this.doWriteDefaultNs(nsURI);
            }
            else {
                this.mCurrElem.addPrefix(actPrefix, nsURI);
                this.doWriteNamespace(actPrefix, nsURI);
            }
        }
    }
    
    @Override
    public final void copyStartElement(final InputElementStack elemStack, final AttributeCollector ac) throws IOException, XMLStreamException {
        String prefix = elemStack.getPrefix();
        String uri = elemStack.getNsURI();
        this.writeStartElement(prefix, elemStack.getLocalName(), uri);
        final int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; ++i) {
                this.writeNamespace(elemStack.getLocalNsPrefix(i), elemStack.getLocalNsURI(i));
            }
        }
        final int attrCount = this.mCfgCopyDefaultAttrs ? ac.getCount() : ac.getSpecifiedCount();
        if (attrCount > 0) {
            for (int j = 0; j < attrCount; ++j) {
                uri = ac.getURI(j);
                prefix = ac.getPrefix(j);
                if (prefix != null) {
                    if (prefix.length() != 0) {
                        prefix = this.findOrCreateAttrPrefix(prefix, uri, this.mCurrElem);
                    }
                }
                if (prefix == null || prefix.length() == 0) {
                    this.mWriter.writeAttribute(ac.getLocalName(j), ac.getValue(j));
                }
                else {
                    this.mWriter.writeAttribute(prefix, ac.getLocalName(j), ac.getValue(j));
                }
            }
        }
    }
    
    @Override
    public String validateQNamePrefix(final QName name) throws XMLStreamException {
        final String uri = name.getNamespaceURI();
        String suggPrefix = name.getPrefix();
        String actPrefix = this.validateElemPrefix(suggPrefix, uri, this.mCurrElem);
        if (actPrefix == null) {
            if (suggPrefix == null) {
                suggPrefix = "";
            }
            actPrefix = this.generateElemPrefix(suggPrefix, uri, this.mCurrElem);
            if (actPrefix == null || actPrefix.length() == 0) {
                this.writeDefaultNamespace(uri);
            }
            else {
                this.writeNamespace(actPrefix, uri);
            }
        }
        return actPrefix;
    }
    
    protected final String findElemPrefix(final String nsURI, final SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI != null && nsURI.length() != 0) {
            return this.mCurrElem.getPrefix(nsURI);
        }
        final String currDefNsURI = elem.getDefaultNsUri();
        if (currDefNsURI != null && currDefNsURI.length() > 0) {
            return null;
        }
        return "";
    }
    
    protected final String generateElemPrefix(String suggPrefix, final String nsURI, final SimpleOutputElement elem) throws XMLStreamException {
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
    
    protected final String findOrCreateAttrPrefix(final String suggPrefix, final String nsURI, final SimpleOutputElement elem) throws XMLStreamException {
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
                this.doWriteNamespace(suggPrefix, nsURI);
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
        this.doWriteNamespace(prefix, nsURI);
        return prefix;
    }
    
    private final String validateElemPrefix(final String prefix, final String nsURI, final SimpleOutputElement elem) throws XMLStreamException {
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
}
