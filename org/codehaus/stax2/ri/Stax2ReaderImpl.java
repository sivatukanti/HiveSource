// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri;

import javax.xml.stream.Location;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.validation.DTDValidationSchema;
import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.XMLStreamReader2;

public abstract class Stax2ReaderImpl implements XMLStreamReader2, AttributeInfo, DTDInfo, LocationInfo
{
    protected ValueDecoderFactory _decoderFactory;
    
    protected Stax2ReaderImpl() {
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
            this.throwNotStartElem();
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
            this.throwNotStartElem();
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
    
    public abstract int getDepth();
    
    public abstract boolean isEmptyElement() throws XMLStreamException;
    
    public abstract NamespaceContext getNonTransientNamespaceContext();
    
    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1:
            case 2: {
                final String prefix = this.getPrefix();
                final String localName = this.getLocalName();
                if (prefix == null) {
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
    
    public int findAttributeIndex(final String s, final String s2) {
        return -1;
    }
    
    public int getIdAttributeIndex() {
        return -1;
    }
    
    public int getNotationAttributeIndex() {
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
    
    public abstract XMLStreamLocation2 getStartLocation();
    
    public abstract XMLStreamLocation2 getCurrentLocation();
    
    public abstract XMLStreamLocation2 getEndLocation() throws XMLStreamException;
    
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
    
    public abstract ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler p0);
    
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
    
    public abstract byte[] getElementAsBinary(final Base64Variant p0) throws XMLStreamException;
    
    public void getElementAs(final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        final String elementText = this.getElementText();
        try {
            typedValueDecoder.decode(elementText);
        }
        catch (IllegalArgumentException ex) {
            throw this._constructTypeException(ex, elementText);
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
    
    public abstract int readElementAsArray(final TypedArrayDecoder p0) throws XMLStreamException;
    
    public int readElementAsBinary(final byte[] array, final int n, final int n2) throws XMLStreamException {
        return this.readElementAsBinary(Base64Variants.getDefaultVariant(), array, n, n2);
    }
    
    public abstract int readElementAsBinary(final Base64Variant p0, final byte[] p1, final int p2, final int p3) throws XMLStreamException;
    
    public abstract int getAttributeIndex(final String p0, final String p1);
    
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
    
    public void getAttributeAs(final int n, final TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        final String attributeValue = this.getAttributeValue(n);
        try {
            typedValueDecoder.decode(attributeValue);
        }
        catch (IllegalArgumentException ex) {
            throw this._constructTypeException(ex, attributeValue);
        }
    }
    
    public int[] getAttributeAsIntArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this.getAttributeAsArray(n, intArrayDecoder);
        return intArrayDecoder.getValues();
    }
    
    public long[] getAttributeAsLongArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this.getAttributeAsArray(n, longArrayDecoder);
        return longArrayDecoder.getValues();
    }
    
    public float[] getAttributeAsFloatArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this.getAttributeAsArray(n, floatArrayDecoder);
        return floatArrayDecoder.getValues();
    }
    
    public double[] getAttributeAsDoubleArray(final int n) throws XMLStreamException {
        final ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this.getAttributeAsArray(n, doubleArrayDecoder);
        return doubleArrayDecoder.getValues();
    }
    
    public abstract int getAttributeAsArray(final int p0, final TypedArrayDecoder p1) throws XMLStreamException;
    
    public byte[] getAttributeAsBinary(final int n) throws XMLStreamException {
        return this.getAttributeAsBinary(Base64Variants.getDefaultVariant(), n);
    }
    
    public abstract byte[] getAttributeAsBinary(final Base64Variant p0, final int p1) throws XMLStreamException;
    
    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }
    
    protected TypedXMLStreamException _constructTypeException(final IllegalArgumentException ex, final String s) {
        return new TypedXMLStreamException(s, ex.getMessage(), this.getStartLocation(), ex);
    }
    
    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }
    
    protected void throwNotStartElem() {
        throw new IllegalStateException("Current state not START_ELEMENT");
    }
}
