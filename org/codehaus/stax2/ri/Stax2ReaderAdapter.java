// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.Location;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.DTDValidationSchema;
import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.typed.TypedValueDecoder;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.XMLStreamReader2;
import javax.xml.stream.util.StreamReaderDelegate;

public class Stax2ReaderAdapter extends StreamReaderDelegate implements XMLStreamReader2, AttributeInfo, DTDInfo, LocationInfo
{
    static final int INT_SPACE = 32;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder;
    protected int _depth;
    protected String _typedContent;
    
    protected Stax2ReaderAdapter(final XMLStreamReader reader) {
        super(reader);
        this._base64Decoder = null;
        this._depth = 0;
    }
    
    public static XMLStreamReader2 wrapIfNecessary(final XMLStreamReader xmlStreamReader) {
        if (xmlStreamReader instanceof XMLStreamReader2) {
            return (XMLStreamReader2)xmlStreamReader;
        }
        return new Stax2ReaderAdapter(xmlStreamReader);
    }
    
    @Override
    public int next() throws XMLStreamException {
        if (this._typedContent != null) {
            this._typedContent = null;
            return 2;
        }
        final int next = super.next();
        if (next == 1) {
            ++this._depth;
        }
        else if (next == 2) {
            --this._depth;
        }
        return next;
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        final boolean b = this.getEventType() == 1;
        final String elementText = super.getElementText();
        if (b) {
            --this._depth;
        }
        return elementText;
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
        if (this._typedContent == null) {
            final int eventType = this.getEventType();
            if (eventType == 2) {
                return -1;
            }
            if (eventType != 1) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT");
            }
            this._typedContent = this.getElementText();
        }
        final String typedContent = this._typedContent;
        final int length = typedContent.length();
        int i = 0;
        int n = 0;
        String substring = null;
        try {
        Label_0151:
            while (i < length) {
                while (typedContent.charAt(i) <= ' ') {
                    if (++i >= length) {
                        break Label_0151;
                    }
                }
                final int beginIndex = i;
                ++i;
                while (i < length && typedContent.charAt(i) > ' ') {
                    ++i;
                }
                ++n;
                substring = typedContent.substring(beginIndex, i);
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
            this._typedContent = ((length - i < 1) ? null : typedContent.substring(i));
        }
        return (n < 1) ? -1 : n;
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
            final int eventType = this.getEventType();
            if ((1 << eventType & 0x1052) == 0x0) {
                if (eventType == 2) {
                    if (!base64Decoder.hasData()) {
                        return -1;
                    }
                }
                else {
                    this.throwNotStartElemOrTextual(eventType);
                }
            }
            if (eventType == 1) {
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
                        this.throwNotStartElemOrTextual(next);
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
                if (this.getEventType() == 2) {
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
                        this.throwNotStartElemOrTextual(next2);
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
    
    public void getAttributeAs(final int index, final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        final String trimSpaces = Stax2Util.trimSpaces(this.getAttributeValue(index));
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
    
    public int[] getAttributeAsIntArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(intArrayDecoder, this.getAttributeValue(index));
        return intArrayDecoder.getValues();
    }
    
    public long[] getAttributeAsLongArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(longArrayDecoder, this.getAttributeValue(index));
        return longArrayDecoder.getValues();
    }
    
    public float[] getAttributeAsFloatArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(floatArrayDecoder, this.getAttributeValue(index));
        return floatArrayDecoder.getValues();
    }
    
    public double[] getAttributeAsDoubleArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(doubleArrayDecoder, this.getAttributeValue(index));
        return doubleArrayDecoder.getValues();
    }
    
