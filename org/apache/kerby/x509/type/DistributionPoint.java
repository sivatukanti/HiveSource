// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class DistributionPoint extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DistributionPoint() {
        super(DistributionPoint.fieldInfos);
    }
    
    public DistributionPointName getDistributionPoint() {
        return this.getFieldAs(DistributionPointField.DISTRIBUTION_POINT, DistributionPointName.class);
    }
    
    public void setDistributionPoint(final DistributionPointName distributionPoint) {
        this.setFieldAs(DistributionPointField.DISTRIBUTION_POINT, distributionPoint);
    }
    
    public ReasonFlags getReasons() {
        return this.getFieldAs(DistributionPointField.REASONS, ReasonFlags.class);
    }
    
    public void setReasons(final ReasonFlags reasons) {
        this.setFieldAs(DistributionPointField.REASONS, reasons);
    }
    
    public GeneralNames getCRLIssuer() {
        return this.getFieldAs(DistributionPointField.CRL_ISSUER, GeneralNames.class);
    }
    
    public void setCRLIssuer(final GeneralNames crlIssuer) {
        this.setFieldAs(DistributionPointField.CRL_ISSUER, crlIssuer);
    }
    
    static {
        DistributionPoint.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(DistributionPointField.DISTRIBUTION_POINT, DistributionPointName.class), new ExplicitField(DistributionPointField.REASONS, ReasonFlags.class), new ExplicitField(DistributionPointField.CRL_ISSUER, GeneralNames.class) };
    }
    
    protected enum DistributionPointField implements EnumType
    {
        DISTRIBUTION_POINT, 
        REASONS, 
        CRL_ISSUER;
        
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
