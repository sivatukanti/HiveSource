// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.io.IOException;
import java.util.Arrays;
import java.nio.ByteBuffer;
import org.apache.kerby.xdr.XdrDataType;

public class XdrBoolean extends XdrSimple<Boolean>
{
    private static final byte[] TRUE_BYTE;
    private static final byte[] FALSE_BYTE;
    public static final XdrBoolean TRUE;
    public static final XdrBoolean FALSE;
    
    public XdrBoolean() {
        this((Boolean)null);
    }
    
    public XdrBoolean(final Boolean value) {
        super(XdrDataType.BOOLEAN, value);
    }
    
    @Override
    protected int encodingBodyLength() {
        return 4;
    }
    
    @Override
    protected void toBytes() {
        this.setBytes(((boolean)this.getValue()) ? XdrBoolean.TRUE_BYTE : XdrBoolean.FALSE_BYTE);
    }
    
    @Override
    protected void toValue() throws IOException {
        if (this.getBytes().length != 4) {
            final byte[] boolBytes = ByteBuffer.allocate(4).put(this.getBytes(), 0, 4).array();
            this.setBytes(boolBytes);
        }
        final byte[] bytes = this.getBytes();
        if (Arrays.equals(bytes, XdrBoolean.TRUE_BYTE)) {
            this.setValue(true);
        }
        else {
            if (!Arrays.equals(bytes, XdrBoolean.FALSE_BYTE)) {
                throw new IOException("Fail to decode boolean type: " + Arrays.toString(bytes));
            }
            this.setValue(false);
        }
    }
    
    static {
        TRUE_BYTE = new byte[] { 0, 0, 0, 1 };
        FALSE_BYTE = new byte[] { 0, 0, 0, 0 };
        TRUE = new XdrBoolean(true);
        FALSE = new XdrBoolean(false);
    }
}
