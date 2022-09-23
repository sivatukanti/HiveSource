// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Integer;
import java.math.BigInteger;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Boolean;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class BasicConstraints extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public BasicConstraints() {
        super(BasicConstraints.fieldInfos);
    }
    
    public boolean isCA() {
        return false;
    }
    
    public boolean getCA() {
        return this.getFieldAs(BasicConstraintsField.CA, Asn1Boolean.class).getValue();
    }
    
    public void setCA(final Asn1Boolean isCA) {
        this.setFieldAs(BasicConstraintsField.CA, isCA);
    }
    
    public BigInteger getPathLenConstraint() {
        return this.getFieldAs(BasicConstraintsField.PATH_LEN_CONSTRAINT, Asn1Integer.class).getValue();
    }
    
    public void setPathLenConstraint(final Asn1Integer pathLenConstraint) {
        this.setFieldAs(BasicConstraintsField.PATH_LEN_CONSTRAINT, pathLenConstraint);
    }
    
    static {
        BasicConstraints.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(BasicConstraintsField.CA, Asn1Boolean.class), new Asn1FieldInfo(BasicConstraintsField.PATH_LEN_CONSTRAINT, Asn1Integer.class) };
    }
    
    protected enum BasicConstraintsField implements EnumType
    {
        CA, 
        PATH_LEN_CONSTRAINT;
        
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
