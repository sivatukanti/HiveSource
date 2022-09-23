// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.kerby.xdr.XdrDataType;
import org.apache.kerby.xdr.EnumType;

public abstract class XdrEnumerated<T extends EnumType> extends XdrSimple<T>
{
    public XdrEnumerated() {
        this((EnumType)null);
    }
    
    public XdrEnumerated(final T value) {
        super(XdrDataType.ENUM, value);
    }
    
    @Override
    protected void toBytes() {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(this.getValue().getValue()).array();
        this.setBytes(bytes);
    }
    
    @Override
    protected void toValue() {
        if (this.getBytes().length != 4) {
            final byte[] intBytes = ByteBuffer.allocate(4).put(this.getBytes(), 0, 4).array();
            this.setBytes(intBytes);
        }
        final BigInteger biVal = new BigInteger(this.getBytes());
        final int iVal = biVal.intValue();
        final EnumType[] arr$;
        final EnumType[] allValues = arr$ = this.getAllEnumValues();
        for (final EnumType val : arr$) {
            if (val.getValue() == iVal) {
                this.setValue((T)val);
            }
        }
    }
    
    protected abstract EnumType[] getAllEnumValues();
}
