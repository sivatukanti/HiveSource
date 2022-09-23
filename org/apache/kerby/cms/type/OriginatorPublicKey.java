// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class OriginatorPublicKey extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public OriginatorPublicKey() {
        super(OriginatorPublicKey.fieldInfos);
    }
    
    public AlgorithmIdentifier getAlgorithm() {
        return this.getFieldAs(OriginatorPublicKeyField.ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setAlgorithm(final AlgorithmIdentifier algorithm) {
        this.setFieldAs(OriginatorPublicKeyField.ALGORITHM, algorithm);
    }
    
    public Asn1BitString getPublicKey() {
        return this.getFieldAs(OriginatorPublicKeyField.PUBLIC_KEY, Asn1BitString.class);
    }
    
    public void setPublicKey(final Asn1BitString publicKey) {
        this.setFieldAs(OriginatorPublicKeyField.PUBLIC_KEY, publicKey);
    }
    
    static {
        OriginatorPublicKey.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(OriginatorPublicKeyField.ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(OriginatorPublicKeyField.PUBLIC_KEY, Asn1BitString.class) };
    }
    
    protected enum OriginatorPublicKeyField implements EnumType
    {
        ALGORITHM, 
        PUBLIC_KEY;
        
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
