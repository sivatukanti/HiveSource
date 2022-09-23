// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.EmptyNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import com.ctc.wstx.api.WriterConfig;
import java.util.TreeSet;
import com.ctc.wstx.util.StringVector;

public class NonNsStreamWriter extends TypedStreamWriter
{
    final StringVector mElements;
    TreeSet<String> mAttrNames;
    
    public NonNsStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg) {
        super(xw, enc, cfg);
        this.mElements = new StringVector(32);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }
    
    @Override
    public String getPrefix(final String uri) {
        return null;
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        BaseStreamWriter.reportIllegalArg("Can not set default namespace for non-namespace writer.");
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext context) {
        BaseStreamWriter.reportIllegalArg("Can not set NamespaceContext for non-namespace writer.");
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        BaseStreamWriter.reportIllegalArg("Can not set namespace prefix for non-namespace writer.");
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet<String>();
            }
            if (!this.mAttrNames.add(localName)) {
                BaseStreamWriter.reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, "", "", value);
        }
        try {
            this.mWriter.writeAttribute(localName, value);
        }
        catch (IOException ioe) {
            BaseStreamWriter.throwFromIOE(ioe);
        }
    }
    
    @Override
    public void writeAttribute(final String nsURI, final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(localName, value);
    }
    
    @Override
    public void writeAttribute(final String prefix, final String nsURI, final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(localName, value);
    }
    
    @Override
    public void writeDefaultNamespace(final String nsURI) throws XMLStreamException {
        BaseStreamWriter.reportIllegalMethod("Can not call writeDefaultNamespace namespaces with non-namespace writer.");
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.mEmptyElement = true;
    }
    
    @Override
    public void writeEmptyElement(final String nsURI, final String localName) throws XMLStreamException {
        this.writeEmptyElement(localName);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.writeEmptyElement(localName);
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, this.mCfgAutomaticEmptyElems);
    }
    
    @Override
    public void writeNamespace(final String prefix, final String nsURI) throws XMLStreamException {
        BaseStreamWriter.reportIllegalMethod("Can not set write namespaces with non-namespace writer.");
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.mEmptyElement = false;
    }
    
    @Override
    public void writeStartElement(final String nsURI, final String localName) throws XMLStreamException {
        this.writeStartElement(localName);
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String nsURI) throws XMLStreamException {
        this.writeStartElement(localName);
    }
    
    @Override
    public void writeFullEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, false);
    }
    
    @Override
    public QName getCurrentElementName() {
        if (this.mElements.isEmpty()) {
            return null;
        }
        return new QName(this.mElements.getLastString());
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        return null;
    }
    
    @Override
    public void writeStartElement(final StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        this.writeStartElement(name.getLocalPart());
        final Iterator<Attribute> it = (Iterator<Attribute>)elem.getAttributes();
        while (it.hasNext()) {
            final Attribute attr = it.next();
            name = attr.getName();
            this.writeAttribute(name.getLocalPart(), attr.getValue());
        }
    }
    
    @Override
    public void writeEndElement(final QName name) throws XMLStreamException {
        this.doWriteEndTag(this.mCheckStructure ? name.getLocalPart() : null, this.mCfgAutomaticEmptyElems);
    }
    
    @Override
    protected void writeTypedAttribute(final String prefix, final String nsURI, final String localName, final AsciiValueEncoder enc) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet<String>();
            }
            if (!this.mAttrNames.add(localName)) {
                BaseStreamWriter.reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        try {
            if (this.mValidator == null) {
                this.mWriter.writeTypedAttribute(localName, enc);
            }
            else {
                this.mWriter.writeTypedAttribute(null, localName, null, enc, this.mValidator, this.getCopyBuffer());
            }
        }
        catch (IOException ioe) {
            BaseStreamWriter.throwFromIOE(ioe);
        }
    }
    
    @Override
    protected void closeStartElement(final boolean emptyElem) throws XMLStreamException {
        this.mStartElementOpen = false;
        if (this.mAttrNames != null) {
            this.mAttrNames.clear();
        }
        try {
            if (emptyElem) {
                this.mWriter.writeStartTagEmptyEnd();
            }
            else {
                this.mWriter.writeStartTagEnd();
            }
        }
        catch (IOException ioe) {
            BaseStreamWriter.throwFromIOE(ioe);
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementAndAttributes();
        }
        if (emptyElem) {
            final String localName = this.mElements.removeLast();
            if (this.mElements.isEmpty()) {
                this.mState = 3;
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
            }
        }
    }
    
    @Override
    public void copyStartElement(final InputElementStack elemStack, final AttributeCollector attrCollector) throws IOException, XMLStreamException {
        String ln = elemStack.getLocalName();
        final boolean nsAware = elemStack.isNamespaceAware();
        if (nsAware) {
            final String prefix = elemStack.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                ln = prefix + ":" + ln;
            }
        }
        this.writeStartElement(ln);
        if (nsAware) {
            final int nsCount = elemStack.getCurrentNsCount();
            if (nsCount > 0) {
                for (int i = 0; i < nsCount; ++i) {
                    String prefix2 = elemStack.getLocalNsPrefix(i);
                    if (prefix2 == null || prefix2.length() == 0) {
                        prefix2 = "xml";
                    }
                    else {
                        prefix2 = "xmlns:" + prefix2;
                    }
                    this.writeAttribute(prefix2, elemStack.getLocalNsURI(i));
                }
            }
        }
        final int attrCount = this.mCfgCopyDefaultAttrs ? attrCollector.getCount() : attrCollector.getSpecifiedCount();
        if (attrCount > 0) {
            for (int i = 0; i < attrCount; ++i) {
                attrCollector.writeAttribute(i, this.mWriter, this.mValidator);
            }
        }
    }
    
    @Override
    protected String getTopElementDesc() {
        return this.mElements.isEmpty() ? "#root" : this.mElements.getLastString();
    }
    
    @Override
    public String validateQNamePrefix(final QName name) {
        return name.getPrefix();
    }
    
    private void doWriteStartElement(final String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        else if (this.mState == 1) {
            this.verifyRootElement(localName, null);
        }
        else if (this.mState == 3) {
            if (this.mCheckStructure) {
                BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_SECOND_ROOT, localName);
            }
            this.mState = 2;
        }
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mStartElementOpen = true;
        this.mElements.addString(localName);
        try {
            this.mWriter.writeStartTagStart(localName);
        }
        catch (IOException ioe) {
            BaseStreamWriter.throwFromIOE(ioe);
        }
    }
    
    private void doWriteEndTag(final String expName, final boolean allowEmpty) throws XMLStreamException {
        if (this.mStartElementOpen && this.mEmptyElement) {
            this.mEmptyElement = false;
            this.closeStartElement(true);
        }
        if (this.mState != 2) {
            BaseStreamWriter.reportNwfStructure("No open start element, when trying to write end element");
        }
        final String localName = this.mElements.removeLast();
        if (this.mCheckStructure && expName != null && !localName.equals(expName)) {
            BaseStreamWriter.reportNwfStructure("Mismatching close element name, '" + localName + "'; expected '" + expName + "'.");
        }
        if (this.mStartElementOpen) {
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            this.mStartElementOpen = false;
            if (this.mAttrNames != null) {
                this.mAttrNames.clear();
            }
            try {
                if (allowEmpty) {
                    this.mWriter.writeStartTagEmptyEnd();
                    if (this.mElements.isEmpty()) {
                        this.mState = 3;
                    }
                    if (this.mValidator != null) {
                        this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
                    }
                    return;
                }
                this.mWriter.writeStartTagEnd();
            }
            catch (IOException ioe) {
                BaseStreamWriter.throwFromIOE(ioe);
            }
        }
        try {
            this.mWriter.writeEndTag(localName);
        }
        catch (IOException ioe) {
            BaseStreamWriter.throwFromIOE(ioe);
        }
        if (this.mElements.isEmpty()) {
            this.mState = 3;
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
        }
    }
}
