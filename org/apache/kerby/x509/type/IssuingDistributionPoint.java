// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1Boolean;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class IssuingDistributionPoint extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public IssuingDistributionPoint() {
        super(IssuingDistributionPoint.fieldInfos);
    }
    
    public DistributionPointName getDistributionPoint() {
        return this.getFieldAs(IDPointField.DISTRIBUTION_POINT, DistributionPointName.class);
    }
    
    public void setDistributionPoint(final DistributionPointName distributionPoint) {
        this.setFieldAs(IDPointField.DISTRIBUTION_POINT, distributionPoint);
    }
    
    public boolean getOnlyContainsUserCerts() {
        return this.getFieldAs(IDPointField.ONLY_CONTAINS_USER_CERTS, Asn1Boolean.class).getValue();
    }
    
    public void setOnlyContainsUserCerts(final boolean onlyContainsUserCerts) {
        this.setFieldAs(IDPointField.ONLY_CONTAINS_USER_CERTS, new Asn1Boolean(onlyContainsUserCerts));
    }
    
    public boolean getOnlyContainsCACerts() {
        return this.getFieldAs(IDPointField.ONLY_CONTAINS_CA_CERTS, Asn1Boolean.class).getValue();
    }
    
    public void setOnlyContainsCaCerts(final boolean onlyContainsCaCerts) {
        this.setFieldAs(IDPointField.ONLY_CONTAINS_CA_CERTS, new Asn1Boolean(onlyContainsCaCerts));
    }
    
    public ReasonFlags getOnlySomeReasons() {
        return this.getFieldAs(IDPointField.ONLY_SOME_REASONS, ReasonFlags.class);
    }
    
    public void setOnlySomeReasons(final ReasonFlags onlySomeReasons) {
        this.setFieldAs(IDPointField.ONLY_SOME_REASONS, onlySomeReasons);
    }
    
    public boolean getIndirectCRL() {
        return this.getFieldAs(IDPointField.INDIRECT_CRL, Asn1Boolean.class).getValue();
    }
    
    public void setIndirectCrl(final boolean indirectCrl) {
        this.setFieldAs(IDPointField.INDIRECT_CRL, new Asn1Boolean(indirectCrl));
    }
    
    public boolean getOnlyContainsAttributeCerts() {
        return this.getFieldAs(IDPointField.ONLY_CONTAINS_ATTRIBUTE_CERTS, Asn1Boolean.class).getValue();
    }
    
    public void setOnlyContainsAttributeCerts(final boolean onlyContainsAttributeCerts) {
        this.setFieldAs(IDPointField.ONLY_CONTAINS_ATTRIBUTE_CERTS, new Asn1Boolean(onlyContainsAttributeCerts));
    }
    
    static {
        IssuingDistributionPoint.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(IDPointField.DISTRIBUTION_POINT, DistributionPointName.class), new ExplicitField(IDPointField.ONLY_CONTAINS_USER_CERTS, Asn1Boolean.class), new ExplicitField(IDPointField.ONLY_CONTAINS_CA_CERTS, Asn1Boolean.class), new ExplicitField(IDPointField.ONLY_SOME_REASONS, ReasonFlags.class), new ExplicitField(IDPointField.INDIRECT_CRL, Asn1Boolean.class), new ExplicitField(IDPointField.ONLY_CONTAINS_ATTRIBUTE_CERTS, Asn1Boolean.class) };
    }
    
    protected enum IDPointField implements EnumType
    {
        DISTRIBUTION_POINT, 
        ONLY_CONTAINS_USER_CERTS, 
        ONLY_CONTAINS_CA_CERTS, 
        ONLY_SOME_REASONS, 
        INDIRECT_CRL, 
        ONLY_CONTAINS_ATTRIBUTE_CERTS;
        
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
