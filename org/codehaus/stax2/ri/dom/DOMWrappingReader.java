// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.dom;

import java.util.ArrayList;
import java.util.Collections;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.w3c.dom.DocumentType;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.ri.SingletonIterator;
import org.codehaus.stax2.ri.EmptyIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import java.util.List;
import org.codehaus.stax2.ri.Stax2Util;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.namespace.NamespaceContext;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.XMLStreamReader2;

public abstract class DOMWrappingReader implements XMLStreamReader2, AttributeInfo, DTDInfo, LocationInfo, NamespaceContext, XMLStreamConstants
{
    protected static final int INT_SPACE = 32;
    private static final int MASK_GET_TEXT = 6768;
    private static final int MASK_GET_TEXT_XXX = 4208;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected static final int ERR_STATE_NOT_START_ELEM = 1;
    protected static final int ERR_STATE_NOT_ELEM = 2;
    protected static final int ERR_STATE_NOT_PI = 3;
    protected static final int ERR_STATE_NOT_TEXTUAL = 4;
    protected static final int ERR_STATE_NOT_TEXTUAL_XXX = 5;
    protected static final int ERR_STATE_NOT_TEXTUAL_OR_ELEM = 6;
    protected static final int ERR_STATE_NO_LOCALNAME = 7;
    protected final String _systemId;
    protected final Node _rootNode;
    protected final boolean _cfgNsAware;
    protected final boolean _coalescing;
    protected boolean _cfgInternNames;
    protected boolean _cfgInternNsURIs;
    protected int _currEvent;
    protected Node _currNode;
    protected int _depth;
    protected String _coalescedText;
    protected Stax2Util.TextBuffer _textBuffer;
    protected List _attrList;
    protected List _nsDeclList;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder;
    
    protected DOMWrappingReader(final DOMSource domSource, final boolean cfgNsAware, final boolean coalescing) throws XMLStreamException {
        this._cfgInternNames = false;
        this._cfgInternNsURIs = false;
        this._currEvent = 7;
        this._depth = 0;
        this._textBuffer = new Stax2Util.TextBuffer();
        this._attrList = null;
        this._nsDeclList = null;
        this._base64Decoder = null;
        final Node node = domSource.getNode();
        if (node == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamReader");
        }
        this._cfgNsAware = cfgNsAware;
        this._coalescing = coalescing;
        this._systemId = domSource.getSystemId();
        switch (node.getNodeType()) {
            case 1:
            case 9:
            case 11: {
                final Node node2 = node;
                this._currNode = node2;
                this._rootNode = node2;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamReader for a DOM node of type " + node.getClass());
            }
        }
    }
    
    protected void setInternNames(final boolean cfgInternNames) {
        this._cfgInternNames = cfgInternNames;
    }
    
    protected void setInternNsURIs(final boolean cfgInternNsURIs) {
        this._cfgInternNsURIs = cfgInternNsURIs;
    }
    
    protected abstract void throwStreamException(final String p0, final Location p1) throws XMLStreamException;
    
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    public String getEncoding() {
        return this.getCharacterEncodingScheme();
    }
    
    public String getVersion() {
        return null;
    }
    
    public boolean isStandalone() {
        return false;
    }
    
    public boolean standaloneSet() {
        return false;
    }
    
    public abstract Object getProperty(final String p0);
    
    public abstract boolean isPropertySupported(final String p0);
    
    public abstract boolean setProperty(final String p0, final Object p1);
    
    public int getAttributeCount() {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        return this._attrList.size();
    }
    
    public String getAttributeLocalName(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return this._internName(this._safeGetLocalName(this._attrList.get(n)));
    }
    
    public QName getAttributeName(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        final Attr attr = this._attrList.get(n);
        return this._constructQName(attr.getNamespaceURI(), this._safeGetLocalName(attr), attr.getPrefix());
    }
    
    public String getAttributeNamespace(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return this._internNsURI(this._attrList.get(n).getNamespaceURI());
    }
    
    public String getAttributePrefix(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return this._internName(this._attrList.get(n).getPrefix());
    }
    
    public String getAttributeType(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return "CDATA";
    }
    
    public String getAttributeValue(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return this._attrList.get(n).getValue();
    }
    
