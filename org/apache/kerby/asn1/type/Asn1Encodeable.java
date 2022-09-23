// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.TaggingOption;
import org.apache.kerby.asn1.parse.Asn1DerivedItem;
import org.apache.kerby.asn1.parse.Asn1Container;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.parse.Asn1Parser;
import org.apache.kerby.asn1.util.Asn1Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Tag;

public abstract class Asn1Encodeable extends Asn1Object implements Asn1Type
{
    protected int bodyLength;
    public Asn1Encodeable outerEncodeable;
    private EncodingType encodingType;
    private boolean isImplicit;
    private boolean isDefinitiveLength;
    
    public Asn1Encodeable(final Tag tag) {
        super(tag);
        this.bodyLength = -1;
        this.outerEncodeable = null;
        this.encodingType = EncodingType.BER;
        this.isImplicit = true;
        this.isDefinitiveLength = true;
    }
    
    public Asn1Encodeable(final UniversalTag tag) {
        super(tag);
        this.bodyLength = -1;
        this.outerEncodeable = null;
        this.encodingType = EncodingType.BER;
        this.isImplicit = true;
        this.isDefinitiveLength = true;
    }
    
    public Asn1Encodeable(final int tag) {
        super(tag);
        this.bodyLength = -1;
        this.outerEncodeable = null;
        this.encodingType = EncodingType.BER;
        this.isImplicit = true;
        this.isDefinitiveLength = true;
    }
    
    @Override
    public void usePrimitive(final boolean isPrimitive) {
        this.tag().usePrimitive(isPrimitive);
    }
    
    @Override
    public boolean isPrimitive() {
        return this.tag().isPrimitive();
    }
    
    @Override
    public void useDefinitiveLength(final boolean isDefinitiveLength) {
        this.isDefinitiveLength = isDefinitiveLength;
    }
    
    @Override
    public boolean isDefinitiveLength() {
        return this.isDefinitiveLength;
    }
    
    @Override
    public void useImplicit(final boolean isImplicit) {
        this.isImplicit = isImplicit;
    }
    
    @Override
    public boolean isImplicit() {
        return this.isImplicit;
    }
    
    @Override
    public void useDER() {
        this.encodingType = EncodingType.DER;
    }
    
    @Override
    public boolean isDER() {
        return this.encodingType == EncodingType.DER;
    }
    
    @Override
    public void useBER() {
        this.encodingType = EncodingType.BER;
    }
    
    @Override
    public boolean isBER() {
        return this.encodingType == EncodingType.BER;
    }
    
    @Override
    public void useCER() {
        this.encodingType = EncodingType.CER;
    }
    
    @Override
    public boolean isCER() {
        return this.encodingType == EncodingType.CER;
    }
    
    @Override
    public byte[] encode() throws IOException {
        final int len = this.encodingLength();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        this.encode(byteBuffer);
        byteBuffer.flip();
        return byteBuffer.array();
    }
    
    @Override
    public void encode(final ByteBuffer buffer) throws IOException {
        Asn1Util.encodeTag(buffer, this.tag());
        final int bodyLen = this.getBodyLength();
        Asn1Util.encodeLength(buffer, bodyLen);
        this.encodeBody(buffer);
    }
    
