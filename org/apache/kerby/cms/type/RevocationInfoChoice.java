// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.ImplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.CertificateList;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1Choice;

public class RevocationInfoChoice extends Asn1Choice
{
    static Asn1FieldInfo[] fieldInfos;
    
    public RevocationInfoChoice() {
        super(RevocationInfoChoice.fieldInfos);
    }
    
    public CertificateList getCRL() {
        return this.getChoiceValueAs(RevocationInfoChoiceField.CRL, CertificateList.class);
    }
    
    public void setCRL(final CertificateList crl) {
        this.setChoiceValue(RevocationInfoChoiceField.CRL, crl);
    }
    
    public OtherRevocationInfoFormat getOther() {
        return this.getChoiceValueAs(RevocationInfoChoiceField.OTHER, OtherRevocationInfoFormat.class);
    }
    
    public void setOther(final OtherRevocationInfoFormat other) {
        this.setChoiceValue(RevocationInfoChoiceField.OTHER, other);
    }
    
    static {
        RevocationInfoChoice.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(RevocationInfoChoiceField.CRL, CertificateList.class), new ImplicitField(RevocationInfoChoiceField.OTHER, OtherRevocationInfoFormat.class) };
    }
    
    protected enum RevocationInfoChoiceField implements EnumType
    {
        CRL, 
        OTHER;
        
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
