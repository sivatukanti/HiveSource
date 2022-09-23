// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.stream.Location;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import com.ctc.wstx.io.WstxInputData;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import com.ctc.wstx.cfg.ErrorConsts;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.stax2.typed.TypedValueDecoder;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;

public class TypedStreamReader extends BasicStreamReader
{
    protected static final int MASK_TYPED_ACCESS_ARRAY = 4182;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    static final int MIN_BINARY_CHUNK = 2000;
    protected ValueDecoderFactory _decoderFactory;
    protected CharArrayBase64Decoder _base64Decoder;
    
    protected TypedStreamReader(final InputBootstrapper bs, final BranchingReaderSource input, final ReaderCreator owner, final ReaderConfig cfg, final InputElementStack elemStack, final boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
        this._base64Decoder = null;
    }
    
    public static TypedStreamReader createStreamReader(final BranchingReaderSource input, final ReaderCreator owner, final ReaderConfig cfg, final InputBootstrapper bs, final boolean forER) throws XMLStreamException {
        final TypedStreamReader sr = new TypedStreamReader(bs, input, owner, cfg, BasicStreamReader.createElementStack(cfg), forER);
        return sr;
    }
    
    @Override
    public boolean getElementAsBoolean() throws XMLStreamException {
        final ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public int getElementAsInt() throws XMLStreamException {
        final ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public long getElementAsLong() throws XMLStreamException {
        final ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public float getElementAsFloat() throws XMLStreamException {
        final ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public double getElementAsDouble() throws XMLStreamException {
        final ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public BigInteger getElementAsInteger() throws XMLStreamException {
        final ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        final ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }
    
    @Override
    public QName getElementAsQName() throws XMLStreamException {
        final ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(dec);
        return this._verifyQName(dec.getValue());
    }
    
    @Override
    public final byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }
    
    @Override
    public byte[] getElementAsBinary(final Base64Variant v) throws XMLStreamException {
        final Stax2Util.ByteAggregator aggr = this._base64Decoder().getByteAggregator();
        byte[] buffer = aggr.startAggregation();
        int offset = 0;
    Block_1:
        while (true) {
            offset = 0;
            int len = buffer.length;
            do {
                final int readCount = this.readElementAsBinary(buffer, offset, len, v);
                if (readCount < 1) {
                    break Block_1;
                }
                offset += readCount;
                len -= readCount;
            } while (len > 0);
            buffer = aggr.addFullBlock(buffer);
        }
        return aggr.aggregateAll(buffer, offset);
    }
    
    @Override
    public void getElementAs(final TypedValueDecoder tvd) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            this.throwParseError(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        if (this.mStEmptyElem) {
            this.mStEmptyElem = false;
            this.mCurrToken = 2;
            this._handleEmptyValue(tvd);
            return;
        }
        while (true) {
            final int type = this.next();
            if (type == 2) {
                this._handleEmptyValue(tvd);
                return;
            }
            if (type == 5) {
                continue;
            }
            if (type == 3) {
                continue;
            }
            if ((1 << type & 0x1250) == 0x0) {
                this.throwParseError("Expected a text token, got " + this.tokenTypeDesc(type) + ".");
            }
            if (this.mTokenState < 3) {
                this.readCoalescedText(this.mCurrToken, false);
            }
            if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
                this.mInputPtr += 2;
                this.mCurrToken = 2;
                try {
                    this.mTextBuffer.decode(tvd);
                }
                catch (IllegalArgumentException iae) {
                    throw this._constructTypeException(iae, this.mTextBuffer.contentsAsString());
                }
                this.readEndElem();
                return;
            }
            final int extra = 1 + (this.mTextBuffer.size() >> 1);
            final StringBuilder sb = this.mTextBuffer.contentsAsStringBuilder(extra);
            int type2;
            while ((type2 = this.next()) != 2) {
                if ((1 << type2 & 0x1250) != 0x0) {
                    if (this.mTokenState < 3) {
                        this.readCoalescedText(type2, false);
                    }
                    this.mTextBuffer.contentsToStringBuilder(sb);
                }
                else {
                    if (type2 == 5 || type2 == 3) {
                        continue;
                    }
                    this.throwParseError("Expected a text token, got " + this.tokenTypeDesc(type2) + ".");
                }
            }
            final String str = sb.toString();
            final String tstr = Stax2Util.trimSpaces(str);
            if (tstr == null) {
                this._handleEmptyValue(tvd);
            }
            else {
                try {
                    tvd.decode(tstr);
                }
                catch (IllegalArgumentException iae2) {
                    throw this._constructTypeException(iae2, str);
                }
            }
        }
    }
    
    @Override
    public int readElementAsIntArray(final int[] value, final int from, final int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(value, from, length));
    }
    
    @Override
    public int readElementAsLongArray(final long[] value, final int from, final int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(value, from, length));
    }
    
    @Override
    public int readElementAsFloatArray(final float[] value, final int from, final int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(value, from, length));
    }
    
