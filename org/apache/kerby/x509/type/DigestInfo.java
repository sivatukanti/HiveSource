// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1OctetString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class DigestInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public DigestInfo() {
        super(DigestInfo.fieldInfos);
    }
    
    public AlgorithmIdentifier getAlgorithmId() {
        return this.getFieldAs(DigestInfoField.DIGEST_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setDigestAlgorithm(final AlgorithmIdentifier digestAlgorithm) {
        this.setFieldAs(DigestInfoField.DIGEST_ALGORITHM, digestAlgorithm);
    }
    
    public byte[] getDigest() {
        return this.getFieldAsOctets(DigestInfoField.DIGEST);
    }
    
    public void setDigest(final byte[] digest) {
        this.setFieldAsOctets(DigestInfoField.DIGEST, digest);
    }
    
    static {
        DigestInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(DigestInfoField.DIGEST_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(DigestInfoField.DIGEST, Asn1OctetString.class) };
    }
    
    protected enum DigestInfoField implements EnumType
    {
        DIGEST_ALGORITHM, 
        DIGEST;
        
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
