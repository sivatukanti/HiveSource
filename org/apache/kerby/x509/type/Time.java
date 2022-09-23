// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1GeneralizedTime;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1UtcTime;
import java.util.Date;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class Time extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public Time() {
        super(Time.fieldInfos);
    }
    
    public Date getUtcTime() {
        return this.getChoiceValueAs(TimeField.UTC_TIME, Asn1UtcTime.class).getValue();
    }
    
    public void setUtcTime(final Asn1UtcTime utcTime) {
        this.setChoiceValue(TimeField.UTC_TIME, utcTime);
    }
    
    public Date generalizedTime() {
        return this.getChoiceValueAs(TimeField.GENERAL_TIME, Asn1GeneralizedTime.class).getValue();
    }
    
    public void setGeneralTime(final Asn1GeneralizedTime generalTime) {
        this.setChoiceValue(TimeField.GENERAL_TIME, generalTime);
    }
    
    static {
        Time.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(TimeField.UTC_TIME, Asn1UtcTime.class), new Asn1FieldInfo(TimeField.GENERAL_TIME, Asn1GeneralizedTime.class) };
    }
    
    protected enum TimeField implements EnumType
    {
        UTC_TIME, 
        GENERAL_TIME;
        
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
