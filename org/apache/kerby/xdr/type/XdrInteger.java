// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.nio.ByteBuffer;
import org.apache.kerby.xdr.XdrDataType;

public class XdrInteger extends XdrSimple<Integer>
{
    public XdrInteger() {
        this((Integer)null);
    }
    
    public XdrInteger(final Integer value) {
        super(XdrDataType.INTEGER, value);
    }
    
    @Override
    protected int encodingBodyLength() {
        return 4;
    }
    
    @Override
    protected void toBytes() {
        final int value = this.getValue();
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        buffer.flip();
        this.setBytes(buffer.array());
    }
    
    @Override
    protected void toValue() {
        if (this.getBytes().length != 4) {
            final byte[] intBytes = ByteBuffer.allocate(4).put(this.getBytes(), 0, 4).array();
            this.setBytes(intBytes);
        }
        final ByteBuffer buffer = ByteBuffer.wrap(this.getBytes());
        this.setValue(buffer.getInt());
    }
}
