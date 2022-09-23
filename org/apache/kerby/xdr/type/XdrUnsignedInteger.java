// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.kerby.xdr.XdrDataType;

public class XdrUnsignedInteger extends XdrSimple<Long>
{
    public XdrUnsignedInteger() {
        this((Long)null);
    }
    
    public XdrUnsignedInteger(final String value) {
        this(Long.valueOf(value));
    }
    
    public XdrUnsignedInteger(final Long value) {
        super(XdrDataType.UNSIGNED_INTEGER, value);
    }
    
    @Override
    protected int encodingBodyLength() {
        return 4;
    }
    
    @Override
    protected void toBytes() throws IOException {
        final Long value = this.getValue();
        this.validateUnsignedInteger(value);
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        final byte[] bytes = new byte[4];
        System.arraycopy(buffer.array(), 4, bytes, 0, 4);
        this.setBytes(bytes);
    }
    
    private void validateUnsignedInteger(final Long value) throws IOException {
        if (value < 0L || value > 4294967295L) {
            throw new IOException("Invalid unsigned integer: " + value);
        }
    }
    
    @Override
    protected void toValue() {
        if (this.getBytes().length != 4) {
            final byte[] bytes = ByteBuffer.allocate(4).put(this.getBytes(), 0, 4).array();
            this.setBytes(bytes);
        }
        final byte[] longBytes = { 0, 0, 0, 0, 0, 0, 0, 0 };
        System.arraycopy(this.getBytes(), 0, longBytes, 4, 4);
        final ByteBuffer buffer = ByteBuffer.wrap(longBytes);
        this.setValue(buffer.getLong());
    }
}
