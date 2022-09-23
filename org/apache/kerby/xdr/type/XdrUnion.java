// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.xdr.XdrDataType;
import org.apache.kerby.xdr.XdrFieldInfo;

public abstract class XdrUnion extends AbstractXdrType<XdrUnion>
{
    private XdrFieldInfo[] fieldInfos;
    private XdrType[] fields;
    
    public XdrUnion(final XdrDataType xdrDataType) {
        super(xdrDataType);
        this.fieldInfos = null;
        this.fields = null;
    }
    
    public XdrUnion(final XdrDataType xdrDataType, final XdrFieldInfo[] fieldInfos) {
        super(xdrDataType);
        if (fieldInfos != null) {
            this.fieldInfos = fieldInfos.clone();
            this.getUnionInstance(this.fields = new XdrType[fieldInfos.length], fieldInfos);
        }
        else {
            this.fieldInfos = null;
            this.fields = null;
        }
    }
    
    protected abstract void getUnionInstance(final XdrType[] p0, final XdrFieldInfo[] p1);
    
    public XdrFieldInfo[] getXdrFieldInfos() {
        return this.fieldInfos;
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        int allLen = 0;
        for (int i = 0; i < this.fields.length; ++i) {
            final AbstractXdrType field = (AbstractXdrType)this.fields[i];
            if (field != null) {
                allLen += field.encodingLength();
            }
        }
        return allLen;
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        for (int i = 0; i < this.fields.length; ++i) {
            final XdrType field = this.fields[i];
            if (field != null) {
                field.encode(buffer);
            }
        }
    }
    
    @Override
    public void decode(ByteBuffer content) throws IOException {
        final AbstractXdrType[] fields = this.getAllFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i] != null) {
                fields[i].decode(content);
                final int length = fields[i].encodingLength();
                final byte[] array = content.array();
                final byte[] newArray = new byte[array.length - length];
                System.arraycopy(array, length, newArray, 0, array.length - length);
                content = ByteBuffer.wrap(newArray);
            }
        }
        this.fields = fields;
        this.setValue(this.fieldsToValues(fields));
    }
    
    protected abstract XdrUnion fieldsToValues(final AbstractXdrType[] p0);
    
    protected abstract AbstractXdrType[] getAllFields();
}
