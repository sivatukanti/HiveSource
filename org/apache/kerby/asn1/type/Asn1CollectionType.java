// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import java.math.BigInteger;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1Binder;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.asn1.parse.Asn1Container;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.TaggingOption;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.Asn1Dumpable;

public abstract class Asn1CollectionType extends AbstractAsn1Type<Asn1CollectionType> implements Asn1Dumpable
{
    private final Asn1FieldInfo[] fieldInfos;
    private final Asn1Type[] fields;
    
    public Asn1CollectionType(final UniversalTag universalTag, final Asn1FieldInfo[] fieldInfos) {
        super(universalTag);
        this.setValue(this);
        this.fieldInfos = fieldInfos;
        this.fields = new Asn1Type[fieldInfos.length];
        this.usePrimitive(false);
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        int allLen = 0;
        for (int i = 0; i < this.fields.length; ++i) {
            final Asn1Encodeable field = (Asn1Encodeable)this.fields[i];
            if (field != null) {
                int fieldLen;
                if (this.fieldInfos[i].isTagged()) {
                    final TaggingOption taggingOption = this.fieldInfos[i].getTaggingOption();
                    fieldLen = field.taggedEncodingLength(taggingOption);
                }
                else {
                    fieldLen = field.encodingLength();
                }
                allLen += fieldLen;
            }
        }
        return allLen;
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        for (int i = 0; i < this.fields.length; ++i) {
            final Asn1Type field = this.fields[i];
            if (field != null) {
                if (this.fieldInfos[i].isTagged()) {
                    final TaggingOption taggingOption = this.fieldInfos[i].getTaggingOption();
                    field.taggedEncode(buffer, taggingOption);
                }
                else {
                    field.encode(buffer);
                }
            }
        }
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        this.useDefinitiveLength(parseResult.isDefinitiveLength());
        final Asn1Container container = (Asn1Container)parseResult;
        final List<Asn1ParseResult> parseResults = container.getChildren();
        int lastPos = -1;
        int foundPos = -1;
        for (final Asn1ParseResult parseItem : parseResults) {
            if (parseItem.isEOC()) {
                continue;
            }
            foundPos = this.match(lastPos, parseItem);
            if (foundPos == -1) {
                throw new IOException("Unexpected item: " + parseItem.simpleInfo());
            }
            lastPos = foundPos;
            this.attemptBinding(parseItem, foundPos);
        }
    }
    
    private void attemptBinding(final Asn1ParseResult parseItem, final int foundPos) throws IOException {
        final Asn1FieldInfo fieldInfo = this.fieldInfos[foundPos];
        this.checkAndInitField(foundPos);
        final Asn1Type fieldValue = this.fields[foundPos];
        if (fieldValue instanceof Asn1Any) {
            final Asn1Any any = (Asn1Any)fieldValue;
            any.setDecodeInfo(fieldInfo);
            Asn1Binder.bind(parseItem, any);
        }
        else if (parseItem.isContextSpecific()) {
            Asn1Binder.bindWithTagging(parseItem, fieldValue, fieldInfo.getTaggingOption());
        }
        else {
            Asn1Binder.bind(parseItem, fieldValue);
        }
    }
    
    private int match(final int lastPos, final Asn1ParseResult parseItem) {
        int foundPos = -1;
        for (int i = lastPos + 1; i < this.fieldInfos.length; ++i) {
            final Asn1Type fieldValue = this.fields[i];
            final Asn1FieldInfo fieldInfo = this.fieldInfos[i];
            if (fieldInfo.isTagged()) {
                if (parseItem.isContextSpecific()) {
                    if (fieldInfo.getTagNo() == parseItem.tagNo()) {
                        foundPos = i;
                        break;
                    }
                }
            }
            else if (fieldValue != null) {
                if (fieldValue.tag().equals(parseItem.tag())) {
                    foundPos = i;
                    break;
                }
                if (fieldValue instanceof Asn1Choice) {
                    final Asn1Choice aChoice = (Asn1Choice)fieldValue;
                    if (aChoice.matchAndSetValue(parseItem.tag())) {
                        foundPos = i;
                        break;
                    }
                }
                else if (fieldValue instanceof Asn1Any) {
                    foundPos = i;
                    break;
                }
            }
            else {
                if (fieldInfo.getFieldTag().equals(parseItem.tag())) {
                    foundPos = i;
                    break;
                }
                if (Asn1Choice.class.isAssignableFrom(fieldInfo.getType())) {
                    final Asn1Type[] fields = this.fields;
                    final int n = i;
                    final Asn1Type fieldValue2 = fieldInfo.createFieldValue();
                    fields[n] = fieldValue2;
                    final Asn1Choice aChoice = (Asn1Choice)fieldValue2;
                    if (aChoice.matchAndSetValue(parseItem.tag())) {
                        foundPos = i;
                        break;
                    }
                }
                else if (Asn1Any.class.isAssignableFrom(fieldInfo.getType())) {
                    foundPos = i;
                    break;
                }
            }
        }
        return foundPos;
    }
    
