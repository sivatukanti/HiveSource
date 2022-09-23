// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class Target extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Target() {
        super(Target.fieldInfos);
    }
    
    public GeneralName getTargetName() {
        return this.getChoiceValueAs(TargetField.TARGET_NAME, GeneralName.class);
    }
    
    public void setTargetName(final GeneralName targetName) {
        this.setChoiceValue(TargetField.TARGET_NAME, targetName);
    }
    
    public GeneralName getTargetGroup() {
        return this.getChoiceValueAs(TargetField.TARGET_GROUP, GeneralName.class);
    }
    
    public void setTargetGroup(final GeneralName targetGroup) {
        this.setChoiceValue(TargetField.TARGET_GROUP, targetGroup);
    }
    
    public TargetCert targetCert() {
        return this.getChoiceValueAs(TargetField.TARGET_CERT, TargetCert.class);
    }
    
    public void setTargetCert(final TargetCert targetCert) {
        this.setChoiceValue(TargetField.TARGET_CERT, targetCert);
    }
    
    static {
        Target.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(TargetField.TARGET_NAME, GeneralName.class), new ExplicitField(TargetField.TARGET_GROUP, GeneralName.class), new ExplicitField(TargetField.TARGET_CERT, TargetCert.class) };
    }
    
    protected enum TargetField implements EnumType
    {
        TARGET_NAME, 
        TARGET_GROUP, 
        TARGET_CERT;
        
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
