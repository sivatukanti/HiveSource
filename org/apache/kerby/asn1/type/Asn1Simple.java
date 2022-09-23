// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Tag;
import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1Item;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.UniversalTag;

public abstract class Asn1Simple<T> extends AbstractAsn1Type<T>
{
    private byte[] bytes;
    
    public Asn1Simple(final UniversalTag tagNo) {
        this(tagNo, null);
    }
    
    public Asn1Simple(final UniversalTag universalTag, final T value) {
        super(universalTag, value);
        this.usePrimitive(true);
    }
    
    @Override
    public boolean isDefinitiveLength() {
        return true;
    }
    
    protected byte[] getBytes() {
        return this.bytes;
    }
    
    protected void setBytes(final byte[] bytes) {
        this.resetBodyLength();
        this.bytes = bytes;
    }
    
    protected byte[] encodeBody() {
        if (this.bytes == null) {
            this.toBytes();
        }
        return this.bytes;
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) {
        final byte[] body = this.encodeBody();
        if (body != null) {
            buffer.put(body);
        }
    }
    
    @Override
    protected int encodingBodyLength() {
        if (this.getValue() == null) {
            return 0;
        }
        if (this.bytes == null) {
            this.toBytes();
        }
        return this.bytes.length;
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        final Asn1Item item = (Asn1Item)parseResult;
        final byte[] leftBytes = item.readBodyBytes();
        if (leftBytes.length > 0) {
            this.setBytes(leftBytes);
            this.toValue();
        }
    }
    
    protected void toValue() throws IOException {
    }
    
    protected void toBytes() {
    }
    
    public static boolean isSimple(final Tag tag) {
        return isSimple(tag.universalTag());
    }
    
    public static boolean isSimple(final int tag) {
        return isSimple(new Tag(tag));
    }
    
    public static boolean isSimple(final UniversalTag tagNo) {
        switch (tagNo) {
            case BIT_STRING:
            case BMP_STRING:
            case BOOLEAN:
            case ENUMERATED:
            case GENERALIZED_TIME:
            case GENERAL_STRING:
            case IA5_STRING:
            case INTEGER:
            case NULL:
            case NUMERIC_STRING:
            case OBJECT_IDENTIFIER:
            case OCTET_STRING:
            case PRINTABLE_STRING:
            case T61_STRING:
            case UNIVERSAL_STRING:
            case UTC_TIME:
            case UTF8_STRING:
            case VISIBLE_STRING: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static Asn1Simple<?> createSimple(final int tagNo) {
        if (!isSimple(tagNo)) {
            throw new IllegalArgumentException("Not simple type, tag: " + tagNo);
        }
        return createSimple(UniversalTag.fromValue(tagNo));
    }
    
    public static Asn1Simple<?> createSimple(final UniversalTag tagNo) {
        if (!isSimple(tagNo)) {
            throw new IllegalArgumentException("Not simple type, tag: " + tagNo);
        }
        switch (tagNo) {
            case BIT_STRING: {
                return new Asn1BitString();
            }
            case BMP_STRING: {
                return new Asn1BmpString();
            }
            case BOOLEAN: {
                return new Asn1Boolean();
            }
            case ENUMERATED: {
                return null;
            }
            case GENERALIZED_TIME: {
                return new Asn1GeneralizedTime();
            }
            case GENERAL_STRING: {
                return new Asn1GeneralString();
            }
            case IA5_STRING: {
                return new Asn1IA5String();
            }
            case INTEGER: {
                return new Asn1Integer();
            }
            case NULL: {
                return Asn1Null.INSTANCE;
            }
            case NUMERIC_STRING: {
                return new Asn1NumericsString();
            }
            case OBJECT_IDENTIFIER: {
                return new Asn1ObjectIdentifier();
            }
            case OCTET_STRING: {
                return new Asn1OctetString();
            }
            case PRINTABLE_STRING: {
                return new Asn1PrintableString();
            }
            case T61_STRING: {
                return new Asn1T61String();
            }
            case UNIVERSAL_STRING: {
                return new Asn1UniversalString();
            }
            case UTC_TIME: {
                return new Asn1UtcTime();
            }
            case UTF8_STRING: {
                return new Asn1Utf8String();
            }
            case VISIBLE_STRING: {
                return new Asn1VisibleString();
            }
            default: {
                throw new IllegalArgumentException("Unexpected tag " + tagNo.getValue());
            }
        }
    }
    
    @Override
    public String toString() {
        final String typeStr = this.simpleInfo();
        final T theValue = this.getValue();
        final String valueStr = (theValue != null) ? String.valueOf(theValue) : "null";
        return typeStr + valueStr;
    }
}
