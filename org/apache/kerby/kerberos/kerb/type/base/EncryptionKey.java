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

public class EncryptionKey extends KrbSequenceType
{
    private int kvno;
    static Asn1FieldInfo[] fieldInfos;
    
    public EncryptionKey() {
        super(EncryptionKey.fieldInfos);
        this.kvno = -1;
    }
    
    public EncryptionKey(final int keyType, final byte[] keyData) {
        this(keyType, keyData, -1);
    }
    
    public EncryptionKey(final int keyType, final byte[] keyData, final int kvno) {
        this(EncryptionType.fromValue(keyType), keyData, kvno);
    }
    
    public EncryptionKey(final EncryptionType keyType, final byte[] keyData) {
        this(keyType, keyData, -1);
    }
    
    public EncryptionKey(final EncryptionType keyType, final byte[] keyData, final int kvno) {
        this();
        this.setKeyType(keyType);
        this.setKeyData(keyData);
        this.setKvno(kvno);
    }
    
    public EncryptionType getKeyType() {
        final Integer value = this.getFieldAsInteger(EncryptionKeyField.KEY_TYPE);
        return EncryptionType.fromValue(value);
    }
    
    public void setKeyType(final EncryptionType keyType) {
        this.setFieldAsInt(EncryptionKeyField.KEY_TYPE, keyType.getValue());
    }
    
    public byte[] getKeyData() {
        return this.getFieldAsOctets(EncryptionKeyField.KEY_VALUE);
    }
    
    public void setKeyData(final byte[] keyData) {
        this.setFieldAsOctets(EncryptionKeyField.KEY_VALUE, keyData);
    }
    
    public void setKvno(final int kvno) {
        this.kvno = kvno;
    }
    
    public int getKvno() {
        return this.kvno;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EncryptionKey that = (EncryptionKey)o;
        return (this.kvno == -1 || that.kvno == -1 || this.kvno == that.kvno) && this.getKeyType() == that.getKeyType() && Arrays.equals(this.getKeyData(), that.getKeyData());
    }
    
    @Override
    public int hashCode() {
        int result = this.kvno;
        if (this.getKeyType() != null) {
            result = 31 * result + this.getKeyType().hashCode();
        }
        if (this.getKeyData() != null) {
            result = 31 * result + Arrays.hashCode(this.getKeyData());
        }
        return result;
    }
    
    static {
        EncryptionKey.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(EncryptionKeyField.KEY_TYPE, Asn1Integer.class), new ExplicitField(EncryptionKeyField.KEY_VALUE, Asn1OctetString.class) };
    }
    
    protected enum EncryptionKeyField implements EnumType
    {
        KEY_TYPE, 
        KEY_VALUE;
        
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
