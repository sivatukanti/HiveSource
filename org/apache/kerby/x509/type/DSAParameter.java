// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Integer;
import java.math.BigInteger;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class DSAParameter extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DSAParameter() {
        super(DSAParameter.fieldInfos);
    }
    
    public BigInteger getP() {
        return this.getFieldAs(DSAParameterField.P, Asn1Integer.class).getValue();
    }
    
    public void setP(final BigInteger p) {
        this.setFieldAs(DSAParameterField.P, new Asn1Integer(p));
    }
    
    public BigInteger getQ() {
        return this.getFieldAs(DSAParameterField.Q, Asn1Integer.class).getValue();
    }
    
    public void setQ(final BigInteger q) {
        this.setFieldAs(DSAParameterField.Q, new Asn1Integer(q));
    }
    
    public BigInteger getG() {
        return this.getFieldAs(DSAParameterField.G, Asn1Integer.class).getValue();
    }
    
    public void setG(final BigInteger g) {
        this.setFieldAs(DSAParameterField.G, new Asn1Integer(g));
    }
    
    static {
        DSAParameter.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(DSAParameterField.P, Asn1Integer.class), new Asn1FieldInfo(DSAParameterField.Q, Asn1Integer.class), new Asn1FieldInfo(DSAParameterField.G, Asn1Integer.class) };
    }
    
    protected enum DSAParameterField implements EnumType
    {
        P, 
        Q, 
        G;
        
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
