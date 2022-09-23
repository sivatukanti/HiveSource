// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1Dumper;
import java.lang.reflect.ParameterizedType;
import org.apache.kerby.asn1.parse.Asn1Container;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.Tag;
import org.apache.kerby.asn1.Asn1Dumpable;

public class Asn1Tagging<T extends Asn1Type> extends AbstractAsn1Type<T> implements Asn1Dumpable
{
    public Asn1Tagging(final int tagNo, final T value, final boolean isAppSpecific, final boolean isImplicit) {
        super(makeTag(isAppSpecific, tagNo), value);
        if (value == null) {
            this.initValue();
        }
        this.useImplicit(isImplicit);
    }
    
    private static Tag makeTag(final boolean isAppSpecific, final int tagNo) {
        return isAppSpecific ? Tag.newAppTag(tagNo) : Tag.newCtxTag(tagNo);
    }
    
    @Override
    public void useImplicit(final boolean isImplicit) {
        super.useImplicit(isImplicit);
        if (!isImplicit) {
            this.usePrimitive(false);
        }
        else {
            this.usePrimitive(this.getValue().isPrimitive());
        }
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        final Asn1Encodeable value = this.getValue();
        if (this.isImplicit()) {
            return value.encodingBodyLength();
        }
        return value.encodingLength();
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        final Asn1Encodeable value = this.getValue();
        if (this.isImplicit()) {
            value.encodeBody(buffer);
        }
        else {
            value.encode(buffer);
        }
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        final Asn1Encodeable value = this.getValue();
        if (this.isImplicit()) {
            value.decodeBody(parseResult);
        }
        else {
            final Asn1Container container = (Asn1Container)parseResult;
            final Asn1ParseResult body = container.getChildren().get(0);
            value.decode(body);
        }
    }
    
    private void initValue() {
        final Class<? extends Asn1Type> valueType = (Class<? extends Asn1Type>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        AbstractAsn1Type<?> value;
        try {
            value = (AbstractAsn1Type<?>)valueType.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create tagged value", e);
        }
        this.setValue((T)value);
    }
    
    @Override
    public void dumpWith(final Asn1Dumper dumper, final int indents) {
        final Asn1Type taggedValue = this.getValue();
        dumper.indent(indents).appendType(this.getClass());
        dumper.append(this.simpleInfo()).newLine();
        dumper.dumpType(indents, taggedValue);
    }
}
