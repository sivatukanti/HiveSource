// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1Binder;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.TaggingOption;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.Asn1Dumpable;

public class Asn1Choice extends AbstractAsn1Type<Asn1Type> implements Asn1Dumpable
{
    private final Asn1FieldInfo[] fieldInfos;
    private final Tag[] tags;
    private Asn1FieldInfo chosenField;
    
    public Asn1Choice(final Asn1FieldInfo[] fieldInfos) {
        super(UniversalTag.CHOICE);
        this.fieldInfos = fieldInfos;
        this.tags = new Tag[fieldInfos.length];
        this.initTags();
    }
    
    @Override
    public Tag tag() {
        if (this.getValue() != null) {
            return this.getValue().tag();
        }
        if (this.chosenField != null) {
            return this.chosenField.getFieldTag();
        }
        return super.tag();
    }
    
    private void initTags() {
        for (int i = 0; i < this.fieldInfos.length; ++i) {
            this.tags[i] = this.fieldInfos[i].getFieldTag();
        }
    }
    
    public boolean matchAndSetValue(final Tag tag) {
        int foundPos = -1;
        for (int i = 0; i < this.fieldInfos.length; ++i) {
            if (tag.isContextSpecific()) {
                if (this.fieldInfos[i].getTagNo() == tag.tagNo()) {
                    foundPos = i;
                    break;
                }
            }
            else if (this.tags[i].equals(tag)) {
                foundPos = i;
                break;
            }
        }
        if (foundPos != -1) {
            this.chosenField = this.fieldInfos[foundPos];
            this.setValue(this.fieldInfos[foundPos].createFieldValue());
            return true;
        }
        return false;
    }
    
    @Override
    public byte[] encode() throws IOException {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue == null) {
            return null;
        }
        if (this.chosenField.isTagged()) {
            final TaggingOption taggingOption = this.chosenField.getTaggingOption();
            return theValue.taggedEncode(taggingOption);
        }
        return theValue.encode();
    }
    
    @Override
    public void encode(final ByteBuffer buffer) throws IOException {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue != null) {
            if (this.chosenField.isTagged()) {
                final TaggingOption taggingOption = this.chosenField.getTaggingOption();
                theValue.taggedEncode(buffer, taggingOption);
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
        if (this.chosenField.isTagged()) {
            final TaggingOption taggingOption = this.chosenField.getTaggingOption();
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
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        final Asn1Encodeable theValue = ((AbstractAsn1Type<Asn1Encodeable>)this).getValue();
        if (theValue != null) {
            if (this.chosenField.isTagged()) {
                final TaggingOption taggingOption = this.chosenField.getTaggingOption();
                theValue.taggedEncode(buffer, taggingOption);
            }
            else {
                theValue.encode(buffer);
            }
        }
    }
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
        this.chosenField = null;
        this.setValue(null);
        super.decode(content);
    }
    
    @Override
    public void decode(final Asn1ParseResult parseResult) throws IOException {
        if (this.chosenField == null) {
            this.matchAndSetValue(parseResult.tag());
        }
        this.decodeBody(parseResult);
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        if (this.chosenField == null) {
            this.matchAndSetValue(parseResult.tag());
        }
        if (this.chosenField == null) {
            throw new IOException("Unexpected item, not in choices: " + parseResult.simpleInfo());
        }
        final Asn1Type fieldValue = this.getValue();
        if (parseResult.isContextSpecific()) {
            Asn1Binder.bindWithTagging(parseResult, fieldValue, this.chosenField.getTaggingOption());
        }
        else {
            Asn1Binder.bind(parseResult, fieldValue);
        }
    }
    
    protected <T extends Asn1Type> T getChoiceValueAs(final EnumType index, final Class<T> t) {
        if (this.chosenField == null || this.getValue() == null) {
            return null;
        }
        if (this.chosenField != null && index != this.chosenField.getIndex()) {
            throw new IllegalArgumentException("Incorrect chosen value requested");
        }
        return (T)this.getValue();
    }
    
    protected void setChoiceValue(final EnumType index, final Asn1Type value) {
        if (this.fieldInfos[index.getValue()].getIndex() != index) {
            throw new IllegalArgumentException("Incorrect choice option to set");
        }
        this.chosenField = this.fieldInfos[index.getValue()];
        this.setValue(value);
    }
    
    protected void setChoiceValueAsOctets(final EnumType index, final byte[] bytes) {
        final Asn1OctetString value = new Asn1OctetString(bytes);
        this.setChoiceValue(index, value);
    }
    
    protected byte[] getChoiceValueAsOctets(final EnumType index) {
        final Asn1OctetString value = this.getChoiceValueAs(index, Asn1OctetString.class);
        if (value != null) {
            return value.getValue();
        }
        return null;
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        final Asn1Type theValue = this.getValue();
        dumper.indent(indents).append("<Choice>").newLine();
        dumper.dumpType(indents, theValue);
    }
}
