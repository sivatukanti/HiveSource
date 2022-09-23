// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.asn1.Asn1Binder;
import java.util.List;
import org.apache.kerby.asn1.Asn1Converter;
import java.io.IOException;
import org.apache.kerby.asn1.TaggingOption;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.Asn1Dumpable;

public class Asn1Any extends AbstractAsn1Type<Asn1Type> implements Asn1Dumpable
{
    private Class<? extends Asn1Type> valueType;
    private Asn1FieldInfo decodeInfo;
    private Asn1ParseResult parseResult;
    private boolean isBlindlyDecoded;
    
    public Asn1Any() {
        super(UniversalTag.ANY);
        this.isBlindlyDecoded = true;
    }
    
    public Asn1Any(final Asn1Type anyValue) {
        this();
        this.setValue(anyValue);
    }
    
    @Override
    public Tag tag() {
        if (this.getValue() != null) {
            return this.getValue().tag();
        }
        if (this.parseResult != null) {
            return this.parseResult.tag();
        }
        return super.tag();
    }
    
    public void setValueType(final Class<? extends Asn1Type> valueType) {
        this.valueType = valueType;
    }
    
    public void setDecodeInfo(final Asn1FieldInfo decodeInfo) {
        this.decodeInfo = decodeInfo;
    }
    
    public Asn1ParseResult getParseResult() {
        return this.parseResult;
    }
    
    @Override
    public void encode(final ByteBuffer buffer) throws IOException {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue != null) {
            if (!this.isBlindlyDecoded) {
                if (this.decodeInfo.isTagged()) {
                    final TaggingOption taggingOption = this.decodeInfo.getTaggingOption();
                    theValue.taggedEncode(buffer, taggingOption);
                }
                else {
                    theValue.encode(buffer);
                }
            }
            else {
                theValue.encode(buffer);
            }
        }
    }
    
    @Override
    public int encodingLength() {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue == null) {
            return super.encodingLength();
        }
        if (this.isBlindlyDecoded) {
            return theValue.encodingLength();
        }
        if (this.decodeInfo.isTagged()) {
            final TaggingOption taggingOption = this.decodeInfo.getTaggingOption();
            return theValue.taggedEncodingLength(taggingOption);
        }
        return theValue.encodingLength();
    }
    
    @Override
    protected int encodingBodyLength() {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue == null) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
        this.setValue(null);
        super.decode(content);
    }
    
    @Override
    public void decode(final Asn1ParseResult parseResult) throws IOException {
        this.decodeBody(parseResult);
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        this.parseResult = parseResult;
        if (this.valueType != null) {
            this.typeAwareDecode(this.valueType);
        }
        else {
            this.blindlyDecode();
        }
    }
    
    private void blindlyDecode() throws IOException {
        final Asn1Type anyValue = Asn1Converter.convert(this.parseResult, false);
        if (this.decodeInfo != null && this.decodeInfo.isTagged()) {
            final Asn1Constructed constructed = (Asn1Constructed)anyValue;
            final Asn1Type innerValue = constructed.getValue().get(0);
            this.setValue(innerValue);
        }
        else {
            this.setValue(anyValue);
        }
        this.isBlindlyDecoded = true;
    }
    
    protected <T extends Asn1Type> T getValueAs(final Class<T> t) {
        final Asn1Type value = this.getValue();
        if (value != null && !this.isBlindlyDecoded) {
            return (T)value;
        }
        if (this.valueType != null && this.valueType != t) {
            throw new RuntimeException("Required value type isn't the same with the value type set before");
        }
        try {
            this.typeAwareDecode(t);
        }
        catch (IOException e) {
            throw new RuntimeException("Type aware decoding of Any type failed");
        }
        return (T)this.getValue();
    }
    
    private <T extends Asn1Type> void typeAwareDecode(final Class<T> t) throws IOException {
        T result;
        try {
            result = t.newInstance();
        }
        catch (Exception e) {
            throw new IOException("No default constructor?", e);
        }
        if (this.parseResult.isContextSpecific()) {
            Asn1Binder.bindWithTagging(this.parseResult, result, this.decodeInfo.getTaggingOption());
        }
        else {
            Asn1Binder.bind(this.parseResult, result);
        }
        this.setValue(result);
        this.isBlindlyDecoded = false;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        final Asn1Type theValue = this.getValue();
        dumper.indent(indents).append("<Any>").newLine();
        dumper.dumpType(indents, theValue);
    }
}
