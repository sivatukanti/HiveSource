// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.x500.type.Name;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Integer;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class TBSCertList extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public TBSCertList() {
        super(TBSCertList.fieldInfos);
    }
    
    public Asn1Integer getVersion() {
        return this.getFieldAs(TBSCertListField.VERSION, Asn1Integer.class);
    }
    
    public void setVersion(final Asn1Integer version) {
        this.setFieldAs(TBSCertListField.VERSION, version);
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.getFieldAs(TBSCertListField.SIGNATURE, AlgorithmIdentifier.class);
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.setFieldAs(TBSCertListField.SIGNATURE, signature);
    }
    
    public Name getIssuer() {
        return this.getFieldAs(TBSCertListField.ISSUER, Name.class);
    }
    
    public void setIssuer(final Name issuer) {
        this.setFieldAs(TBSCertListField.ISSUER, issuer);
    }
    
    public Time getThisUpdate() {
        return this.getFieldAs(TBSCertListField.THIS_UPDATA, Time.class);
    }
    
    public void setThisUpdata(final Time thisUpdata) {
        this.setFieldAs(TBSCertListField.THIS_UPDATA, thisUpdata);
    }
    
    public Time getNextUpdate() {
        return this.getFieldAs(TBSCertListField.NEXT_UPDATE, Time.class);
    }
    
    public void setNextUpdate(final Time nextUpdate) {
        this.setFieldAs(TBSCertListField.NEXT_UPDATE, nextUpdate);
    }
    
    public RevokedCertificates getRevokedCertificates() {
        return this.getFieldAs(TBSCertListField.REVOKED_CERTIFICATES, RevokedCertificates.class);
    }
    
    public void setRevokedCertificates(final RevokedCertificates revokedCertificates) {
        this.setFieldAs(TBSCertListField.REVOKED_CERTIFICATES, revokedCertificates);
    }
    
    public Extensions getCrlExtensions() {
        return this.getFieldAs(TBSCertListField.CRL_EXTENSIONS, Extensions.class);
    }
    
    public void setCrlExtensions(final Extensions crlExtensions) {
        this.setFieldAs(TBSCertListField.CRL_EXTENSIONS, crlExtensions);
    }
    
    static {
        TBSCertList.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(TBSCertListField.VERSION, Asn1Integer.class), new Asn1FieldInfo(TBSCertListField.SIGNATURE, AlgorithmIdentifier.class), new Asn1FieldInfo(TBSCertListField.ISSUER, Name.class), new Asn1FieldInfo(TBSCertListField.THIS_UPDATA, Time.class), new Asn1FieldInfo(TBSCertListField.NEXT_UPDATE, Time.class), new Asn1FieldInfo(TBSCertListField.REVOKED_CERTIFICATES, RevokedCertificates.class), new ExplicitField(TBSCertListField.CRL_EXTENSIONS, 0, Extensions.class) };
    }
    
    protected enum TBSCertListField implements EnumType
    {
        VERSION, 
        SIGNATURE, 
        ISSUER, 
        THIS_UPDATA, 
        NEXT_UPDATE, 
        REVOKED_CERTIFICATES, 
        CRL_EXTENSIONS;
        
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
