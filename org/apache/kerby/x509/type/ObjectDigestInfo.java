// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class ObjectDigestInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public ObjectDigestInfo() {
        super(ObjectDigestInfo.fieldInfos);
    }
    
    public DigestedObjectType getDigestedObjectType() {
        return this.getFieldAs(ODInfoField.DIGESTED_OBJECT_TYPE, DigestedObjectType.class);
    }
    
    public void setDigestedObjectType(final DigestedObjectType digestedObjectType) {
        this.setFieldAs(ODInfoField.DIGESTED_OBJECT_TYPE, digestedObjectType);
    }
    
    public Asn1ObjectIdentifier getOtherObjectTypeID() {
        return this.getFieldAs(ODInfoField.OTHER_OBJECT_TYPE_ID, Asn1ObjectIdentifier.class);
    }
    
    public void setOtherObjectTypeId(final Asn1ObjectIdentifier otherObjectTypeID) {
        this.setFieldAs(ODInfoField.OTHER_OBJECT_TYPE_ID, otherObjectTypeID);
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.getFieldAs(ODInfoField.DIGEST_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setDigestAlgorithm(final AlgorithmIdentifier digestAlgorithm) {
        this.setFieldAs(ODInfoField.DIGEST_ALGORITHM, digestAlgorithm);
    }
    
    public Asn1BitString getObjectDigest() {
        return this.getFieldAs(ODInfoField.OBJECT_DIGEST, Asn1BitString.class);
    }
    
    public void setObjectDigest(final Asn1BitString objectDigest) {
        this.setFieldAs(ODInfoField.OBJECT_DIGEST, objectDigest);
    }
    
    static {
        ObjectDigestInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(ODInfoField.DIGESTED_OBJECT_TYPE, DigestedObjectType.class), new Asn1FieldInfo(ODInfoField.OTHER_OBJECT_TYPE_ID, Asn1ObjectIdentifier.class), new Asn1FieldInfo(ODInfoField.DIGEST_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(ODInfoField.OBJECT_DIGEST, Asn1BitString.class) };
    }
    
    protected enum ODInfoField implements EnumType
    {
        DIGESTED_OBJECT_TYPE, 
        OTHER_OBJECT_TYPE_ID, 
        DIGEST_ALGORITHM, 
        OBJECT_DIGEST;
        
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