    public int getAttributeAsArray(final int index, final TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this._getAttributeAsArray(typedArrayDecoder, this.getAttributeValue(index));
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
    
    public byte[] getAttributeAsBinary(final int index, final Base64Variant base64Variant) throws XMLStreamException {
        final String attributeValue = this.getAttributeValue(index);
        final StringBase64Decoder base64Decoder = this._base64Decoder();
        base64Decoder.init(base64Variant, true, attributeValue);
        try {
            return base64Decoder.decodeCompletely();
        }
        catch (IllegalArgumentException ex) {
            throw new TypedXMLStreamException(attributeValue, ex.getMessage(), this.getLocation(), ex);
        }
    }
    
    public Object getFeature(final String s) {
        return null;
    }
    
    public void setFeature(final String s, final Object o) {
    }
    
    public boolean isPropertySupported(final String s) {
        return false;
    }
    
    public boolean setProperty(final String s, final Object o) {
        return false;
    }
    
    public void skipElement() throws XMLStreamException {
        if (this.getEventType() != 1) {
            this.throwNotStartElem(this.getEventType());
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
        if (this.getEventType() != 1) {
            this.throwNotStartElem(this.getEventType());
        }
        return this;
    }
    
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this.getEventType() != 11) {
            return null;
        }
        return this;
    }
    
    public final LocationInfo getLocationInfo() {
        return this;
    }
    
    public int getText(final Writer writer, final boolean b) throws IOException, XMLStreamException {
        final char[] textCharacters = this.getTextCharacters();
        final int textStart = this.getTextStart();
        final int textLength = this.getTextLength();
        if (textLength > 0) {
            writer.write(textCharacters, textStart, textLength);
        }
        return textLength;
    }
    
    public int getDepth() {
        if (this.getEventType() == 2) {
            return this._depth + 1;
        }
        return this._depth;
    }
    
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }
    
    public NamespaceContext getNonTransientNamespaceContext() {
        return null;
    }
    
    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1:
            case 2: {
                final String prefix = this.getPrefix();
                final String localName = this.getLocalName();
                if (prefix == null || prefix.length() == 0) {
                    return localName;
                }
                final StringBuffer sb = new StringBuffer(localName.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(localName);
                return sb.toString();
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
                throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
            }
        }
    }
    
    public void closeCompletely() throws XMLStreamException {
        this.close();
    }
    
    public int findAttributeIndex(String anObject, final String anObject2) {
        if ("".equals(anObject)) {
            anObject = null;
        }
        for (int i = 0; i < this.getAttributeCount(); ++i) {
            if (this.getAttributeLocalName(i).equals(anObject2)) {
                final String attributeNamespace = this.getAttributeNamespace(i);
                if (anObject == null) {
                    if (attributeNamespace == null || attributeNamespace.length() == 0) {
                        return i;
                    }
                }
                else if (anObject.equals(attributeNamespace)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int getIdAttributeIndex() {
        for (int i = 0; i < this.getAttributeCount(); ++i) {
            if ("ID".equals(this.getAttributeType(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public int getNotationAttributeIndex() {
        for (int i = 0; i < this.getAttributeCount(); ++i) {
            if ("NOTATION".equals(this.getAttributeType(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public Object getProcessedDTD() {
        return null;
    }
    
    public String getDTDRootName() {
        return null;
    }
    
    public String getDTDPublicId() {
        return null;
    }
    
    public String getDTDSystemId() {
        return null;
    }
    
    public String getDTDInternalSubset() {
        if (this.getEventType() == 11) {
            return this.getText();
        }
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
        return this.getCurrentLocation();
    }
    
    public XMLStreamLocation2 getCurrentLocation() {
        return new Stax2LocationAdapter(this.getLocation());
    }
    
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return this.getCurrentLocation();
    }
    
    public XMLValidator validateAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidationSchema xmlValidationSchema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }
    
    public XMLValidator stopValidatingAgainst(final XMLValidator xmlValidator) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }
    
    public ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler validationProblemHandler) {
        return null;
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
    
    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }
    
    protected void throwNotStartElem(final int n) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(n) + ") not START_ELEMENT");
    }
    
    protected void throwNotStartElemOrTextual(final int n) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(n) + ") not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA");
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
        return new TypedXMLStreamException(s, message, startLocation, ex);
    }
    
    protected TypedXMLStreamException _constructTypeException(final String s, final String s2) {
        final XMLStreamLocation2 startLocation = this.getStartLocation();
        if (startLocation == null) {
            return new TypedXMLStreamException(s2, s);
        }
        return new TypedXMLStreamException(s2, s, startLocation);
    }
}
