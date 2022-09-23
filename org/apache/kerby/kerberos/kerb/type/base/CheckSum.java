// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Integer;
import java.util.Arrays;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class CheckSum extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public CheckSum() {
        super(CheckSum.fieldInfos);
    }
    
    public CheckSum(final CheckSumType cksumType, final byte[] checksum) {
        super(CheckSum.fieldInfos);
        this.setCksumtype(cksumType);
        this.setChecksum(checksum);
    }
    
    public CheckSum(final int cksumType, final byte[] checksum) {
        this(CheckSumType.fromValue(cksumType), checksum);
    }
    
    public CheckSumType getCksumtype() {
        final Integer value = this.getFieldAsInteger(CheckSumField.CKSUM_TYPE);
        return CheckSumType.fromValue(value);
    }
    
    public void setCksumtype(final CheckSumType cksumtype) {
        this.setFieldAsInt(CheckSumField.CKSUM_TYPE, cksumtype.getValue());
    }
    
    public byte[] getChecksum() {
        return this.getFieldAsOctets(CheckSumField.CHECK_SUM);
    }
    
    public void setChecksum(final byte[] checksum) {
        this.setFieldAsOctets(CheckSumField.CHECK_SUM, checksum);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CheckSum)) {
            return false;
        }
        final CheckSum that = (CheckSum)other;
        return this.getCksumtype() == that.getCksumtype() && Arrays.equals(this.getChecksum(), that.getChecksum());
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (this.getCksumtype() != null) {
            result = 31 * result + this.getCksumtype().hashCode();
        }
        if (this.getChecksum() != null) {
            result = 31 * result + Arrays.hashCode(this.getChecksum());
        }
        return result;
    }
    
    public boolean isEqual(final CheckSum other) {
        return this.equals(other);
    }
    
    public boolean isEqual(final byte[] cksumBytes) {
        return Arrays.equals(this.getChecksum(), cksumBytes);
    }
    
    static {
        CheckSum.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(CheckSumField.CKSUM_TYPE, Asn1Integer.class), new ExplicitField(CheckSumField.CHECK_SUM, Asn1OctetString.class) };
    }
    
    protected enum CheckSumField implements EnumType
    {
        CKSUM_TYPE, 
        CHECK_SUM;
        
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
