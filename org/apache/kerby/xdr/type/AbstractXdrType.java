// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.kerby.xdr.XdrDataType;

public abstract class AbstractXdrType<T> implements XdrType
{
    private XdrDataType dataType;
    private T value;
    
    public AbstractXdrType(final XdrDataType dataType, final T value) {
        this(dataType);
        this.value = value;
    }
    
    public AbstractXdrType(final XdrDataType dataType) {
        this.dataType = dataType;
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
        this.encodeBody(buffer);
    }
    
    protected abstract void encodeBody(final ByteBuffer p0) throws IOException;
    
    @Override
    public void decode(final byte[] content) throws IOException {
        this.decode(ByteBuffer.wrap(content));
    }
    
    @Override
    public int encodingLength() throws IOException {
        return this.encodingBodyLength();
    }
    
    protected abstract int encodingBodyLength() throws IOException;
    
    @Override
    public void decode(final ByteBuffer content) throws IOException {
    }
    
    public T getValue() {
        return this.value;
    }
    
    public void setValue(final T value) {
        this.value = value;
    }
    
    public XdrDataType getDataType() {
        return this.dataType;
    }
}
