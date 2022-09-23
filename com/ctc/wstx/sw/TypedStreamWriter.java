// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sw;

import org.codehaus.stax2.validation.XMLValidator;
import java.io.IOException;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import com.ctc.wstx.api.WriterConfig;
import org.codehaus.stax2.ri.typed.ValueEncoderFactory;

public abstract class TypedStreamWriter extends BaseStreamWriter
{
    protected ValueEncoderFactory mValueEncoderFactory;
    
    protected TypedStreamWriter(final XmlWriter xw, final String enc, final WriterConfig cfg) {
        super(xw, enc, cfg);
    }
    
    protected final ValueEncoderFactory valueEncoderFactory() {
        if (this.mValueEncoderFactory == null) {
            this.mValueEncoderFactory = new ValueEncoderFactory();
        }
        return this.mValueEncoderFactory;
    }
    
    @Override
    public void writeBoolean(final boolean value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeInt(final int value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeLong(final long value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeFloat(final float value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeDouble(final double value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeInteger(final BigInteger value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }
    
    @Override
    public void writeDecimal(final BigDecimal value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }
    
    @Override
    public void writeQName(final QName name) throws XMLStreamException {
        this.writeCharacters(this.serializeQName(name));
    }
    
    @Override
    public final void writeIntArray(final int[] value, final int from, final int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }
    
    @Override
    public void writeLongArray(final long[] value, final int from, final int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }
    
    @Override
    public void writeFloatArray(final float[] value, final int from, final int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }
    
    @Override
    public void writeDoubleArray(final double[] value, final int from, final int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }
    
    @Override
    public void writeBinary(final byte[] value, final int from, final int length) throws XMLStreamException {
        final Base64Variant v = Base64Variants.getDefaultVariant();
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(v, value, from, length));
    }
    
    @Override
    public void writeBinary(final Base64Variant v, final byte[] value, final int from, final int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(v, value, from, length));
    }
    
    protected final void writeTypedElement(final AsciiValueEncoder enc) throws XMLStreamException {
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            BaseStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            this.reportInvalidContent(4);
        }
        try {
            final XMLValidator vld = (this.mVldContent == 3) ? this.mValidator : null;
            if (vld == null) {
                this.mWriter.writeTypedElement(enc);
            }
            else {
                this.mWriter.writeTypedElement(enc, vld, this.getCopyBuffer());
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }
    
    @Override
    public void writeBooleanAttribute(final String prefix, final String nsURI, final String localName, final boolean value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeIntAttribute(final String prefix, final String nsURI, final String localName, final int value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeLongAttribute(final String prefix, final String nsURI, final String localName, final long value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeFloatAttribute(final String prefix, final String nsURI, final String localName, final float value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeDoubleAttribute(final String prefix, final String nsURI, final String localName, final double value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }
    
    @Override
    public void writeIntegerAttribute(final String prefix, final String nsURI, final String localName, final BigInteger value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }
    
    @Override
    public void writeDecimalAttribute(final String prefix, final String nsURI, final String localName, final BigDecimal value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }
    
    @Override
    public void writeQNameAttribute(final String prefix, final String nsURI, final String localName, final QName name) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.serializeQName(name));
    }
    
    @Override
    public void writeIntArrayAttribute(final String prefix, final String nsURI, final String localName, final int[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }
    
    @Override
    public void writeLongArrayAttribute(final String prefix, final String nsURI, final String localName, final long[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }
    
    @Override
    public void writeFloatArrayAttribute(final String prefix, final String nsURI, final String localName, final float[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }
    
    @Override
    public void writeDoubleArrayAttribute(final String prefix, final String nsURI, final String localName, final double[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }
    
    @Override
    public void writeBinaryAttribute(final String prefix, final String nsURI, final String localName, final byte[] value) throws XMLStreamException {
        final Base64Variant v = Base64Variants.getDefaultVariant();
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }
    
    @Override
    public void writeBinaryAttribute(final Base64Variant v, final String prefix, final String nsURI, final String localName, final byte[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }
    
    protected abstract void writeTypedAttribute(final String p0, final String p1, final String p2, final AsciiValueEncoder p3) throws XMLStreamException;
    
    private String serializeQName(final QName name) throws XMLStreamException {
        final String vp = this.validateQNamePrefix(name);
        final String local = name.getLocalPart();
        if (vp == null || vp.length() == 0) {
            return local;
        }
        return vp + ":" + local;
    }
}
