// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.Asn1Dumpable;

public abstract class Asn1TaggingCollection extends AbstractAsn1Type<Asn1CollectionType> implements Asn1Dumpable
{
    private Asn1Tagging<Asn1CollectionType> tagging;
    private Asn1CollectionType tagged;
    
    public Asn1TaggingCollection(final int taggingTagNo, final Asn1FieldInfo[] tags, final boolean isAppSpecific, final boolean isImplicit) {
        super(makeTag(isAppSpecific, taggingTagNo));
        this.setValue(this.tagged = this.createTaggedCollection(tags));
        this.tagging = new Asn1Tagging<Asn1CollectionType>(taggingTagNo, this.tagged, isAppSpecific, isImplicit);
    }
    
    private static Tag makeTag(final boolean isAppSpecific, final int tagNo) {
        return isAppSpecific ? Tag.newAppTag(tagNo) : Tag.newCtxTag(tagNo);
    }
    
    protected abstract Asn1CollectionType createTaggedCollection(final Asn1FieldInfo[] p0);
    
    @Override
    public Tag tag() {
        return this.tagging.tag();
    }
    
    @Override
    public int tagNo() {
        return this.tagging.tagNo();
    }
    
    @Override
    public void usePrimitive(final boolean isPrimitive) {
        this.tagging.usePrimitive(isPrimitive);
    }
    
    @Override
    public boolean isPrimitive() {
        return this.tagging.isPrimitive();
    }
    
    @Override
    public void useDefinitiveLength(final boolean isDefinitiveLength) {
        this.tagging.useDefinitiveLength(isDefinitiveLength);
    }
    
    @Override
    public boolean isDefinitiveLength() {
        return this.tagging.isDefinitiveLength();
    }
    
    @Override
    public void useImplicit(final boolean isImplicit) {
        this.tagging.useImplicit(isImplicit);
    }
    
    @Override
    public boolean isImplicit() {
        return this.tagging.isImplicit();
    }
    
    @Override
    public void useDER() {
        this.tagging.useDER();
    }
    
    @Override
    public boolean isDER() {
        return this.tagging.isDER();
    }
    
    @Override
    public void useBER() {
        this.tagging.useBER();
    }
    
    @Override
    public boolean isBER() {
        return this.tagging.isBER();
    }
    
    @Override
    public void useCER() {
        this.tagging.useCER();
    }
    
    @Override
    public boolean isCER() {
        return this.tagging.isCER();
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        return this.tagging.encodingBodyLength();
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        this.tagging.encodeBody(buffer);
    }
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
        this.tagging.decode(content);
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        this.tagging.decodeBody(parseResult);
    }
    
    protected <T extends Asn1Type> T getFieldAs(final EnumType index, final Class<T> t) {
        return this.tagged.getFieldAs(index, t);
    }
    
    protected void setFieldAs(final EnumType index, final Asn1Type value) {
        this.tagged.setFieldAs(index, value);
    }
    
    protected String getFieldAsString(final EnumType index) {
        return this.tagged.getFieldAsString(index);
    }
    
    protected byte[] getFieldAsOctets(final EnumType index) {
        return this.tagged.getFieldAsOctets(index);
    }
    
    protected void setFieldAsOctets(final EnumType index, final byte[] bytes) {
        this.tagged.setFieldAsOctets(index, bytes);
    }
    
    protected Integer getFieldAsInteger(final EnumType index) {
        return this.tagged.getFieldAsInteger(index);
    }
    
    protected void setFieldAsInt(final EnumType index, final int value) {
        this.tagged.setFieldAsInt(index, value);
    }
    
    protected byte[] getFieldAsOctetBytes(final EnumType index) {
        return this.tagged.getFieldAsOctets(index);
    }
    
    protected void setFieldAsOctetBytes(final EnumType index, final byte[] value) {
        this.tagged.setFieldAsOctets(index, value);
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        final Asn1Type taggedValue = ((AbstractAsn1Type<Asn1Type>)this).getValue();
        dumper.indent(indents).appendType(this.getClass());
        dumper.append(this.simpleInfo()).newLine();
        dumper.dumpType(indents, taggedValue);
    }
}
