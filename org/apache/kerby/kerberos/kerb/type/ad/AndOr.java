// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ad;

import org.apache.kerby.asn1.ExplicitField;
import java.math.BigInteger;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class AndOr extends KrbSequenceType
{
    private static Asn1FieldInfo[] fieldInfos;
    
    public AndOr() {
        super(AndOr.fieldInfos);
    }
    
    public AndOr(final int conditionCount, final AuthorizationData authzData) {
        super(AndOr.fieldInfos);
        this.setFieldAs(AndOrField.AndOr_ConditionCount, new Asn1Integer(Integer.valueOf(conditionCount)));
        this.setFieldAs(AndOrField.AndOr_Elements, authzData);
    }
    
    public int getConditionCount() {
        return this.getFieldAs(AndOrField.AndOr_ConditionCount, Asn1Integer.class).getValue().intValue();
    }
    
    public void setConditionCount(final int conditionCount) {
        this.setFieldAs(AndOrField.AndOr_ConditionCount, new Asn1Integer(Integer.valueOf(conditionCount)));
    }
    
    public AuthorizationData getAuthzData() {
        return this.getFieldAs(AndOrField.AndOr_Elements, AuthorizationData.class);
    }
    
    public void setAuthzData(final AuthorizationData authzData) {
        this.setFieldAs(AndOrField.AndOr_Elements, authzData);
    }
    
    static {
        AndOr.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(AndOrField.AndOr_ConditionCount, Asn1Integer.class), new ExplicitField(AndOrField.AndOr_Elements, AuthorizationData.class) };
    }
    
    protected enum AndOrField implements EnumType
    {
        AndOr_ConditionCount, 
        AndOr_Elements;
        
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
