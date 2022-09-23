// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AccessDescription extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AccessDescription() {
        super(AccessDescription.fieldInfos);
    }
    
    public Asn1ObjectIdentifier getAccessMethod() {
        return this.getFieldAs(AccessDescriptionField.ACCESS_METHOD, Asn1ObjectIdentifier.class);
    }
    
    public void setAccessMethod(final Asn1ObjectIdentifier accessMethod) {
        this.setFieldAs(AccessDescriptionField.ACCESS_METHOD, accessMethod);
    }
    
    public GeneralName getAccessLocation() {
        return this.getFieldAs(AccessDescriptionField.ACCESS_LOCATION, GeneralName.class);
    }
    
    public void setAccessLocation(final GeneralName accessLocation) {
        this.setFieldAs(AccessDescriptionField.ACCESS_LOCATION, accessLocation);
    }
    
    static {
        AccessDescription.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AccessDescriptionField.ACCESS_METHOD, Asn1ObjectIdentifier.class), new Asn1FieldInfo(AccessDescriptionField.ACCESS_LOCATION, GeneralName.class) };
    }
    
    protected enum AccessDescriptionField implements EnumType
    {
        ACCESS_METHOD, 
        ACCESS_LOCATION;
        
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