    private void checkAndInitField(final int index) {
        if (this.fields[index] == null) {
            this.fields[index] = this.fieldInfos[index].createFieldValue();
        }
    }
    
    protected abstract Asn1Collection createCollection();
    
    protected <T extends Asn1Type> T getFieldAs(final EnumType index, final Class<T> t) {
        final Asn1Type value = this.fields[index.getValue()];
        if (value == null) {
            return null;
        }
        return (T)value;
    }
    
    protected void setFieldAs(final EnumType index, final Asn1Type value) {
        this.resetBodyLength();
        if (value instanceof Asn1Encodeable) {
            ((Asn1Encodeable)value).outerEncodeable = this;
        }
        this.fields[index.getValue()] = value;
    }
    
    protected String getFieldAsString(final EnumType index) {
        final Asn1Type value = this.fields[index.getValue()];
        if (value == null) {
            return null;
        }
        if (value instanceof Asn1String) {
            return ((Asn1String)value).getValue();
        }
        throw new RuntimeException("The targeted field type isn't of string");
    }
    
    protected byte[] getFieldAsOctets(final EnumType index) {
        final Asn1OctetString value = this.getFieldAs(index, Asn1OctetString.class);
        if (value != null) {
            return value.getValue();
        }
        return null;
    }
    
    protected void setFieldAsOctets(final EnumType index, final byte[] bytes) {
        final Asn1OctetString value = new Asn1OctetString(bytes);
        this.setFieldAs(index, value);
    }
    
    protected Integer getFieldAsInteger(final EnumType index) {
        final Asn1Integer value = this.getFieldAs(index, Asn1Integer.class);
        if (value != null && value.getValue() != null) {
            return value.getValue().intValue();
        }
        return null;
    }
    
    protected void setFieldAsInt(final EnumType index, final int value) {
        this.setFieldAs(index, new Asn1Integer(Integer.valueOf(value)));
    }
    
    protected void setFieldAsInt(final EnumType index, final BigInteger value) {
        this.setFieldAs(index, new Asn1Integer(value));
    }
    
    protected void setFieldAsObjId(final EnumType index, final String value) {
        this.setFieldAs(index, new Asn1ObjectIdentifier(value));
    }
    
    protected String getFieldAsObjId(final EnumType index) {
        final Asn1ObjectIdentifier objId = this.getFieldAs(index, Asn1ObjectIdentifier.class);
        if (objId != null) {
            return objId.getValue();
        }
        return null;
    }
    
    protected <T extends Asn1Type> T getFieldAsAny(final EnumType index, final Class<T> t) {
        final Asn1Type value = this.fields[index.getValue()];
        if (value != null && value instanceof Asn1Any) {
            final Asn1Any any = (Asn1Any)value;
            return any.getValueAs(t);
        }
        return null;
    }
    
    protected void setFieldAsAny(final EnumType index, final Asn1Type value) {
        if (value != null) {
            final Asn1Any any = new Asn1Any(value);
            any.setDecodeInfo(this.fieldInfos[index.getValue()]);
            this.setFieldAs(index, any);
        }
    }
    
    protected void setAnyFieldValueType(final EnumType index, final Class<? extends Asn1Type> valueType) {
        if (valueType != null) {
            this.checkAndInitField(index.getValue());
            final Asn1Type value = this.fields[index.getValue()];
            if (value != null && value instanceof Asn1Any) {
                final Asn1Any any = (Asn1Any)value;
                any.setValueType(valueType);
            }
        }
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        dumper.indent(indents).appendType(this.getClass());
        dumper.append(this.simpleInfo()).newLine();
        for (int i = 0; i < this.fieldInfos.length; ++i) {
            String fdName = this.fieldInfos[i].getIndex().getName();
            fdName = fdName.replace("_", "-").toLowerCase();
            dumper.indent(indents + 4).append(fdName).append(" = ");
            final Asn1Type fdValue = this.fields[i];
            if (fdValue == null || fdValue instanceof Asn1Simple) {
                dumper.append((Asn1Simple<?>)fdValue);
            }
            else {
                dumper.newLine().dumpType(indents + 8, fdValue);
            }
            if (i < this.fieldInfos.length - 1) {
                dumper.newLine();
            }
        }
    }
}