    @Override
    public int readElementAsDoubleArray(final double[] value, final int from, final int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(value, from, length));
    }
    
    @Override
    public final int readElementAsArray(final TypedArrayDecoder dec) throws XMLStreamException {
        int type = this.mCurrToken;
        if ((1 << type & 0x1056) == 0x0) {
            this.throwNotTextualOrElem(type);
        }
        if (type == 1) {
            if (this.mStEmptyElem) {
                this.mStEmptyElem = false;
                this.mCurrToken = 2;
                return -1;
            }
            while (true) {
                type = this.next();
                if (type == 2) {
                    return -1;
                }
                if (type == 5) {
                    continue;
                }
                if (type == 3) {
                    continue;
                }
                if (type == 4) {
                    break;
                }
                if (type == 12) {
                    break;
                }
                throw this._constructUnexpectedInTyped(type);
            }
        }
        int count = 0;
        while (type != 2) {
            if (type == 4 || type == 12 || type == 6) {
                if (this.mTokenState < 3) {
                    this.readCoalescedText(type, false);
                }
                count += this.mTextBuffer.decodeElements(dec, this);
                if (!dec.hasRoom()) {
                    break;
                }
                type = this.next();
            }
            else {
                if (type != 5 && type != 3) {
                    throw this._constructUnexpectedInTyped(type);
                }
                type = this.next();
            }
        }
        return (count > 0) ? count : -1;
    }
    
    @Override
    public final int readElementAsBinary(final byte[] resultBuffer, final int offset, final int maxLength) throws XMLStreamException {
        return this.readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }
    
    @Override
    public int readElementAsBinary(final byte[] resultBuffer, int offset, int maxLength, final Base64Variant v) throws XMLStreamException {
        if (resultBuffer == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Illegal offset (" + offset + "), must be [0, " + resultBuffer.length + "[");
        }
        if (maxLength >= 1 && offset + maxLength <= resultBuffer.length) {
            final CharArrayBase64Decoder dec = this._base64Decoder();
            int type = this.mCurrToken;
            if ((1 << type & 0x1052) == 0x0) {
                if (type == 2) {
                    if (!dec.hasData()) {
                        return -1;
                    }
                }
                else {
                    this.throwNotTextualOrElem(type);
                }
            }
            else if (type == 1) {
                if (this.mStEmptyElem) {
                    this.mStEmptyElem = false;
                    this.mCurrToken = 2;
                    return -1;
                }
                while (true) {
                    type = this.next();
                    if (type == 2) {
                        return -1;
                    }
                    if (type == 5) {
                        continue;
                    }
                    if (type == 3) {
                        continue;
                    }
                    if (this.mTokenState < this.mStTextThreshold) {
                        this.finishToken(false);
                    }
                    this._initBinaryChunks(v, dec, type, true);
                    break;
                }
            }
            int totalCount = 0;
            while (true) {
                int count;
                try {
                    count = dec.decode(resultBuffer, offset, maxLength);
                }
                catch (IllegalArgumentException iae) {
                    throw this._constructTypeException(iae.getMessage(), "");
                }
                offset += count;
                totalCount += count;
                maxLength -= count;
                if (maxLength < 1) {
                    break;
                }
                if (this.mCurrToken == 2) {
                    break;
                }
                while (true) {
                    type = this.next();
                    if (type != 5 && type != 3) {
                        if (type == 6) {
                            continue;
                        }
                        break;
                    }
                }
                if (type == 2) {
                    final int left = dec.endOfContent();
                    if (left < 0) {
                        throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                    }
                    if (left > 0) {
                        continue;
                    }
                    break;
                }
                else {
                    if (this.mTokenState < this.mStTextThreshold) {
                        this.finishToken(false);
                    }
                    this._initBinaryChunks(v, dec, type, false);
                }
            }
            return (totalCount > 0) ? totalCount : -1;
        }
        if (maxLength == 0) {
            return 0;
        }
        throw new IllegalArgumentException("Illegal maxLength (" + maxLength + "), has to be positive number, and offset+maxLength can not exceed" + resultBuffer.length);
    }
    
    private final void _initBinaryChunks(final Base64Variant v, final CharArrayBase64Decoder dec, final int type, final boolean isFirst) throws XMLStreamException {
        if (type == 4) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = (this.readTextSecondary(2000, false) ? 3 : 2);
            }
        }
        else {
            if (type != 12) {
                throw this._constructUnexpectedInTyped(type);
            }
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = (this.readCDataSecondary(2000) ? 3 : 2);
            }
        }
        this.mTextBuffer.initBinaryChunks(v, dec, isFirst);
    }
    
    @Override
    public int getAttributeIndex(final String namespaceURI, final String localName) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack.findAttributeIndex(namespaceURI, localName);
    }
    
    @Override
    public boolean getAttributeAsBoolean(final int index) throws XMLStreamException {
        final ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public int getAttributeAsInt(final int index) throws XMLStreamException {
        final ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public long getAttributeAsLong(final int index) throws XMLStreamException {
        final ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public float getAttributeAsFloat(final int index) throws XMLStreamException {
        final ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public double getAttributeAsDouble(final int index) throws XMLStreamException {
        final ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public BigInteger getAttributeAsInteger(final int index) throws XMLStreamException {
        final ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public BigDecimal getAttributeAsDecimal(final int index) throws XMLStreamException {
        final ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }
    
    @Override
    public QName getAttributeAsQName(final int index) throws XMLStreamException {
        final ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(index, dec);
        return this._verifyQName(dec.getValue());
    }
    
    @Override
    public void getAttributeAs(final int index, final TypedValueDecoder tvd) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        try {
            this.mAttrCollector.decodeValue(index, tvd);
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, this.mAttrCollector.getValue(index));
        }
    }
    
    @Override
    public int[] getAttributeAsIntArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.IntArrayDecoder dec = this._decoderFactory().getIntArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }
    
    @Override
    public long[] getAttributeAsLongArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.LongArrayDecoder dec = this._decoderFactory().getLongArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }
    
    @Override
    public float[] getAttributeAsFloatArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.FloatArrayDecoder dec = this._decoderFactory().getFloatArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }
    
    @Override
    public double[] getAttributeAsDoubleArray(final int index) throws XMLStreamException {
        final ValueDecoderFactory.DoubleArrayDecoder dec = this._decoderFactory().getDoubleArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }
    
    @Override
    public int getAttributeAsArray(final int index, final TypedArrayDecoder tad) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.decodeValues(index, tad, this);
    }
    
    @Override
    public byte[] getAttributeAsBinary(final int index) throws XMLStreamException {
        return this.getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }
    
    @Override
    public byte[] getAttributeAsBinary(final int index, final Base64Variant v) throws XMLStreamException {
        return this.mAttrCollector.decodeBinary(index, v, this._base64Decoder(), this);
    }
    
    protected QName _verifyQName(final QName n) throws TypedXMLStreamException {
        final String ln = n.getLocalPart();
        final int ix = WstxInputData.findIllegalNameChar(ln, this.mCfgNsEnabled, this.mXml11);
        if (ix >= 0) {
            final String prefix = n.getPrefix();
            final String pname = (prefix != null && prefix.length() > 0) ? (prefix + ":" + ln) : ln;
            throw this._constructTypeException("Invalid local name \"" + ln + "\" (character at #" + ix + " is invalid)", pname);
        }
        return n;
    }
    
    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }
    
    protected CharArrayBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new CharArrayBase64Decoder();
        }
        return this._base64Decoder;
    }
    
    private void _handleEmptyValue(final TypedValueDecoder dec) throws XMLStreamException {
        try {
            dec.handleEmptyValue();
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, "");
        }
    }
    
    protected TypedXMLStreamException _constructTypeException(final IllegalArgumentException iae, final String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, iae.getMessage(), this.getStartLocation(), iae);
    }
}
