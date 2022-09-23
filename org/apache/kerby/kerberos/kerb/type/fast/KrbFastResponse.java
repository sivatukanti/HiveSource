// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KrbFastResponse extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KrbFastResponse() {
        super(KrbFastResponse.fieldInfos);
    }
    
    public PaData getPaData() {
        return this.getFieldAs(KrbFastResponseField.PADATA, PaData.class);
    }
    
    public void setPaData(final PaData paData) {
        this.setFieldAs(KrbFastResponseField.PADATA, paData);
    }
    
    public EncryptionKey getStrengthenKey() {
        return this.getFieldAs(KrbFastResponseField.STRENGTHEN_KEY, EncryptionKey.class);
    }
    
    public void setStrengthenKey(final EncryptionKey strengthenKey) {
        this.setFieldAs(KrbFastResponseField.STRENGTHEN_KEY, strengthenKey);
    }
    
    public KrbFastFinished getFastFinished() {
        return this.getFieldAs(KrbFastResponseField.FINISHED, KrbFastFinished.class);
    }
    
    public void setFastFinished(final KrbFastFinished fastFinished) {
        this.setFieldAs(KrbFastResponseField.FINISHED, fastFinished);
    }
    
    public int getNonce() {
        return this.getFieldAsInt(KrbFastResponseField.NONCE);
    }
    
    public void setNonce(final int nonce) {
        this.setFieldAsInt(KrbFastResponseField.NONCE, nonce);
    }
    
    static {
        KrbFastResponse.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KrbFastResponseField.PADATA, PaData.class), new ExplicitField(KrbFastResponseField.STRENGTHEN_KEY, EncryptionKey.class), new ExplicitField(KrbFastResponseField.FINISHED, KrbFastFinished.class), new ExplicitField(KrbFastResponseField.NONCE, Asn1Integer.class) };
    }
    
    protected enum KrbFastResponseField implements EnumType
    {
        PADATA, 
        STRENGTHEN_KEY, 
        FINISHED, 
        NONCE;
        
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
