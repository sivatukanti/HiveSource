// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.x509.type.SubjectPublicKeyInfo;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class AuthPack extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AuthPack() {
        super(AuthPack.fieldInfos);
    }
    
    public PkAuthenticator getPkAuthenticator() {
        return this.getFieldAs(AuthPackField.PK_AUTHENTICATOR, PkAuthenticator.class);
    }
    
    public void setPkAuthenticator(final PkAuthenticator pkAuthenticator) {
        this.setFieldAs(AuthPackField.PK_AUTHENTICATOR, pkAuthenticator);
    }
    
    public SubjectPublicKeyInfo getClientPublicValue() {
        return this.getFieldAs(AuthPackField.CLIENT_PUBLIC_VALUE, SubjectPublicKeyInfo.class);
    }
    
    public void setClientPublicValue(final SubjectPublicKeyInfo clientPublicValue) {
        this.setFieldAs(AuthPackField.CLIENT_PUBLIC_VALUE, clientPublicValue);
    }
    
    public AlgorithmIdentifiers getsupportedCmsTypes() {
        return this.getFieldAs(AuthPackField.SUPPORTED_CMS_TYPES, AlgorithmIdentifiers.class);
    }
    
    public void setsupportedCmsTypes(final AlgorithmIdentifiers supportedCMSTypes) {
        this.setFieldAs(AuthPackField.SUPPORTED_CMS_TYPES, supportedCMSTypes);
    }
    
    public DhNonce getClientDhNonce() {
        return this.getFieldAs(AuthPackField.CLIENT_DH_NONCE, DhNonce.class);
    }
    
    public void setClientDhNonce(final DhNonce dhNonce) {
        this.setFieldAs(AuthPackField.CLIENT_DH_NONCE, dhNonce);
    }
    
    public SupportedKdfs getsupportedKDFs() {
        return this.getFieldAs(AuthPackField.SUPPORTED_KDFS, SupportedKdfs.class);
    }
    
    public void setsupportedKDFs(final SupportedKdfs supportedKdfs) {
        this.setFieldAs(AuthPackField.SUPPORTED_KDFS, supportedKdfs);
    }
    
    static {
        AuthPack.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(AuthPackField.PK_AUTHENTICATOR, PkAuthenticator.class), new ExplicitField(AuthPackField.CLIENT_PUBLIC_VALUE, SubjectPublicKeyInfo.class), new ExplicitField(AuthPackField.SUPPORTED_CMS_TYPES, AlgorithmIdentifiers.class), new ExplicitField(AuthPackField.CLIENT_DH_NONCE, DhNonce.class), new ExplicitField(AuthPackField.SUPPORTED_KDFS, SupportedKdfs.class) };
    }
    
    protected enum AuthPackField implements EnumType
    {
        PK_AUTHENTICATOR, 
        CLIENT_PUBLIC_VALUE, 
        SUPPORTED_CMS_TYPES, 
        CLIENT_DH_NONCE, 
        SUPPORTED_KDFS;
        
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