    public String getAttributeValue(String s, final String s2) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        final NamedNodeMap attributes = ((Element)this._currNode).getAttributes();
        if (s != null && s.length() == 0) {
            s = null;
        }
        final Attr attr = (Attr)attributes.getNamedItemNS(s, s2);
        return (attr == null) ? null : attr.getValue();
    }
    
    public String getElementText() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportParseProblem(1);
        }
        if (this._coalescing) {
            String str = null;
            while (true) {
                final int next = this.next();
                if (next == 2) {
                    break;
                }
                if (next == 5) {
                    continue;
                }
                if (next == 3) {
                    continue;
                }
                if ((1 << next & 0x1250) == 0x0) {
                    this.reportParseProblem(4);
                }
                if (str == null) {
                    str = this.getText();
                }
                else {
                    str += this.getText();
                }
            }
            return (str == null) ? "" : str;
        }
        this._textBuffer.reset();
        while (true) {
            final int next2 = this.next();
            if (next2 == 2) {
                break;
            }
            if (next2 == 5) {
                continue;
            }
            if (next2 == 3) {
                continue;
            }
            if ((1 << next2 & 0x1250) == 0x0) {
                this.reportParseProblem(4);
            }
            this._textBuffer.append(this.getText());
        }
        return this._textBuffer.get();
    }
    
    public int getEventType() {
        return this._currEvent;
    }
    
    public String getLocalName() {
        if (this._currEvent == 1 || this._currEvent == 2) {
            return this._internName(this._safeGetLocalName(this._currNode));
        }
        if (this._currEvent != 9) {
            this.reportWrongState(7);
        }
        return this._internName(this._currNode.getNodeName());
    }
    
    public final Location getLocation() {
        return this.getStartLocation();
    }
    
    public QName getName() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(1);
        }
        return this._constructQName(this._currNode.getNamespaceURI(), this._safeGetLocalName(this._currNode), this._currNode.getPrefix());
    }
    
    public NamespaceContext getNamespaceContext() {
        return this;
    }
    
    public int getNamespaceCount() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                return 0;
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        return this._nsDeclList.size() / 2;
    }
    
    public String getNamespacePrefix(final int n) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(n);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (n < 0 || n + n >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(n);
        }
        return this._nsDeclList.get(n + n);
    }
    
    public String getNamespaceURI() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internNsURI(this._currNode.getNamespaceURI());
    }
    
    public String getNamespaceURI(final int n) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(n);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (n < 0 || n + n >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(n);
        }
        return this._nsDeclList.get(n + n + 1);
    }
    
    public String getPIData() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._currNode.getNodeValue();
    }
    
    public String getPITarget() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._internName(this._currNode.getNodeName());
    }
    
    public String getPrefix() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internName(this._currNode.getPrefix());
    }
    
    public String getText() {
        if (this._coalescedText != null) {
            return this._coalescedText;
        }
        if ((1 << this._currEvent & 0x1A70) == 0x0) {
            this.reportWrongState(4);
        }
        return this._currNode.getNodeValue();
    }
    
    public char[] getTextCharacters() {
        return this.getText().toCharArray();
    }
    
    public int getTextCharacters(final int srcBegin, final char[] dst, final int dstBegin, int length) {
        if ((1 << this._currEvent & 0x1070) == 0x0) {
            this.reportWrongState(5);
        }
        final String text = this.getText();
        if (length > text.length()) {
            length = text.length();
        }
        text.getChars(srcBegin, srcBegin + length, dst, dstBegin);
        return length;
    }
    
    public int getTextLength() {
        if ((1 << this._currEvent & 0x1070) == 0x0) {
            this.reportWrongState(5);
        }
        return this.getText().length();
    }
    
    public int getTextStart() {
        if ((1 << this._currEvent & 0x1070) == 0x0) {
            this.reportWrongState(5);
        }
        return 0;
    }
    
    public boolean hasName() {
        return this._currEvent == 1 || this._currEvent == 2;
    }
    
    public boolean hasNext() {
        return this._currEvent != 8;
    }
    
    public boolean hasText() {
        return (1 << this._currEvent & 0x1A70) != 0x0;
    }
    
    public boolean isAttributeSpecified(final int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        final Attr attr = (Attr)((Element)this._currNode).getAttributes().item(n);
        if (attr == null) {
            this.handleIllegalAttrIndex(n);
            return false;
        }
        return attr.getSpecified();
    }
    
    public boolean isCharacters() {
        return this._currEvent == 4;
    }
    
    public boolean isEndElement() {
        return this._currEvent == 2;
    }
    
    public boolean isStartElement() {
        return this._currEvent == 1;
    }
    
    public boolean isWhiteSpace() {
        if (this._currEvent == 4 || this._currEvent == 12) {
            final String text = this.getText();
            for (int i = 0; i < text.length(); ++i) {
                if (text.charAt(i) > ' ') {
                    return false;
                }
            }
            return true;
        }
        return this._currEvent == 6;
    }
    
    public void require(final int n, final String str, final String s) throws XMLStreamException {
        int currEvent = this._currEvent;
        if (currEvent != n) {
            if (currEvent == 12) {
                currEvent = 4;
            }
            else if (currEvent == 6) {
                currEvent = 4;
            }
        }
        if (n != currEvent) {
            this.throwStreamException("Required type " + Stax2Util.eventTypeDesc(n) + ", current type " + Stax2Util.eventTypeDesc(currEvent));
        }
        if (s != null) {
            if (currEvent != 1 && currEvent != 2 && currEvent != 9) {
                this.throwStreamException("Required a non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + Stax2Util.eventTypeDesc(this._currEvent) + ")");
            }
            final String localName = this.getLocalName();
            if (localName != s && !localName.equals(s)) {
                this.throwStreamException("Required local name '" + s + "'; current local name '" + localName + "'.");
            }
        }
        if (str != null) {
            if (currEvent != 1 && currEvent != 2) {
                this.throwStreamException("Required non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + Stax2Util.eventTypeDesc(currEvent) + ")");
            }
            final String namespaceURI = this.getNamespaceURI();
            if (str.length() == 0) {
                if (namespaceURI != null && namespaceURI.length() > 0) {
                    this.throwStreamException("Required empty namespace, instead have '" + namespaceURI + "'.");
                }
            }
            else if (str != namespaceURI && !str.equals(namespaceURI)) {
                this.throwStreamException("Required namespace '" + str + "'; have '" + namespaceURI + "'.");
            }
        }
    }
    
    public int next() throws XMLStreamException {
        this._coalescedText = null;
        Label_0405: {
            switch (this._currEvent) {
                case 7: {
                    switch (this._currNode.getNodeType()) {
                        case 9:
                        case 11: {
                            this._currNode = this._currNode.getFirstChild();
                            if (this._currNode == null) {
                                return this._currEvent = 8;
                            }
                            break Label_0405;
                        }
                        case 1: {
                            return this._currEvent = 1;
                        }
                        default: {
                            throw new XMLStreamException("Internal error: unexpected DOM root node type " + this._currNode.getNodeType() + " for node '" + this._currNode + "'");
                        }
                    }
                    break;
                }
                case 8: {
                    throw new NoSuchElementException("Can not call next() after receiving END_DOCUMENT");
                }
                case 1: {
                    ++this._depth;
                    this._attrList = null;
                    final Node firstChild = this._currNode.getFirstChild();
                    if (firstChild == null) {
                        return this._currEvent = 2;
                    }
                    this._nsDeclList = null;
                    this._currNode = firstChild;
                    break Label_0405;
                }
                case 2: {
                    --this._depth;
                    this._attrList = null;
                    this._nsDeclList = null;
                    if (this._currNode == this._rootNode) {
                        return this._currEvent = 8;
                    }
                    break;
                }
            }
            final Node nextSibling = this._currNode.getNextSibling();
            if (nextSibling != null) {
                this._currNode = nextSibling;
            }
            else {
                this._currNode = this._currNode.getParentNode();
                final short nodeType = this._currNode.getNodeType();
                if (nodeType == 1) {
                    return this._currEvent = 2;
                }
                if (this._currNode != this._rootNode || (nodeType != 9 && nodeType != 11)) {
                    throw new XMLStreamException("Internal error: non-element parent node (" + nodeType + ") that is not the initial root node");
                }
                return this._currEvent = 8;
            }
        }
        switch (this._currNode.getNodeType()) {
            case 4: {
                if (this._coalescing) {
                    this.coalesceText(12);
                    break;
                }
                this._currEvent = 12;
                break;
            }
            case 8: {
                this._currEvent = 5;
                break;
            }
            case 10: {
                this._currEvent = 11;
                break;
            }
            case 1: {
                this._currEvent = 1;
                break;
            }
            case 5: {
                this._currEvent = 9;
                break;
            }
            case 7: {
                this._currEvent = 3;
                break;
            }
            case 3: {
                if (this._coalescing) {
                    this.coalesceText(4);
                    break;
                }
                this._currEvent = 4;
                break;
            }
            case 2:
            case 6:
            case 12: {
                throw new XMLStreamException("Internal error: unexpected DOM node type " + this._currNode.getNodeType() + " (attr/entity/notation?), for node '" + this._currNode + "'");
            }
            default: {
                throw new XMLStreamException("Internal error: unrecognized DOM node type " + this._currNode.getNodeType() + ", for node '" + this._currNode + "'");
            }
        }
        return this._currEvent;
    }
    
    public int nextTag() throws XMLStreamException {
        int next = 0;
    Label_0090:
        while (true) {
            next = this.next();
            switch (next) {
                case 3:
                case 5:
                case 6: {
                    continue;
                }
                case 4:
                case 12: {
                    if (this.isWhiteSpace()) {
                        continue;
                    }
                    this.throwStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                    break;
                }
                case 1:
                case 2: {
                    break Label_0090;
                }
            }
            this.throwStreamException("Received event " + Stax2Util.eventTypeDesc(next) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
        return next;
    }
    
    public void close() throws XMLStreamException {
    }
    
    public String getNamespaceURI(final String s) {
        Node node = this._currNode;
        final boolean b = s == null || s.length() == 0;
        while (node != null) {
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    final Node item = attributes.item(i);
                    final String prefix = item.getPrefix();
                    if (prefix == null || prefix.length() == 0) {
                        if (b && "xmlns".equals(item.getLocalName())) {
                            return item.getNodeValue();
                        }
                    }
                    else if (!b && "xmlns".equals(prefix) && s.equals(item.getLocalName())) {
                        return item.getNodeValue();
                    }
                }
            }
            node = node.getParentNode();
        }
        return null;
    }
    
    public String getPrefix(String s) {
        Node node = this._currNode;
        if (s == null) {
            s = "";
        }
        while (node != null) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                final Node item = attributes.item(i);
                final String prefix = item.getPrefix();
                if (prefix == null || prefix.length() == 0) {
                    if ("xmlns".equals(item.getLocalName()) && s.equals(item.getNodeValue())) {
                        return "";
                    }
                }
                else if ("xmlns".equals(prefix) && s.equals(item.getNodeValue())) {
                    return item.getLocalName();
                }
            }
            node = node.getParentNode();
        }
        return null;
    }
    
    public Iterator getPrefixes(final String s) {
        final String prefix = this.getPrefix(s);
        if (prefix == null) {
            return EmptyIterator.getInstance();
        }
        return new SingletonIterator(prefix);
    }
    
    public boolean getElementAsBoolean() throws XMLStreamException {
        final ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(booleanDecoder);
        return booleanDecoder.getValue();
    }
    
    public int getElementAsInt() throws XMLStreamException {
        final ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getElementAs(intDecoder);
        return intDecoder.getValue();
    }
    
    public long getElementAsLong() throws XMLStreamException {
        final ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getElementAs(longDecoder);
        return longDecoder.getValue();
    }
    
    public float getElementAsFloat() throws XMLStreamException {
        final ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getElementAs(floatDecoder);
        return floatDecoder.getValue();
    }
    
    public double getElementAsDouble() throws XMLStreamException {
        final ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(doubleDecoder);
        return doubleDecoder.getValue();
    }
    
    public BigInteger getElementAsInteger() throws XMLStreamException {
        final ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(integerDecoder);
        return integerDecoder.getValue();
    }
    
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        final ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(decimalDecoder);
        return decimalDecoder.getValue();
    }
    
    public QName getElementAsQName() throws XMLStreamException {
        final ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(qNameDecoder);
        return qNameDecoder.getValue();
    }
    
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }
    
    public byte[] getElementAsBinary(final Base64Variant base64Variant) throws XMLStreamException {
        final Stax2Util.ByteAggregator byteAggregator = this._base64Decoder().getByteAggregator();
        byte[] array = byteAggregator.startAggregation();
        int n = 0;
    Block_1:
        while (true) {
            n = 0;
            int i = array.length;
            do {
                final int elementAsBinary = this.readElementAsBinary(array, n, i, base64Variant);
                if (elementAsBinary < 1) {
                    break Block_1;
                }
                n += elementAsBinary;
                i -= elementAsBinary;
            } while (i > 0);
            array = byteAggregator.addFullBlock(array);
        }
        return byteAggregator.aggregateAll(array, n);
    }
    
    public void getElementAs(final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        final String trimSpaces = Stax2Util.trimSpaces(this.getElementText());
        try {
            if (trimSpaces == null) {
                typedValueDecoder.handleEmptyValue();
            }
            else {
                typedValueDecoder.decode(trimSpaces);
            }
        }
        catch (IllegalArgumentException ex) {
            throw this._constructTypeException(ex, trimSpaces);
        }
    }
    
    public int readElementAsIntArray(final int[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(array, n, n2));
    }
    
    public int readElementAsLongArray(final long[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(array, n, n2));
    }
    
    public int readElementAsFloatArray(final float[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(array, n, n2));
    }
    
    public int readElementAsDoubleArray(final double[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(array, n, n2));
    }
    
    public int readElementAsArray(final TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        if (this._currEvent == 1) {
            final Node firstChild = this._currNode.getFirstChild();
            if (firstChild == null) {
                this._currEvent = 2;
                return -1;
            }
            this._coalescedText = this.coalesceTypedText(firstChild);
            this._currEvent = 4;
            this._currNode = this._currNode.getLastChild();
        }
        else {
            if (this._currEvent != 4 && this._currEvent != 12) {
                if (this._currEvent == 2) {
                    return -1;
                }
                this.reportWrongState(6);
            }
            if (this._coalescedText == null) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT, not directly for a textual event");
            }
        }
        final String coalescedText = this._coalescedText;
        final int length = coalescedText.length();
        int i = 0;
        int n = 0;
        String substring = null;
        try {
        Label_0218:
            while (i < length) {
                while (coalescedText.charAt(i) <= ' ') {
                    if (++i >= length) {
                        break Label_0218;
                    }
                }
                final int beginIndex = i;
                ++i;
                while (i < length && coalescedText.charAt(i) > ' ') {
                    ++i;
                }
                ++n;
                substring = coalescedText.substring(beginIndex, i);
                ++i;
                if (typedArrayDecoder.decodeValue(substring)) {
                    break;
                }
            }
        }
        catch (IllegalArgumentException ex) {
            throw new TypedXMLStreamException(substring, ex.getMessage(), this.getLocation(), ex);
        }
        finally {
            this._coalescedText = ((length - i < 1) ? "" : coalescedText.substring(i));
        }
        if (n < 1) {
            this._currEvent = 2;
            this._currNode = this._currNode.getParentNode();
            return -1;
        }
        return n;
    }
    
    private String coalesceTypedText(final Node node) throws XMLStreamException {
        this._textBuffer.reset();
        this._attrList = null;
        for (Node nextSibling = node; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            switch (nextSibling.getNodeType()) {
                case 1: {
                    this.throwStreamException("Element content can not contain child START_ELEMENT when using Typed Access methods");
                }
                case 3:
                case 4: {
                    this._textBuffer.append(nextSibling.getNodeValue());
                    break;
                }
                case 7:
                case 8: {
                    break;
                }
                default: {
                    this.throwStreamException("Unexpected DOM node type (" + nextSibling.getNodeType() + ") when trying to decode Typed content");
                    break;
                }
            }
        }
        return this._textBuffer.get();
    }
    
    public int readElementAsBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsBinary(array, n, n2, Base64Variants.getDefaultVariant());
    }
    
    public int readElementAsBinary(final byte[] array, int i, int j, final Base64Variant base64Variant) throws XMLStreamException {
        if (array == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("Illegal offset (" + i + "), must be [0, " + array.length + "[");
        }
        if (j >= 1 && i + j <= array.length) {
            final StringBase64Decoder base64Decoder = this._base64Decoder();
            final int currEvent = this._currEvent;
            if ((1 << currEvent & 0x1052) == 0x0) {
                if (currEvent == 2) {
                    if (!base64Decoder.hasData()) {
                        return -1;
                    }
                }
                else {
                    this.reportWrongState(6);
                }
            }
            if (currEvent == 1) {
                while (true) {
                    final int next = this.next();
                    if (next == 2) {
                        return -1;
                    }
                    if (next == 5) {
                        continue;
                    }
                    if (next == 3) {
                        continue;
                    }
                    if ((1 << next & 0x1250) == 0x0) {
                        this.reportParseProblem(4);
                    }
                    base64Decoder.init(base64Variant, true, this.getText());
                    break;
                }
            }
            int n = 0;
            while (true) {
                int decode;
                try {
                    decode = base64Decoder.decode(array, i, j);
                }
                catch (IllegalArgumentException ex) {
                    throw this._constructTypeException(ex, "");
                }
                i += decode;
                n += decode;
                j -= decode;
                if (j < 1) {
                    break;
                }
                if (this._currEvent == 2) {
                    break;
                }
                int next2;
                while (true) {
                    next2 = this.next();
                    if (next2 != 5 && next2 != 3) {
                        if (next2 == 6) {
                            continue;
                        }
                        break;
                    }
                }
                if (next2 == 2) {
                    final int endOfContent = base64Decoder.endOfContent();
                    if (endOfContent < 0) {
                        throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                    }
                    if (endOfContent > 0) {
                        continue;
                    }
                    break;
                }
                else {
                    if ((1 << next2 & 0x1250) == 0x0) {
                        this.reportParseProblem(4);
                    }
                    base64Decoder.init(base64Variant, false, this.getText());
                }
            }
            return (n > 0) ? n : -1;
        }
        if (j == 0) {
            return 0;
        }
        throw new IllegalArgumentException("Illegal maxLength (" + j + "), has to be positive number, and offset+maxLength can not exceed" + array.length);
    }
    
    public int getAttributeIndex(final String s, final String s2) {
        return this.findAttributeIndex(s, s2);
    }
    
    public boolean getAttributeAsBoolean(final int n) throws XMLStreamException {
        final ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(n, booleanDecoder);
        return booleanDecoder.getValue();
    }
    
    public int getAttributeAsInt(final int n) throws XMLStreamException {
        final ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(n, intDecoder);
        return intDecoder.getValue();
    }
    
    public long getAttributeAsLong(final int n) throws XMLStreamException {
        final ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(n, longDecoder);
        return longDecoder.getValue();
    }
    
    public float getAttributeAsFloat(final int n) throws XMLStreamException {
        final ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(n, floatDecoder);
        return floatDecoder.getValue();
    }
    
    public double getAttributeAsDouble(final int n) throws XMLStreamException {
        final ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(n, doubleDecoder);
        return doubleDecoder.getValue();
    }
    
    public BigInteger getAttributeAsInteger(final int n) throws XMLStreamException {
        final ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(n, integerDecoder);
        return integerDecoder.getValue();
    }
    
    public BigDecimal getAttributeAsDecimal(final int n) throws XMLStreamException {
        final ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(n, decimalDecoder);
        return decimalDecoder.getValue();
    }
    
    public QName getAttributeAsQName(final int n) throws XMLStreamException {
        final ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(n, qNameDecoder);
        return qNameDecoder.getValue();
    }
    
    public final void getAttributeAs(final int n, final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        final String trimSpaces = Stax2Util.trimSpaces(this.getAttributeValue(n));
        try {
            if (trimSpaces == null) {
                typedValueDecoder.handleEmptyValue();
            }
            else {
                typedValueDecoder.decode(trimSpaces);
            }
        }
        catch (IllegalArgumentException ex) {
            throw this._constructTypeException(ex, trimSpaces);
        }
    }
    
    public int[] getAttributeAsIntArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(intArrayDecoder, this.getAttributeValue(n));
        return intArrayDecoder.getValues();
    }
    
    public long[] getAttributeAsLongArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(longArrayDecoder, this.getAttributeValue(n));
        return longArrayDecoder.getValues();
    }
    
    public float[] getAttributeAsFloatArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(floatArrayDecoder, this.getAttributeValue(n));
        return floatArrayDecoder.getValues();
    }
    
    public double[] getAttributeAsDoubleArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(doubleArrayDecoder, this.getAttributeValue(n));
        return doubleArrayDecoder.getValues();
    }
    
    public int getAttributeAsArray(final int n, final TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this._getAttributeAsArray(typedArrayDecoder, this.getAttributeValue(n));
    }
    
    protected int _getAttributeAsArray(final TypedArrayDecoder typedArrayDecoder, final String s) throws XMLStreamException {
        int i = 0;
        final int length = s.length();
        String substring = null;
        int n = 0;
        try {
        Label_0115:
            while (i < length) {
                while (s.charAt(i) <= ' ') {
                    if (++i >= length) {
                        break Label_0115;
                    }
                }
                final int beginIndex = i;
                ++i;
                while (i < length && s.charAt(i) > ' ') {
                    ++i;
                }
                final int endIndex = i;
                ++i;
                substring = s.substring(beginIndex, endIndex);
                ++n;
                if (typedArrayDecoder.decodeValue(substring) && !this.checkExpand(typedArrayDecoder)) {
                    break;
                }
            }
        }
        catch (IllegalArgumentException ex) {
            throw new TypedXMLStreamException(substring, ex.getMessage(), this.getLocation(), ex);
        }
        return n;
    }
    
    private final boolean checkExpand(final TypedArrayDecoder typedArrayDecoder) {
        if (typedArrayDecoder instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)typedArrayDecoder).expand();
            return true;
        }
        return false;
    }
    
    public byte[] getAttributeAsBinary(final int n) throws XMLStreamException {
        return this.getAttributeAsBinary(n, Base64Variants.getDefaultVariant());
    }
    
    public byte[] getAttributeAsBinary(final int n, final Base64Variant base64Variant) throws XMLStreamException {
        final String attributeValue = this.getAttributeValue(n);
        final StringBase64Decoder base64Decoder = this._base64Decoder();
        base64Decoder.init(base64Variant, true, attributeValue);
        try {
            return base64Decoder.decodeCompletely();
        }
        catch (IllegalArgumentException ex) {
            throw this._constructTypeException(ex, attributeValue);
        }
    }
    
    public Object getFeature(final String str) {
        throw new IllegalArgumentException("Unrecognized feature \"" + str + "\"");
    }
    
    public void setFeature(final String str, final Object o) {
        throw new IllegalArgumentException("Unrecognized feature \"" + str + "\"");
    }
    
    public void skipElement() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        int n = 1;
        while (true) {
            final int next = this.next();
            if (next == 1) {
                ++n;
            }
            else {
                if (next == 2 && --n == 0) {
                    break;
                }
                continue;
            }
        }
    }
    
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        return this;
    }
    
    public int findAttributeIndex(String s, final String s2) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        final NamedNodeMap attributes = ((Element)this._currNode).getAttributes();
        if (s != null && s.length() == 0) {
            s = null;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node item = attributes.item(i);
            if (s2.equals(this._safeGetLocalName(item))) {
                final String namespaceURI = item.getNamespaceURI();
                final boolean b = namespaceURI == null || namespaceURI.length() == 0;
                if (s == null) {
                    if (b) {
                        return i;
                    }
                }
                else if (!b && s.equals(namespaceURI)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getIdAttributeIndex() {
        return -1;
    }
    
    public int getNotationAttributeIndex() {
        return -1;
    }
    
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this._currEvent != 11) {
            return null;
        }
        return this;
    }
    
    public final LocationInfo getLocationInfo() {
        return this;
    }
    
    public int getText(final Writer writer, final boolean b) throws IOException, XMLStreamException {
        final String text = this.getText();
        writer.write(text);
        return text.length();
    }
    
    public int getDepth() {
        return this._depth;
    }
    
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }
    
    public NamespaceContext getNonTransientNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }
    
    public String getPrefixedName() {
        switch (this._currEvent) {
            case 1:
            case 2: {
                final String prefix = this._currNode.getPrefix();
                final String safeGetLocalName = this._safeGetLocalName(this._currNode);
                if (prefix == null) {
                    return this._internName(safeGetLocalName);
                }
                final StringBuffer sb = new StringBuffer(safeGetLocalName.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(safeGetLocalName);
                return this._internName(sb.toString());
            }
            case 9: {
                return this.getLocalName();
            }
            case 3: {
                return this.getPITarget();
            }
            case 11: {
                return this.getDTDRootName();
            }
            default: {
                throw new IllegalStateException("Current state (" + Stax2Util.eventTypeDesc(this._currEvent) + ") not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
            }
        }
    }
    
    public void closeCompletely() throws XMLStreamException {
    }
    
    public Object getProcessedDTD() {
        return null;
    }
    
    public String getDTDRootName() {
        if (this._currEvent == 11) {
            return this._internName(((DocumentType)this._currNode).getName());
        }
        return null;
    }
    
    public String getDTDPublicId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getPublicId();
        }
        return null;
    }
    
    public String getDTDSystemId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getSystemId();
        }
        return null;
    }
    
    public String getDTDInternalSubset() {
        return null;
    }
    
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }
    
    public long getStartingByteOffset() {
        return -1L;
    }
    
    public long getStartingCharOffset() {
        return 0L;
    }
    
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }
    
    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }
    
    public XMLStreamLocation2 getStartLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }
    
    public XMLStreamLocation2 getCurrentLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }
    
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidator xmlValidator) throws XMLStreamException {
        return null;
    }
    
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler validationProblemHandler) {
        return null;
    }
    
    protected void coalesceText(final int n) {
        this._textBuffer.reset();
        this._textBuffer.append(this._currNode.getNodeValue());
        Node nextSibling;
        while ((nextSibling = this._currNode.getNextSibling()) != null) {
            final short nodeType = nextSibling.getNodeType();
            if (nodeType != 3 && nodeType != 4) {
                break;
            }
            this._currNode = nextSibling;
            this._textBuffer.append(this._currNode.getNodeValue());
        }
        this._coalescedText = this._textBuffer.get();
        this._currEvent = 4;
    }
    
    private QName _constructQName(final String s, final String s2, final String s3) {
        return new QName(this._internNsURI(s), this._internName(s2), this._internName(s3));
    }
    
    private void _calcNsAndAttrLists(final boolean b) {
        final NamedNodeMap attributes = this._currNode.getAttributes();
        final int length = attributes.getLength();
        if (length == 0) {
            final List empty_LIST = Collections.EMPTY_LIST;
            this._nsDeclList = empty_LIST;
            this._attrList = empty_LIST;
            return;
        }
        if (!this._cfgNsAware) {
            this._attrList = new ArrayList(length);
            for (int i = 0; i < length; ++i) {
                this._attrList.add(attributes.item(i));
            }
            this._nsDeclList = Collections.EMPTY_LIST;
            return;
        }
        ArrayList<Node> list = null;
        ArrayList<String> list2 = null;
        for (int j = 0; j < length; ++j) {
            final Node item = attributes.item(j);
            final String prefix = item.getPrefix();
            String localName;
            if (prefix == null || prefix.length() == 0) {
                if (!"xmlns".equals(item.getLocalName())) {
                    if (b) {
                        if (list == null) {
                            list = new ArrayList<Node>(length - j);
                        }
                        list.add(item);
                    }
                    continue;
                }
                else {
                    localName = null;
                }
            }
            else if (!"xmlns".equals(prefix)) {
                if (b) {
                    if (list == null) {
                        list = new ArrayList<Node>(length - j);
                    }
                    list.add(item);
                }
                continue;
            }
            else {
                localName = item.getLocalName();
            }
            if (list2 == null) {
                list2 = new ArrayList<String>((length - j) * 2);
            }
            list2.add(this._internName(localName));
            list2.add(this._internNsURI(item.getNodeValue()));
        }
        this._attrList = ((list == null) ? Collections.EMPTY_LIST : list);
        this._nsDeclList = ((list2 == null) ? Collections.EMPTY_LIST : list2);
    }
    
    private void handleIllegalAttrIndex(final int i) {
        final Element element = (Element)this._currNode;
        final int length = element.getAttributes().getLength();
        throw new IllegalArgumentException("Illegal attribute index " + i + "; element <" + element.getNodeName() + "> has " + ((length == 0) ? "no" : String.valueOf(length)) + " attributes");
    }
    
    private void handleIllegalNsIndex(final int i) {
        throw new IllegalArgumentException("Illegal namespace declaration index " + i + " (has " + this.getNamespaceCount() + " ns declarations)");
    }
    
    private String _safeGetLocalName(final Node node) {
        String s = node.getLocalName();
        if (s == null) {
            s = node.getNodeName();
        }
        return s;
    }
    
    protected void reportWrongState(final int n) {
        throw new IllegalStateException(this.findErrorDesc(n, this._currEvent));
    }
    
    protected void reportParseProblem(final int n) throws XMLStreamException {
        this.throwStreamException(this.findErrorDesc(n, this._currEvent));
    }
    
    protected void throwStreamException(final String s) throws XMLStreamException {
        this.throwStreamException(s, this.getErrorLocation());
    }
    
    protected Location getErrorLocation() {
        Location location = this.getCurrentLocation();
        if (location == null) {
            location = this.getLocation();
        }
        return location;
    }
    
    protected TypedXMLStreamException _constructTypeException(final IllegalArgumentException ex, final String s) {
        String message = ex.getMessage();
        if (message == null) {
            message = "";
        }
        final XMLStreamLocation2 startLocation = this.getStartLocation();
        if (startLocation == null) {
            return new TypedXMLStreamException(s, message, ex);
        }
        return new TypedXMLStreamException(s, message, startLocation);
    }
    
    protected TypedXMLStreamException _constructTypeException(final String s, final String s2) {
        final XMLStreamLocation2 startLocation = this.getStartLocation();
        if (startLocation == null) {
            return new TypedXMLStreamException(s2, s);
        }
        return new TypedXMLStreamException(s2, s, startLocation);
    }
    
    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }
    
    protected StringBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new StringBase64Decoder();
        }
        return this._base64Decoder;
    }
    
    protected String findErrorDesc(final int i, final int n) {
        final String eventTypeDesc = Stax2Util.eventTypeDesc(n);
        switch (i) {
            case 1: {
                return "Current event " + eventTypeDesc + ", needs to be START_ELEMENT";
            }
            case 2: {
                return "Current event " + eventTypeDesc + ", needs to be START_ELEMENT or END_ELEMENT";
            }
            case 7: {
                return "Current event (" + eventTypeDesc + ") has no local name";
            }
            case 3: {
                return "Current event (" + eventTypeDesc + ") needs to be PROCESSING_INSTRUCTION";
            }
            case 4: {
                return "Current event (" + eventTypeDesc + ") not a textual event";
            }
            case 6: {
                return "Current event (" + eventTypeDesc + " not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA";
            }
            case 5: {
                return "Current event " + eventTypeDesc + ", needs to be one of CHARACTERS, CDATA, SPACE or COMMENT";
            }
            default: {
                return "Internal error (unrecognized error type: " + i + ")";
            }
        }
    }
    
    protected String _internName(final String s) {
        if (s == null) {
            return "";
        }
        return this._cfgInternNames ? s.intern() : s;
    }
    
    protected String _internNsURI(final String s) {
        if (s == null) {
            return "";
        }
        return this._cfgInternNsURIs ? s.intern() : s;
    }
}
