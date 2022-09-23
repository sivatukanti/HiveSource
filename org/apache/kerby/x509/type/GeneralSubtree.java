// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class GeneralSubtree extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public GeneralSubtree() {
        super(GeneralSubtree.fieldInfos);
    }
    
    public GeneralName getBase() {
        return this.getFieldAs(GeneralSubtreeField.BASE, GeneralName.class);
    }
    
    public void setBase(final GeneralName base) {
        this.setFieldAs(GeneralSubtreeField.BASE, base);
    }
    
    public int getMinimum() {
        return this.getFieldAsInteger(GeneralSubtreeField.MINIMUM);
    }
    
    public void setMinimum(final int minimum) {
        this.setFieldAsInt(GeneralSubtreeField.MINIMUM, minimum);
    }
    
    public int getMaximum() {
        return this.getFieldAsInteger(GeneralSubtreeField.MAXMUM);
    }
    
    public void setMaxmum(final int maxmum) {
        this.setFieldAsInt(GeneralSubtreeField.MAXMUM, maxmum);
    }
    
    static {
        GeneralSubtree.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(GeneralSubtreeField.BASE, GeneralName.class), new ExplicitField(GeneralSubtreeField.MINIMUM, 0, Asn1Integer.class), new ExplicitField(GeneralSubtreeField.MAXMUM, 1, Asn1Integer.class) };
    }
    
    protected enum GeneralSubtreeField implements EnumType
    {
        BASE, 
        MINIMUM, 
        MAXMUM;
        
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
