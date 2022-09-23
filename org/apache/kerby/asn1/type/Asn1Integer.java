// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.UniversalTag;
import java.math.BigInteger;

public class Asn1Integer extends Asn1Simple<BigInteger>
{
    public Asn1Integer() {
        this((BigInteger)null);
    }
    
    public Asn1Integer(final Integer value) {
        this(BigInteger.valueOf(value));
    }
    
    public Asn1Integer(final Long value) {
        this(BigInteger.valueOf(value));
    }
    
    public Asn1Integer(final BigInteger value) {
        super(UniversalTag.INTEGER, value);
    }
    
    @Override
    protected void toBytes() {
        this.setBytes(this.getValue().toByteArray());
    }
    
    @Override
    protected void toValue() throws IOException {
        this.setValue(new BigInteger(this.getBytes()));
    }
}
