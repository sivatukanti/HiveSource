// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class DhRepInfo extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DhRepInfo() {
        super(DhRepInfo.fieldInfos);
    }
    
    public byte[] getDHSignedData() {
        return this.getFieldAsOctets(DhRepInfoField.DH_SIGNED_DATA);
    }
    
    public void setDHSignedData(final byte[] dhSignedData) {
        this.setFieldAsOctets(DhRepInfoField.DH_SIGNED_DATA, dhSignedData);
    }
    
    public DhNonce getServerDhNonce() {
        return this.getFieldAs(DhRepInfoField.SERVER_DH_NONCE, DhNonce.class);
    }
    
    public void setServerDhNonce(final DhNonce dhNonce) {
        this.setFieldAs(DhRepInfoField.SERVER_DH_NONCE, dhNonce);
    }
    
    public KdfAlgorithmId getKdfId() {
        return this.getFieldAs(DhRepInfoField.KDF_ID, KdfAlgorithmId.class);
    }
    
    public void setKdfId(final KdfAlgorithmId kdfId) {
        this.setFieldAs(DhRepInfoField.KDF_ID, kdfId);
    }
    
    static {
        DhRepInfo.fieldInfos = new Asn1FieldInfo[] { new ImplicitField(DhRepInfoField.DH_SIGNED_DATA, Asn1OctetString.class), new ExplicitField(DhRepInfoField.SERVER_DH_NONCE, DhNonce.class), new ExplicitField(DhRepInfoField.KDF_ID, KdfAlgorithmId.class) };
    }
    
    protected enum DhRepInfoField implements EnumType
    {
        DH_SIGNED_DATA, 
        SERVER_DH_NONCE, 
        KDF_ID;
        
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
