// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;

public class EncKrbPrivPart extends KrbAppSequenceType
{
    public static final int TAG = 28;
    static Asn1FieldInfo[] fieldInfos;
    
    public EncKrbPrivPart() {
        super(28, EncKrbPrivPart.fieldInfos);
    }
    
    public byte[] getUserData() {
        return this.getFieldAsOctets(EncKrbPrivPartField.USER_DATA);
    }
    
    public void setUserData(final byte[] userData) {
        this.setFieldAsOctets(EncKrbPrivPartField.USER_DATA, userData);
    }
    
    public KerberosTime getTimeStamp() {
        return this.getFieldAsTime(EncKrbPrivPartField.TIMESTAMP);
    }
    
    public void setTimeStamp(final KerberosTime timeStamp) {
        this.setFieldAs(EncKrbPrivPartField.TIMESTAMP, timeStamp);
    }
    
    public int getUsec() {
        return this.getFieldAsInt(EncKrbPrivPartField.USEC);
    }
    
    public void setUsec(final int usec) {
        this.setFieldAsInt(EncKrbPrivPartField.USEC, usec);
    }
    
    public int getSeqNumber() {
        return this.getFieldAsInt(EncKrbPrivPartField.SEQ_NUMBER);
    }
    
    public void setSeqNumber(final int seqNumber) {
        this.setFieldAsInt(EncKrbPrivPartField.SEQ_NUMBER, seqNumber);
    }
    
    public HostAddress getSAddress() {
        return this.getFieldAs(EncKrbPrivPartField.S_ADDRESS, HostAddress.class);
    }
    
    public void setSAddress(final HostAddress hostAddress) {
        this.setFieldAs(EncKrbPrivPartField.S_ADDRESS, hostAddress);
    }
    
    public HostAddress getRAddress() {
        return this.getFieldAs(EncKrbPrivPartField.R_ADDRESS, HostAddress.class);
    }
    
    public void setRAddress(final HostAddress hostAddress) {
        this.setFieldAs(EncKrbPrivPartField.R_ADDRESS, hostAddress);
    }
    
    static {
        EncKrbPrivPart.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncKrbPrivPartField.USER_DATA, Asn1OctetString.class), new ExplicitField(EncKrbPrivPartField.TIMESTAMP, KerberosTime.class), new ExplicitField(EncKrbPrivPartField.USEC, Asn1Integer.class), new ExplicitField(EncKrbPrivPartField.SEQ_NUMBER, Asn1Integer.class), new ExplicitField(EncKrbPrivPartField.S_ADDRESS, HostAddress.class), new ExplicitField(EncKrbPrivPartField.R_ADDRESS, HostAddress.class) };
    }
    
    protected enum EncKrbPrivPartField implements EnumType
    {
        USER_DATA, 
        TIMESTAMP, 
        USEC, 
        SEQ_NUMBER, 
        S_ADDRESS, 
        R_ADDRESS;
        
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
