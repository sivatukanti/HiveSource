// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.EnumType;

public abstract class Asn1Enumerated<T extends EnumType> extends Asn1Simple<T>
{
    public Asn1Enumerated() {
        this((EnumType)null);
    }
    
    public Asn1Enumerated(final T value) {
        super(UniversalTag.ENUMERATED, value);
    }
    
    @Override
    protected void toBytes() {
        final BigInteger biValue = BigInteger.valueOf(this.getValue().getValue());
        this.setBytes(biValue.toByteArray());
    }
    
    @Override
    protected void toValue() throws IOException {
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
