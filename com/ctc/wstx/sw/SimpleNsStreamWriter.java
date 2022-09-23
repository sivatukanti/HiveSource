// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import java.io.IOException;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.api.WriterConfig;

public class SimpleNsStreamWriter extends BaseNsStreamWriter
{
    public SimpleNsStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg) {
        super(xw, enc, cfg, false);
    }
    
    @Override
    public void writeAttribute(final String nsURI, final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        final String prefix = this.mCurrElem.getExplicitPrefix(nsURI);
        if (!this.mReturnNullForDefaultNamespace && prefix == null) {
            BaseStreamWriter.throwOutputError("Unbound namespace URI '" + nsURI + "'");
        }
        this.doWriteAttr(localName, nsURI, prefix, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String nsURI, final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, prefix, value);
    }
    
    @Override
    public void writeDefaultNamespace(final String nsURI) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        this.setDefaultNamespace(nsURI);
        this.doWriteDefaultNs(nsURI);
    }
    
    @Override
    public void writeNamespace(final String prefix, final String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            BaseStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        if (!this.mXml11 && nsURI.length() == 0) {
            BaseStreamWriter.throwOutputError(ErrorConsts.ERR_NS_EMPTY);
        }
        this.setPrefix(prefix, nsURI);
        this.doWriteNamespace(prefix, nsURI);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.mCurrElem.setDefaultNsUri(uri);
    }
    
    @Override
    public void doSetPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.mCurrElem.addPrefix(prefix, uri);
    }
    
    @Override
    public void writeStartElement(final StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        final Iterator<Namespace> it = (Iterator<Namespace>)elem.getNamespaces();
        while (it.hasNext()) {
            final Namespace ns = it.next();
            final String prefix = ns.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                this.setDefaultNamespace(ns.getNamespaceURI());
            }
            else {
                this.setPrefix(prefix, ns.getNamespaceURI());
            }
        }
        String nsURI = name.getNamespaceURI();
        if (nsURI == null) {
            this.writeStartElement(name.getLocalPart());
        }
        else {
            final String prefix = name.getPrefix();
            this.writeStartElement(prefix, name.getLocalPart(), nsURI);
        }
        final Iterator<Namespace> it2 = (Iterator<Namespace>)elem.getNamespaces();
        while (it2.hasNext()) {
            final Namespace ns2 = it2.next();
            final String prefix2 = ns2.getPrefix();
            if (prefix2 == null || prefix2.length() == 0) {
                this.writeDefaultNamespace(ns2.getNamespaceURI());
            }
            else {
                this.writeNamespace(prefix2, ns2.getNamespaceURI());
            }
        }
        final Iterator<Attribute> ait = (Iterator<Attribute>)elem.getAttributes();
        while (ait.hasNext()) {
            final Attribute attr = ait.next();
            name = attr.getName();
            nsURI = name.getNamespaceURI();
            if (nsURI != null && nsURI.length() > 0) {
                this.writeAttribute(name.getPrefix(), nsURI, name.getLocalPart(), attr.getValue());
            }
            else {
                this.writeAttribute(name.getLocalPart(), attr.getValue());
            }
        }
    }
    
    @Override
    protected void writeStartOrEmpty(final String localName, final String nsURI) throws XMLStreamException {
        final String prefix = this.mCurrElem.getPrefix(nsURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + nsURI + "'");
        }
        this.checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            final SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        }
        else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        this.doWriteStartTag(prefix, localName);
    }
    
    @Override
    protected void writeStartOrEmpty(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            final SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        }
        else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        this.doWriteStartTag(prefix, localName);
    }
    
    @Override
    public final void copyStartElement(final InputElementStack elemStack, final AttributeCollector attrCollector) throws IOException, XMLStreamException {
        final int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; ++i) {
                final String prefix = elemStack.getLocalNsPrefix(i);
                final String uri = elemStack.getLocalNsURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.setDefaultNamespace(uri);
                }
                else {
                    this.setPrefix(prefix, uri);
                }
            }
        }
        this.writeStartElement(elemStack.getPrefix(), elemStack.getLocalName(), elemStack.getNsURI());
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; ++i) {
                final String prefix = elemStack.getLocalNsPrefix(i);
                final String uri = elemStack.getLocalNsURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.writeDefaultNamespace(uri);
                }
                else {
                    this.writeNamespace(prefix, uri);
                }
            }
        }
        final int attrCount = this.mCfgCopyDefaultAttrs ? attrCollector.getCount() : attrCollector.getSpecifiedCount();
        if (attrCount > 0) {
            for (int j = 0; j < attrCount; ++j) {
                attrCollector.writeAttribute(j, this.mWriter, this.mValidator);
            }
        }
    }
    
    @Override
    public String validateQNamePrefix(final QName name) {
        return name.getPrefix();
    }
}
