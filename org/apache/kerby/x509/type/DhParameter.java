// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.EnumType;
import java.math.BigInteger;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class DhParameter extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DhParameter() {
        super(DhParameter.fieldInfos);
    }
    
    public void setP(final BigInteger p) {
        this.setFieldAsInt(DhParameterField.P, p);
    }
    
    public BigInteger getP() {
        final Asn1Integer p = this.getFieldAs(DhParameterField.P, Asn1Integer.class);
        return p.getValue();
    }
    
    public void setG(final BigInteger g) {
        this.setFieldAsInt(DhParameterField.G, g);
    }
    
    public BigInteger getG() {
        final Asn1Integer g = this.getFieldAs(DhParameterField.G, Asn1Integer.class);
        return g.getValue();
    }
    
    public void setQ(final BigInteger q) {
        this.setFieldAsInt(DhParameterField.Q, q);
    }
    
    public BigInteger getQ() {
        final Asn1Integer q = this.getFieldAs(DhParameterField.Q, Asn1Integer.class);
        return q.getValue();
    }
    
    static {
        DhParameter.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(DhParameterField.P, Asn1Integer.class), new Asn1FieldInfo(DhParameterField.G, Asn1Integer.class), new Asn1FieldInfo(DhParameterField.Q, Asn1Integer.class) };
    }
    
    protected enum DhParameterField implements EnumType
    {
        P, 
        G, 
        Q;
        
        @Override
        public int getValue() {
            return this.ordinal();
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
}
