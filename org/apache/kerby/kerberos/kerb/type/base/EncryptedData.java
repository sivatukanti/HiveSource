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

public class EncryptedData extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public EncryptedData() {
        super(EncryptedData.fieldInfos);
    }
    
    public EncryptionType getEType() {
        final Integer value = this.getFieldAsInteger(EncryptedDataField.ETYPE);
        return EncryptionType.fromValue(value);
    }
    
    public void setEType(final EncryptionType eType) {
        this.setFieldAsInt(EncryptedDataField.ETYPE, eType.getValue());
    }
    
    public int getKvno() {
        final Integer value = this.getFieldAsInteger(EncryptedDataField.KVNO);
        if (value != null) {
            return value;
        }
        return -1;
    }
    
    public void setKvno(final int kvno) {
        this.setFieldAsInt(EncryptedDataField.KVNO, kvno);
    }
    
    public byte[] getCipher() {
        return this.getFieldAsOctets(EncryptedDataField.CIPHER);
    }
    
    public void setCipher(final byte[] cipher) {
        this.setFieldAsOctets(EncryptedDataField.CIPHER, cipher);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EncryptedData)) {
            return false;
        }
        final EncryptedData that = (EncryptedData)o;
        return this.getEType() == that.getEType() && Arrays.equals(this.getCipher(), that.getCipher());
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (this.getEType() != null) {
            result = 31 * result + this.getEType().hashCode();
        }
        if (this.getCipher() != null) {
            result = 31 * result + Arrays.hashCode(this.getCipher());
        }
        return result;
    }
    
    static {
        EncryptedData.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncryptedDataField.ETYPE, Asn1Integer.class), new ExplicitField(EncryptedDataField.KVNO, Asn1Integer.class), new ExplicitField(EncryptedDataField.CIPHER, Asn1OctetString.class) };
    }
    
    protected enum EncryptedDataField implements EnumType
    {
        ETYPE, 
        KVNO, 
        CIPHER;
        
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