    public void resetBodyLength() {
        if (this.bodyLength != -1) {
            this.bodyLength = -1;
            if (this.outerEncodeable != null) {
                this.outerEncodeable.resetBodyLength();
            }
        }
    }
    
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
    }
    
    @Override
    public void decode(final byte[] content) throws IOException {
        this.decode(ByteBuffer.wrap(content));
    }
    
    @Override
    public int encodingLength() {
        return this.getHeaderLength() + this.getBodyLength();
    }
    
    @Override
    protected int getHeaderLength() {
        try {
            return this.encodingHeaderLength();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected int getBodyLength() {
        if (this.bodyLength == -1) {
            try {
                this.bodyLength = this.encodingBodyLength();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (this.bodyLength == -1) {
                throw new RuntimeException("Unexpected body length: -1");
            }
        }
        return this.bodyLength;
    }
    
    protected int encodingHeaderLength() throws IOException {
        int headerLen = Asn1Util.lengthOfTagLength(this.tagNo());
        final int bodyLen = this.getBodyLength();
        headerLen += Asn1Util.lengthOfBodyLength(bodyLen);
        return headerLen;
    }
    
    protected abstract int encodingBodyLength() throws IOException;
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
        final Asn1ParseResult parseResult = Asn1Parser.parse(content);
        this.decode(parseResult);
    }
    
    public void decode(final Asn1ParseResult parseResult) throws IOException {
        Asn1ParseResult tmpParseResult = parseResult;
        if (!this.tag().equals(parseResult.tag())) {
            if (!this.isPrimitive() || parseResult.isPrimitive()) {
                throw new IOException("Unexpected item " + parseResult.simpleInfo() + ", expecting " + this.tag());
            }
            final Asn1Container container = (Asn1Container)parseResult;
            tmpParseResult = new Asn1DerivedItem(this.tag(), container);
        }
        this.decodeBody(tmpParseResult);
    }
    
    protected abstract void decodeBody(final Asn1ParseResult p0) throws IOException;
    
    protected int taggedEncodingLength(final TaggingOption taggingOption) {
        final int taggingTagNo = taggingOption.getTagNo();
        final int taggingBodyLen = taggingOption.isImplicit() ? this.getBodyLength() : this.encodingLength();
        final int taggingEncodingLen = Asn1Util.lengthOfTagLength(taggingTagNo) + Asn1Util.lengthOfBodyLength(taggingBodyLen) + taggingBodyLen;
        return taggingEncodingLen;
    }
    
    @Override
    public byte[] taggedEncode(final TaggingOption taggingOption) throws IOException {
        final int len = this.taggedEncodingLength(taggingOption);
        final ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        this.taggedEncode(byteBuffer, taggingOption);
        byteBuffer.flip();
        return byteBuffer.array();
    }
    
    @Override
    public void taggedEncode(final ByteBuffer buffer, final TaggingOption taggingOption) throws IOException {
        final Tag taggingTag = taggingOption.getTag(!this.isPrimitive());
        Asn1Util.encodeTag(buffer, taggingTag);
        final int taggingBodyLen = taggingOption.isImplicit() ? this.encodingBodyLength() : this.encodingLength();
        Asn1Util.encodeLength(buffer, taggingBodyLen);
        if (taggingOption.isImplicit()) {
            this.encodeBody(buffer);
        }
        else {
            this.encode(buffer);
        }
    }
    
    @Override
    public void taggedDecode(final byte[] content, final TaggingOption taggingOption) throws IOException {
        this.taggedDecode(ByteBuffer.wrap(content), taggingOption);
    }
    
    @Override
    public void taggedDecode(final ByteBuffer content, final TaggingOption taggingOption) throws IOException {
        final Asn1ParseResult parseResult = Asn1Parser.parse(content);
        this.taggedDecode(parseResult, taggingOption);
    }
    
    public void taggedDecode(final Asn1ParseResult parseResult, final TaggingOption taggingOption) throws IOException {
        final Tag expectedTaggingTagFlags = taggingOption.getTag(!this.isPrimitive());
        Asn1ParseResult tmpParseResult = parseResult;
        if (!expectedTaggingTagFlags.equals(parseResult.tag())) {
            if (!this.isPrimitive() || parseResult.isPrimitive()) {
                throw new IOException("Unexpected tag " + parseResult.tag() + ", expecting " + expectedTaggingTagFlags);
            }
            final Asn1Container container = (Asn1Container)parseResult;
            tmpParseResult = new Asn1DerivedItem(this.tag(), container);
        }
        if (taggingOption.isImplicit()) {
            this.decodeBody(tmpParseResult);
        }
        else {
            final Asn1Container container = (Asn1Container)parseResult;
            tmpParseResult = container.getChildren().get(0);
            this.decode(tmpParseResult);
        }
    }
}
