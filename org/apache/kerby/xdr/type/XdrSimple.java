// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.xdr.XdrDataType;

public abstract class XdrSimple<T> extends AbstractXdrType<T>
{
    private byte[] bytes;
    
    public XdrSimple(final XdrDataType dataTypeNo) {
        this(dataTypeNo, null);
    }
    
    public XdrSimple(final XdrDataType xdrDataType, final T value) {
        super(xdrDataType, value);
    }
    
    protected byte[] getBytes() {
        return this.bytes;
    }
    
    protected void setBytes(final byte[] bytes) {
        if (bytes != null) {
            this.bytes = bytes.clone();
        }
        else {
            this.bytes = null;
        }
    }
    
    protected byte[] encodeBody() throws IOException {
        if (this.bytes == null) {
            this.toBytes();
        }
        return this.bytes;
    }
    
    @Override
    protected void encodeBody(final ByteBuffer buffer) throws IOException {
        final byte[] body = this.encodeBody();
        if (body != null) {
            buffer.put(body);
        }
    }
    
    @Override
    protected int encodingBodyLength() throws IOException {
        if (this.getValue() == null) {
            return 0;
        }
        if (this.bytes == null) {
            this.toBytes();
        }
        return this.bytes.length;
    }
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
        this.decodeBody(content);
    }
    
    protected void decodeBody(final ByteBuffer body) throws IOException {
        final byte[] result = body.array();
        if (result.length > 0) {
            this.setBytes(result);
            this.toValue();
        }
    }
    
    protected abstract void toValue() throws IOException;
    
    protected abstract void toBytes() throws IOException;
    
    public static boolean isSimple(final XdrDataType dataType) {
        switch (dataType) {
            case BOOLEAN:
            case INTEGER:
            case UNSIGNED_INTEGER:
            case ENUM:
            case STRING: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
