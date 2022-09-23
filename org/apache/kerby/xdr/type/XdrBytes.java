// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.io.IOException;
import org.apache.kerby.xdr.XdrDataType;

public class XdrBytes extends XdrSimple<byte[]>
{
    public XdrBytes() {
        this((byte[])null);
    }
    
    public XdrBytes(final byte[] value) {
        super(XdrDataType.BYTES, value);
    }
    
    @Override
    protected void toValue() throws IOException {
    }
    
    @Override
    protected void toBytes() {
    }
}
